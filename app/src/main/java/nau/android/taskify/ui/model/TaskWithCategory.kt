package nau.android.taskify.ui.model

data class TaskWithCategory(
    val task: Task,
    val category: Category? = null
)