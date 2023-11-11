package nau.android.taskify.data.model.mapper

import nau.android.taskify.data.model.AlarmInterval
import nau.android.taskify.ui.enums.TaskRepeatInterval
import javax.inject.Inject

class TaskRepeatIntervalMapper @Inject constructor() {

    fun toUIRepeatInterval(repeatInterval: AlarmInterval): TaskRepeatInterval {
        return when (repeatInterval) {
            AlarmInterval.NONE -> TaskRepeatInterval.NONE
            AlarmInterval.DAILY -> TaskRepeatInterval.DAILY
            AlarmInterval.HOURLY -> TaskRepeatInterval.HOURLY
            AlarmInterval.WEEKLY -> TaskRepeatInterval.WEEKLY
            AlarmInterval.MONTHLY -> TaskRepeatInterval.MONTHLY
            AlarmInterval.YEARLY -> TaskRepeatInterval.YEARLY
        }
    }

    fun toRepoRepeatInterval(taskRepeatInterval: TaskRepeatInterval): AlarmInterval {
        return when (taskRepeatInterval) {
            TaskRepeatInterval.NONE -> AlarmInterval.NONE
            TaskRepeatInterval.YEARLY -> AlarmInterval.YEARLY
            TaskRepeatInterval.MONTHLY -> AlarmInterval.MONTHLY
            TaskRepeatInterval.WEEKLY -> AlarmInterval.WEEKLY
            TaskRepeatInterval.DAILY -> AlarmInterval.DAILY
            TaskRepeatInterval.HOURLY -> AlarmInterval.HOURLY
        }
    }

}