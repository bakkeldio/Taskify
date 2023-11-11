package nau.android.taskify.data.model.mapper

import nau.android.taskify.ui.enums.ReminderType
import javax.inject.Inject
import nau.android.taskify.data.model.ReminderType as LocalReminderType

class TaskReminderMapper @Inject constructor(){

    fun toUI(reminderType: LocalReminderType): ReminderType {
        return when (reminderType) {
            LocalReminderType.ON_TIME -> ReminderType.OnTime
            LocalReminderType.DAY_BEFORE -> ReminderType.DayBefore
            LocalReminderType.FIVE_MINUTES_BEFORE -> ReminderType.FiveMinutesBefore
            LocalReminderType.TEN_MINUTES_BEFORE -> ReminderType.TenMinutesBefore
            LocalReminderType.FIFTEEN_MINUTES_BEFORE -> ReminderType.FifteenMinutesBefore
            LocalReminderType.THIRTY_MINUTES_BEFORE -> ReminderType.ThirtyMinutesBefore
            LocalReminderType.TWO_DAYS_BEFORE -> ReminderType.TwoDaysBefore
        }
    }

    fun toRepo(reminderType: ReminderType): LocalReminderType {
        return when (reminderType) {
            ReminderType.OnTime -> LocalReminderType.ON_TIME
            ReminderType.FiveMinutesBefore -> LocalReminderType.FIVE_MINUTES_BEFORE
            ReminderType.TenMinutesBefore -> LocalReminderType.TEN_MINUTES_BEFORE
            ReminderType.FifteenMinutesBefore -> LocalReminderType.FIFTEEN_MINUTES_BEFORE
            ReminderType.ThirtyMinutesBefore -> LocalReminderType.THIRTY_MINUTES_BEFORE
            ReminderType.DayBefore -> LocalReminderType.DAY_BEFORE
            ReminderType.TwoDaysBefore -> LocalReminderType.TWO_DAYS_BEFORE
        }
    }
}