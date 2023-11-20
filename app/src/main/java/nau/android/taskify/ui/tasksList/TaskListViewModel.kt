package nau.android.taskify.ui.tasksList

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
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
import nau.android.taskify.ui.extensions.updateDate
import nau.android.taskify.ui.model.Task
import nau.android.taskify.ui.model.TaskWithCategory
import java.util.Calendar
import javax.inject.Inject


@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val taskRepository: ITaskRepository,
    private val alarmScheduler: AlarmScheduler,
    private val completeTask: CompleteTask
) :
    ViewModel() {

    fun createTask(task: Task) {

        viewModelScope.launch {
            val id = taskRepository.createTask(task)
            Log.d("Created task id: ", id.toString())
            if (task.dueDate != null && task.timeIncluded) {
                alarmScheduler.scheduleTaskAlarm(id, task.dueDate.timeInMillis)
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
        }
    }

    fun completeTask(task: Task) {
        viewModelScope.launch {
            delay(500)
            completeTask(task.id)
        }
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

    fun getEisenhowerQuadrantTasks(
        groupingType: GroupingType,
        sortingType: SortingType,
        eisenhowerMatrixQuadrant: EisenhowerMatrixQuadrant
    ) = flow {
        taskRepository.getAllTasksWithCategories().map {
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
            val groupedTasks = mapToKeyValue(tasks, groupingType)
            sortTasksListWithCategories(sortingType, groupedTasks)
        }.catch {
            emit(TasksListState.Error(it))
        }.collect {
            emit(TasksListState.Success(it))
        }
    }


    fun getCategoryTasks(
        categoryId: Long,
        groupingType: GroupingType,
        sortingType: SortingType = SortingType.Date
    ) =
        flow {
            taskRepository.getCategoryTasks(
                categoryId
            ).map { items ->
                val groupedItems = when (groupingType) {
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
                sortCategoryTasksList(sortingType, groupedItems)
            }.catch {
                emit(CategoryTasksListState.Error(it))
            }.collect {
                emit(CategoryTasksListState.Success(it))
            }
        }

    fun getAllTasks(groupingType: GroupingType, sortingType: SortingType = SortingType.Date) =
        flow {
            taskRepository.getAllTasksWithCategories().map { items ->
                val groupedItems = mapToKeyValue(items, groupingType)
                sortTasksListWithCategories(sortingType, groupedItems)
            }.catch {
                emit(TasksListState.Error(it))
            }.collect { items ->
                if (items.isEmpty()) {
                    emit(TasksListState.Empty)
                } else {
                    emit(TasksListState.Success(items))
                }
            }
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

    private fun mapToKeyValue(
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