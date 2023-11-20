package nau.android.taskify.ui.model

data class EisenhowerMatrixModel(
    val importantUrgentTasks: List<Task>,
    val importantNotUrgentTasks: List<Task>,
    val urgentNotImportantTasks: List<Task>,
    val unImportantNotUrgentTasks: List<Task>
)