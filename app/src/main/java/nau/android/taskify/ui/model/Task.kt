package nau.android.taskify.ui.model

import nau.android.taskify.ui.enums.Priority
import nau.android.taskify.ui.enums.ReminderType
import nau.android.taskify.ui.enums.TaskRepeatInterval
import java.util.Calendar


/**
 * Represents a Task class in UI layer
 */
data class Task(
    val id: Long = 0,
    val name: String,
    val description: String? = null,
    val priority: Priority = Priority.NONE,
    val completed: Boolean = false,
    val dueDate: Calendar? = null,
    val categoryId: Long? = null,
    val creationDate: Calendar? = null,
    val completionDate: Calendar? = null,
    val timeIncluded: Boolean = false,
    val repeatInterval: TaskRepeatInterval = TaskRepeatInterval.NONE,
    val reminders: List<ReminderType> = emptyList()
)