package nau.android.taskify.ui.alarm

import kotlinx.coroutines.flow.firstOrNull
import nau.android.taskify.data.repository.ITaskRepository
import nau.android.taskify.ui.alarm.notification.TaskifyNotification
import java.util.Calendar
import javax.inject.Inject

class CompleteTask @Inject constructor(
    private val taskRepo: ITaskRepository,
    private val alarmScheduler: AlarmScheduler,
    private val notification: TaskifyNotification
) {


    suspend operator fun invoke(taskId: Long) {
        val task = taskRepo.getTaskByIdFlow(taskId).firstOrNull() ?: return
        taskRepo.updateTask(task.copy(completed = true, completionDate = Calendar.getInstance()))
        alarmScheduler.cancelTaskAlarm(taskId)
        notification.hide(taskId)
    }
}