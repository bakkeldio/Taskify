package nau.android.taskify.ui.alarm

import nau.android.taskify.data.repository.ITaskRepository
import nau.android.taskify.ui.enums.TaskRepeatInterval
import nau.android.taskify.ui.extensions.updateDate
import nau.android.taskify.ui.model.Task
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScheduleNextAlarm @Inject constructor(
    private val taskRepository: ITaskRepository,
    private val alarmScheduler: AlarmScheduler
) {


    suspend operator fun invoke(task: Task) {

        do {
            task.repeatInterval.updateDate(task.dueDate!!)
        } while (Calendar.getInstance().after(task.dueDate))

        val id = taskRepository.createTask(task.copy(dueDate = task.dueDate))

        alarmScheduler.scheduleTaskAlarm(id, task.dueDate.timeInMillis)

    }
}