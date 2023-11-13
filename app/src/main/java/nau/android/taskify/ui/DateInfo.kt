package nau.android.taskify.ui

import nau.android.taskify.ui.enums.ReminderType
import nau.android.taskify.ui.enums.RepeatIntervalType
import nau.android.taskify.ui.enums.TaskRepeatInterval
import java.util.Calendar

data class DateInfo(
    val date: Calendar? = null,
    val timeIncluded: Boolean = false,
    val repeatInterval: TaskRepeatInterval = TaskRepeatInterval.NONE,
    val reminderTypes: List<ReminderType> = listOf()
)
