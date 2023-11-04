package nau.android.taskify.data.model


/**
 * Represents priorities for a task
 * @property id - id of the priority
 */
enum class TaskPriority(val id: Int) {
    HIGH(1),
    MEDIUM(2),
    LOW(3),
    NONE(4)
}