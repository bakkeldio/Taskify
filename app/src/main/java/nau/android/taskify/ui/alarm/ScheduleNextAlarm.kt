package nau.android.taskify.ui.alarm

import nau.android.taskify.data.repository.ITaskRepository
import nau.android.taskify.ui.enums.TaskRepeatInterval
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

        val updatedDate = updateDate(task.dueDate!!, task.repeatInterval)

        val id = taskRepository.createTask(task.copy(dueDate = updatedDate))

        alarmScheduler.scheduleTaskAlarm(id, updatedDate.timeInMillis)

    }

    private fun updateDate(date: Calendar, repeatInterval: TaskRepeatInterval): Calendar {
        return when (repeatInterval) {
            TaskRepeatInterval.HOURLY -> date.apply {
                add(Calendar.HOUR_OF_DAY, 1)
            }

            TaskRepeatInterval.DAILY -> date.apply {
                add(Calendar.DAY_OF_MONTH, 1)
            }

            TaskRepeatInterval.WEEKLY -> date.apply {
                add(Calendar.WEEK_OF_MONTH, 1)
            }

            TaskRepeatInterval.MONTHLY -> date.apply {
                add(Calendar.MONTH, 1)
            }

            TaskRepeatInterval.YEARLY -> date.apply {
                add(Calendar.YEAR, 1)
            }

            TaskRepeatInterval.NONE -> date
        }
    }
}