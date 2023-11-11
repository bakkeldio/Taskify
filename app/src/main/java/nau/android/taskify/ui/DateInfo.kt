package nau.android.taskify.ui

import nau.android.taskify.ui.enums.ReminderType
import nau.android.taskify.ui.enums.RepeatIntervalType
import java.util.Calendar

data class DateInfo(
    val date: Calendar? = null,
    val time: Pair<Int, Int>? = null,
    val repeatInterval: RepeatIntervalType = RepeatIntervalType.None,
    val reminderTypes: List<ReminderType> = listOf()
)
