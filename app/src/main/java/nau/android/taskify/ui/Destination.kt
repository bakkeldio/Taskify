package nau.android.taskify.ui


import nau.android.taskify.R

sealed class Destination(
    val title: String = "",
    val route: String,
    val filledIcon: Int,
    val outlinedIcon: Int
) {
    object ListOfTasks :
        Destination(
            "Tasks",
            "list_of_tasks",
            R.drawable.ic_tasks_list_filled,
            R.drawable.ic_tasks_list_outlined
        )

    object Categories :
        Destination(
            "Categories",
            "categories",
            R.drawable.ic_category_filled,
            R.drawable.ic_category_outlined
        )

    object EisenhowerMatrix : Destination(
        "Eisenhower Matrix",
        "eisenhower_matrix",
        R.drawable.ic_eisenhower_matrix,
        R.drawable.ic_eisenhower_outlined
    )

    object Calendar : Destination(
        "Calendar",
        "Calendar",
        R.drawable.ic_date_filled,
        R.drawable.ic_date_outlined
    )

    object AppSettings : Destination(
        "Application Settings", "App",
        R.drawable.ic_settings_filled,
        R.drawable.ic_app_settings
    )

    companion object {
        const val TaskDetail = "task_details"
        const val CategoryTasksList = "category_tasks_list"
        const val MatrixTasksList = "matrix_tasks_list"
    }
}


object DestinationNavArgs {
    const val categoryId = "category_id"
    const val taskId = "task_id"
    const val quadrantType = "matrix_quadrant_type"
}