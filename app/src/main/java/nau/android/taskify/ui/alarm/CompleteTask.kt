package nau.android.taskify.ui.alarm

import kotlinx.coroutines.flow.firstOrNull
import nau.android.taskify.data.repository.ITaskRepository
import nau.android.taskify.ui.alarm.notification.TaskifyNotification
import nau.android.taskify.ui.enums.TaskRepeatInterval
import nau.android.taskify.ui.extensions.updateDate
import java.util.Calendar
import javax.inject.Inject

class CompleteTask @Inject constructor(
    private val taskRepo: ITaskRepository,
    private val alarmScheduler: AlarmScheduler,
    private val notification: TaskifyNotification
) {
    suspend operator fun invoke(taskId: Long) {
        val task = taskRepo.getTaskByIdFlow(taskId).firstOrNull() ?: return
        taskRepo.updateTask(
            task.copy(
                completed = true,
                completionDate = Calendar.getInstance(),
                repeatInterval = TaskRepeatInterval.NONE
            )
        )
        alarmScheduler.cancelTaskAlarm(task.id)
        if (task.repeatInterval != TaskRepeatInterval.NONE) {
            if (task.dueDate != null && task.timeIncluded) {
                do {
                    task.repeatInterval.updateDate(task.dueDate)
                } while (Calendar.getInstance().after(task.dueDate))
                val id = taskRepo.createTask(
                    task.copy(
                        id = 0,
                        creationDate = Calendar.getInstance(),
                        dueDate = task.dueDate
                    )
                )
                alarmScheduler.scheduleTaskAlarm(id, task.dueDate.timeInMillis)
            }
        }
        notification.hide(taskId)
    }
}