package nau.android.taskify.ui.alarm

import kotlinx.coroutines.flow.first
import nau.android.taskify.data.repository.ITaskRepository
import nau.android.taskify.ui.enums.TaskRepeatInterval
import nau.android.taskify.ui.model.Task
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RescheduleFutureAlarms @Inject constructor(
    private val taskRepository: ITaskRepository,
    private val alarmScheduler: AlarmScheduler,
    private val scheduleNextAlarm: ScheduleNextAlarm
) {

    /**
     * Reschedule scheduled and misses repeating tasks.
     */
    suspend operator fun invoke() {
        val uncompletedAlarms = taskRepository.getAllTasks().first().filterNot {
            !it.task.completed
        }
        val futureAlarms = uncompletedAlarms.filter { isInFuture(it.task.dueDate) }
        val missedRepeating = uncompletedAlarms.filter { isMissedRepeating(it.task) }

        futureAlarms.forEach { rescheduleFutureTask(it.task) }
        missedRepeating.forEach { rescheduleRepeatingTask(it.task) }
    }

    private fun isInFuture(calendar: Calendar?): Boolean {
        return calendar?.after(Calendar.getInstance()) ?: false
    }

    private fun isMissedRepeating(task: Task): Boolean {
        return task.repeatInterval != TaskRepeatInterval.NONE && task.dueDate?.before(Calendar.getInstance()) ?: false
    }

    private fun rescheduleFutureTask(task: Task) {
        val futureTime = task.dueDate?.time?.time ?: return
        alarmScheduler.scheduleTaskAlarm(task.id, futureTime)
    }

    private suspend fun rescheduleRepeatingTask(task: Task) {
        scheduleNextAlarm(task)
    }
}
