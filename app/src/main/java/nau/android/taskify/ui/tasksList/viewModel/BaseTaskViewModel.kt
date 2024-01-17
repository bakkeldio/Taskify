package nau.android.taskify.ui.tasksList.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nau.android.taskify.data.repository.ITaskRepository
import nau.android.taskify.ui.alarm.AlarmScheduler
import nau.android.taskify.ui.alarm.CompleteTask
import nau.android.taskify.ui.model.Task

open class BaseTaskViewModel(
    private val taskRepository: ITaskRepository,
    private val alarmScheduler: AlarmScheduler,
    private val completeTask: CompleteTask
) : ViewModel() {


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
            alarmScheduler.cancelTaskAlarm(task.id)
        }
    }

    fun completeTask(task: Task) {
        viewModelScope.launch {
            delay(500)
            completeTask(task.id)
        }
    }


}