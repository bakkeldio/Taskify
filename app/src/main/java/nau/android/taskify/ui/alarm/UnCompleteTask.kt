package nau.android.taskify.ui.alarm

import nau.android.taskify.data.repository.ITaskRepository
import nau.android.taskify.ui.model.Task
import javax.inject.Inject

class UnCompleteTask @Inject constructor(
    private val taskRepo: ITaskRepository,
    private val alarmScheduler: AlarmScheduler,
) {

    suspend operator fun invoke(task: Task) {
        taskRepo.updateTask(
            task.copy(
                completed = false,
                completionDate = null
            )
        )
        if (task.dueDate != null && task.timeIncluded) {
            alarmScheduler.scheduleTaskAlarm(task.id, task.dueDate.timeInMillis)
        }
    }
}