package nau.android.taskify.data.model.mapper

import nau.android.taskify.data.model.TaskPriority
import nau.android.taskify.ui.enums.Priority
import javax.inject.Inject

class TaskPriorityMapper @Inject constructor() {

    fun toUI(taskPriority: TaskPriority): Priority {
        return when (taskPriority) {
            TaskPriority.HIGH -> Priority.HIGH
            TaskPriority.MEDIUM -> Priority.MEDIUM
            TaskPriority.LOW -> Priority.LOW
            TaskPriority.NONE -> Priority.NONE
        }
    }

    fun toRepo(priority: Priority): TaskPriority {
        return when (priority) {
            Priority.HIGH -> TaskPriority.HIGH
            Priority.MEDIUM -> TaskPriority.MEDIUM
            Priority.LOW -> TaskPriority.LOW
            Priority.NONE -> TaskPriority.NONE
        }
    }
}