package nau.android.taskify.ui.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import nau.android.taskify.data.repository.ICategoryRepository
import nau.android.taskify.data.repository.ITaskRepository
import nau.android.taskify.ui.DateInfo
import nau.android.taskify.ui.alarm.AlarmScheduler
import nau.android.taskify.ui.alarm.CompleteTask
import nau.android.taskify.ui.alarm.UnCompleteTask
import nau.android.taskify.ui.category.TaskCategoryState
import nau.android.taskify.ui.enums.Priority
import nau.android.taskify.ui.extensions.Debouncer
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepo: ITaskRepository,
    private val categoryRepo: ICategoryRepository,
    private val alarmScheduler: AlarmScheduler,
    private val completeTask: CompleteTask,
    private val unCompleteTask: UnCompleteTask,
    private val debouncer: Debouncer
) : ViewModel() {

    private val taskDetailsMutableStateFlow: MutableStateFlow<TaskDetailsState> =
        MutableStateFlow(TaskDetailsState.Loading)
    val taskDetailsStateFlow: StateFlow<TaskDetailsState> = taskDetailsMutableStateFlow

    private val categoryMutableState: MutableStateFlow<TaskCategoryState> =
        MutableStateFlow(TaskCategoryState.Empty)

    val categoryState: StateFlow<TaskCategoryState> = categoryMutableState

    fun getTaskDetails(id: Long?) {
        id ?: return
        viewModelScope.launch {
            taskRepo.getTaskByIdFlow(id)
                .catch {
                    taskDetailsMutableStateFlow.value = TaskDetailsState.Error(it)
                }.collect {
                    taskDetailsMutableStateFlow.value = TaskDetailsState.Success(it)
                }
        }
    }

    fun getTaskCategory(taskId: Long?) {
        taskId ?: return
        viewModelScope.launch {
            val task = taskRepo.getTaskById(taskId) ?: return@launch
            val categoryId = task.categoryId
            if (categoryId == null) {
                categoryMutableState.value = TaskCategoryState.Empty
            } else {
                categoryMutableState.value =
                    categoryRepo.getCategoryById(categoryId)?.let { category ->
                        TaskCategoryState.Success(category)
                    } ?: TaskCategoryState.Error(taskId)
            }
        }
    }

    fun updateDateOfTask(dateInfo: DateInfo) {
        val dueDate = dateInfo.date
        val taskResult = taskDetailsMutableStateFlow.value as? TaskDetailsState.Success ?: return
        viewModelScope.launch {
            taskRepo.updateTask(taskResult.task.copy(dueDate = dueDate, timeIncluded = dateInfo.timeIncluded))
            if (dueDate != null && dateInfo.timeIncluded) {
                alarmScheduler.scheduleTaskAlarm(taskResult.task.id, dueDate.timeInMillis)
            } else {
                alarmScheduler.cancelTaskAlarm(taskResult.task.id)
            }
        }
    }

    fun completeTask() {
        val result = taskDetailsStateFlow.value as? TaskDetailsState.Success ?: return
        viewModelScope.launch {
            completeTask(result.task.id)
        }
    }

    fun unCompleteTask() {
        val taskState = taskDetailsStateFlow.value as? TaskDetailsState.Success ?: return
        viewModelScope.launch {
            unCompleteTask(taskState.task)
        }
    }

    fun updateTaskTitle(title: String) {
        val result = taskDetailsMutableStateFlow.value as? TaskDetailsState.Success ?: return
        debouncer(viewModelScope) {
            taskRepo.updateTask(result.task.copy(name = title))
        }
    }

    fun updateTaskDescription(description: String) {
        val result = taskDetailsStateFlow.value as? TaskDetailsState.Success ?: return
        debouncer(viewModelScope) {
            taskRepo.updateTask(result.task.copy(description = description))
        }
    }

    fun updateTaskCategory(categoryId: Long) {
        val result = taskDetailsStateFlow.value as? TaskDetailsState.Success ?: return
        viewModelScope.launch {
            taskRepo.updateTask(result.task.copy(categoryId = categoryId))
            getTaskCategory(result.task.id)
        }
    }

    fun updateTaskPriority(priority: Priority) {
        val result = taskDetailsMutableStateFlow.value as? TaskDetailsState.Success ?: return
        viewModelScope.launch {
            taskRepo.updateTask(result.task.copy(priority = priority))
        }
    }
}