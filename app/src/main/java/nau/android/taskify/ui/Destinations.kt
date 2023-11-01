package nau.android.taskify.ui


import nau.android.taskify.R

sealed class MainDestination(val title: String = "", val route: String, val filledIcon: Int, val outlinedIcon: Int) {
    object ListOfTasks :
        MainDestination("Tasks", "list_of_tasks", R.drawable.ic_tasks_list_filled, R.drawable.ic_tasks_list_outlined)

    object Categories :
        MainDestination("Categories", "categories", R.drawable.ic_category_filled, R.drawable.ic_category_outlined)

    object EisenhowerMatrix : MainDestination(
        "Eisenhower Matrix",
        "eisenhower_matrix",
        R.drawable.ic_eisenhower_matrix,
        R.drawable.ic_eisenhower_outlined
    )

    object Date : MainDestination("Calendar", "Calendar", R.drawable.ic_date_filled, R.drawable.ic_date_outlined)
    object AppSettings : MainDestination("Application Settings", "App",
        R.drawable.ic_settings_filled,
        R.drawable.ic_app_settings
    )


}