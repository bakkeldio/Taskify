package nau.android.taskify.data.model.mapper

import nau.android.taskify.ui.enums.TaskRepeatInterval
import nau.android.taskify.ui.model.Task
import javax.inject.Inject
import javax.inject.Singleton
import nau.android.taskify.data.model.Task as TaskEntity

@Singleton
class TaskMapper @Inject constructor(
    private val taskRepeatIntervalMapper: TaskRepeatIntervalMapper,
    private val taskPriorityMapper: TaskPriorityMapper,
    private val taskReminderMapper: TaskReminderMapper
) {

    fun toUI(task: TaskEntity): Task {
        return Task(
            id = task.id,
            name = task.name,
            description = task.description,
            priority = taskPriorityMapper.toUI(task.priority),
            completed = task.completed,
            dueDate = task.dueDate,
            categoryId = task.categoryId,
            creationDate = task.creationDate,
            completionDate = task.completedDate,
            timeIncluded = task.timeIncluded,
            repeatInterval = taskRepeatIntervalMapper.toUIRepeatInterval(task.alarmInterval),
            reminders = task.reminders?.map {
                taskReminderMapper.toUI(it)
            } ?: emptyList()
        )
    }

    fun toRepo(task: Task): TaskEntity {
        return TaskEntity(
            name = task.name,
            description = task.description,
            dueDate = task.dueDate,
            completed = task.completed,
            priority = taskPriorityMapper.toRepo(task.priority),
            isRepeating = task.repeatInterval != TaskRepeatInterval.NONE,
            categoryId = task.categoryId,
            timeIncluded = task.timeIncluded,
            reminders = task.reminders.map { taskReminderMapper.toRepo(it) },
            alarmInterval = taskRepeatIntervalMapper.toRepoRepeatInterval(task.repeatInterval),
            creationDate = task.creationDate,
            completedDate = task.completionDate
        )
    }

}