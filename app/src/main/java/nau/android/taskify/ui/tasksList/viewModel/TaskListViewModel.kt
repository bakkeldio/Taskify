package nau.android.taskify.ui.tasksList.viewModel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import nau.android.taskify.data.extensions.isItImportant
import nau.android.taskify.data.extensions.isItUrgent
import nau.android.taskify.data.repository.ITaskRepository
import nau.android.taskify.ui.alarm.AlarmScheduler
import nau.android.taskify.ui.alarm.CompleteTask
import nau.android.taskify.ui.eisenhowerMatrix.EisenhowerMatrixQuadrant
import nau.android.taskify.ui.enums.DateEnum
import nau.android.taskify.ui.enums.TaskRepeatInterval
import nau.android.taskify.ui.extensions.Debouncer
import nau.android.taskify.ui.model.Task
import nau.android.taskify.ui.model.TaskWithCategory
import nau.android.taskify.ui.tasksList.CategoryTasksListState
import nau.android.taskify.ui.tasksList.GroupingType
import nau.android.taskify.ui.tasksList.HeaderType
import nau.android.taskify.ui.tasksList.SortingType
import nau.android.taskify.ui.tasksList.TasksListState
import java.util.Calendar
import javax.inject.Inject


@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val taskRepository: ITaskRepository,
    private val alarmScheduler: AlarmScheduler,
    completeTask: CompleteTask,
    private val debouncer: Debouncer
) :
    BaseTaskViewModel(taskRepository, alarmScheduler, completeTask) {


    private val _taskWithCategoriesState: MutableStateFlow<TasksListState> =
        MutableStateFlow(TasksListState.Loading)
    val tasksWithCategoriesState: StateFlow<TasksListState> = _taskWithCategoriesState

    private val _categoryTasksState: MutableStateFlow<CategoryTasksListState> =
        MutableStateFlow(CategoryTasksListState.Loading)
    val categoryTasksState: StateFlow<CategoryTasksListState> = _categoryTasksState


    private val _completedTasks: MutableStateFlow<List<TaskWithCategory>> =
        MutableStateFlow(emptyList())
    val completedTasks: StateFlow<List<TaskWithCategory>> = _completedTasks

    private val _categoryCompletedTasks: MutableStateFlow<List<Task>> =
        MutableStateFlow(emptyList())
    val categoryCompletedTasks: StateFlow<List<Task>> = _categoryCompletedTasks


    private var taskOnDeletion: Task? = null

    fun deleteTask() {
        val task = taskOnDeletion ?: return
        deleteTask(task)
        taskOnDeletion = null
    }

    fun deleteTasks(selectedTasks: Set<Task>) {
        viewModelScope.launch {
            taskRepository.deleteMultipleTasks(selectedTasks.toList())
            selectedTasks.filterNot {
                it.completed
            }.filter {
                it.timeIncluded || it.repeatInterval != TaskRepeatInterval.NONE
            }.forEach {
                alarmScheduler.cancelTaskAlarm(it.id)
            }
        }
    }

    fun completeTasks(selectedTasks: List<Task>) {
        viewModelScope.launch {
            taskRepository.updateTasks(selectedTasks.map {
                it.copy(completed = true)
            })
            selectedTasks.filter {
                it.timeIncluded || it.repeatInterval != TaskRepeatInterval.NONE
            }.forEach {
                alarmScheduler.cancelTaskAlarm(it.id)
            }
        }
    }

    fun moveTasks(selectedTasks: List<Task>, categoryId: Long) {
        viewModelScope.launch {
            taskRepository.updateTasks(selectedTasks.map {
                it.copy(categoryId = categoryId)
            })
        }
    }

    fun getCompletedTasks() {
        viewModelScope.launch {
            taskRepository.getCompletedTasks().collectLatest {
                _completedTasks.value  = it
            }
        }
    }

    fun getEisenhowerQuadrantTasks(
        query: String = "",
        groupingType: GroupingType,
        sortingType: SortingType,
        eisenhowerMatrixQuadrant: EisenhowerMatrixQuadrant
    ) {

        viewModelScope.launch {
            taskRepository.getAllTasksWithCategories(query).map {
                val tasks = when (eisenhowerMatrixQuadrant) {
                    EisenhowerMatrixQuadrant.IMPORTANT_URGENT -> {
                        it.filter { taskWithCategory ->
                            taskWithCategory.task.isItImportant() && taskWithCategory.task.isItUrgent()
                        }
                    }

                    EisenhowerMatrixQuadrant.NOT_URGENT_IMPORTANT -> {
                        it.filter { taskWithCategory ->
                            taskWithCategory.task.isItImportant() && !taskWithCategory.task.isItUrgent()
                        }
                    }

                    EisenhowerMatrixQuadrant.URGENT_UNIMPORTANT -> {
                        it.filter { taskWithCategory ->
                            taskWithCategory.task.isItUrgent() && !taskWithCategory.task.isItImportant()
                        }
                    }

                    EisenhowerMatrixQuadrant.NOT_URGENT_UNIMPORTANT -> {
                        it.filter { taskWithCategory ->
                            !taskWithCategory.task.isItImportant() && !taskWithCategory.task.isItUrgent()
                        }
                    }
                }
                val groupedTasks =
                    if (tasks.isNotEmpty()) groupTasks(tasks, groupingType) else emptyMap()
                sortTasksListWithCategories(sortingType, groupedTasks)
            }.catch {
                _taskWithCategoriesState.value = TasksListState.Error(it)
            }.collect {
                if (it.isEmpty()) {
                    _taskWithCategoriesState.value = TasksListState.Empty
                } else {
                    _taskWithCategoriesState.value = TasksListState.Success(it)
                }
            }
        }
    }

    fun getCompletedTasks(sortingType: SortingType = SortingType.Date, categoryId: Long? = null) {
        viewModelScope.launch {
            if (categoryId != null) {
                taskRepository.getCompletedTasksOfTheCategory(categoryId).catch {

                }.collectLatest {
                    _categoryCompletedTasks.value = it
                }
            }
        }
    }


    fun getCategoryTasks(
        categoryId: Long,
        groupingType: GroupingType,
        sortingType: SortingType = SortingType.Date
    ) {

        viewModelScope.launch {
            taskRepository.getCategoryTasks(
                categoryId
            ).map { items ->
                val groupedItems = if (items.isNotEmpty()) {
                    when (groupingType) {
                        GroupingType.Priority -> {
                            items.groupBy {
                                it.priority
                            }.mapKeys {
                                HeaderType.Priority(it.key.name, it.key.id)
                            }
                        }

                        GroupingType.Date -> {
                            items.groupBy { taskWithCategory ->
                                taskWithCategory.dueDate?.let {
                                    convertCalendarToDateEnum(it)
                                } ?: DateEnum.NO_DATE
                            }.mapKeys {
                                HeaderType.Date(it.key)
                            }
                        }

                        else -> mapOf(Pair(HeaderType.NoHeader, items))
                    }
                } else {
                    emptyMap()
                }
                sortCategoryTasksList(sortingType, groupedItems)
            }.catch {
                _categoryTasksState.value = CategoryTasksListState.Error(it)
            }.collect {
                if (it.isEmpty()) {
                    _categoryTasksState.value = CategoryTasksListState.Empty
                } else {
                    _categoryTasksState.value = CategoryTasksListState.Success(it)
                }
            }
        }
    }


    fun launchAllTasks(query: String = "", groupingType: GroupingType, sortingType: SortingType) {
        viewModelScope.launch {
            getAllTasks(query = query, groupingType = groupingType, sortingType = sortingType)
        }
    }

    private suspend fun getAllTasks(
        query: String = "",
        groupingType: GroupingType,
        sortingType: SortingType = SortingType.Date
    ) {
        taskRepository.getAllTasksWithCategories(query.ifEmpty { null })
            .map { items ->
                val groupedItems =
                    if (items.isNotEmpty()) groupTasks(items, groupingType) else emptyMap()
                sortTasksListWithCategories(sortingType, groupedItems)
            }.catch {
                _taskWithCategoriesState.value = TasksListState.Error(it)
            }.collect { items ->
                if (items.isEmpty()) {
                    _taskWithCategoriesState.value = TasksListState.Empty
                } else {
                    _taskWithCategoriesState.value = TasksListState.Success(items)
                }
            }

    }

    fun searchThroughAllTasks(query: String, groupingType: GroupingType, sortingType: SortingType) {
        debouncer(viewModelScope) {
            getAllTasks(query, groupingType, sortingType)
        }
    }

    fun putTasksOnDeletion(tasks: List<Task>, categoryId: Long? = null) {
        if (categoryId != null) {
            val currentTasks =
                _categoryTasksState.value as? CategoryTasksListState.Success ?: return
            _categoryTasksState.value =
                CategoryTasksListState.Success(currentTasks.tasks.mapValues { entry ->
                    entry.value.filterNot {
                        tasks.contains(it)
                    }
                })
        } else {
            val currentTasks = _taskWithCategoriesState.value as? TasksListState.Success ?: return
            _taskWithCategoriesState.value =
                TasksListState.Success(currentTasks.tasks.mapValues { entry ->
                    entry.value.filterNot {
                        tasks.contains(it.task)
                    }
                })
        }
    }

    fun putTaskOnDeletion(task: Task, categoryId: Long? = null) {
        taskOnDeletion = task
        if (categoryId != null) {
            val currentTasks =
                _categoryTasksState.value as? CategoryTasksListState.Success ?: return
            _categoryTasksState.value =
                CategoryTasksListState.Success(currentTasks.tasks.mapValues { entry ->
                    entry.value.filterNot {
                        task.id != it.id
                    }
                })
        } else {
            val currentTasks = _taskWithCategoriesState.value as? TasksListState.Success ?: return
            _taskWithCategoriesState.value =
                TasksListState.Success(currentTasks.tasks.mapValues { entry ->
                    entry.value.filterNot {
                        task.id == it.task.id
                    }
                })
        }

    }

    fun undoTasksDeletion(groupingType: GroupingType, sortingType: SortingType) {
        viewModelScope.launch {
            getAllTasks(groupingType = groupingType, sortingType = sortingType)
        }
    }

    fun undoCategoryTasksDeletion(
        categoryId: Long,
        groupingType: GroupingType,
        sortingType: SortingType
    ) {
        getCategoryTasks(categoryId, groupingType, sortingType)
    }

    private fun sortTasksListWithCategories(
        sortingType: SortingType,
        tasks: Map<out HeaderType, List<TaskWithCategory>>
    ) =
        tasks.mapValues { map ->
            when (sortingType) {
                SortingType.Date -> map.value.sortedBy {
                    it.task.dueDate
                }

                SortingType.Priority -> map.value.sortedBy {
                    it.task.priority.id
                }

                SortingType.Title -> map.value.sortedBy {
                    it.task.name
                }
            }
        }

    private fun groupTasks(
        items: List<TaskWithCategory>,
        groupingType: GroupingType
    ): Map<HeaderType, List<TaskWithCategory>> {
        return when (groupingType) {
            GroupingType.None -> {
                mapOf(Pair(HeaderType.NoHeader, items))
            }

            GroupingType.Category -> {
                items.groupBy {
                    it.category
                }.mapKeys {
                    HeaderType.Category(it.key?.name ?: "No category")
                }
            }

            GroupingType.Priority -> {
                items.groupBy {
                    it.task.priority
                }.mapKeys {
                    HeaderType.Priority(it.key.name, it.key.id)
                }
            }

            GroupingType.Date -> {
                items.groupBy { taskWithCategory ->
                    taskWithCategory.task.dueDate?.let {
                        convertCalendarToDateEnum(it)
                    } ?: DateEnum.NO_DATE
                }.mapKeys {
                    HeaderType.Date(it.key)
                }
            }
        }
    }

    private fun sortCategoryTasksList(
        sortingType: SortingType,
        tasks: Map<out HeaderType, List<Task>>
    ) =
        tasks.mapValues { map ->
            when (sortingType) {
                SortingType.Date -> map.value.sortedBy {
                    it.dueDate
                }

                SortingType.Title -> map.value.sortedBy {
                    it.name
                }

                SortingType.Priority -> map.value.sortedBy {
                    it.priority.id
                }
            }
        }

    private fun convertCalendarToDateEnum(taskDueDate: Calendar): DateEnum {
        val currentDate = Calendar.getInstance()
        return if (currentDate[Calendar.YEAR] == taskDueDate[Calendar.YEAR] &&
            currentDate[Calendar.MONTH] == taskDueDate[Calendar.MONTH] &&
            currentDate[Calendar.DAY_OF_MONTH] == taskDueDate[Calendar.DAY_OF_MONTH]
        ) {
            DateEnum.TODAY
        } else if (taskDueDate.after(currentDate)) {
            val tomorrow = currentDate.clone() as Calendar
            tomorrow[Calendar.DAY_OF_MONTH] += 1
            val nextSevenDays = currentDate.clone() as Calendar
            nextSevenDays[Calendar.DAY_OF_MONTH] += 7

            if (taskDueDate[Calendar.YEAR] == tomorrow[Calendar.YEAR] &&
                taskDueDate[Calendar.MONTH] == tomorrow[Calendar.MONTH] &&
                taskDueDate[Calendar.DAY_OF_MONTH] == tomorrow[Calendar.DAY_OF_MONTH]
            ) {
                DateEnum.TOMORROW
            } else if (taskDueDate.after(nextSevenDays)) {
                DateEnum.LATER
            } else {
                DateEnum.NEXT_SEVEN_DAYS
            }
        } else {
            DateEnum.OVERDUE
        }
    }


}