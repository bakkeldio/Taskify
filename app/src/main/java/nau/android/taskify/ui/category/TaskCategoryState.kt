package nau.android.taskify.ui.category

import nau.android.taskify.ui.model.Category


sealed class TaskCategoryState {

    object Empty : TaskCategoryState()
    class Error(taskId: Long, val message: String = "Error occurred while loading the category of the task $taskId") : TaskCategoryState()
    class Success(val taskCategory: Category) : TaskCategoryState()

}