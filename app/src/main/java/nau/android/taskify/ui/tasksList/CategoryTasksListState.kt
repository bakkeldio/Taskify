package nau.android.taskify.ui.tasksList

import nau.android.taskify.ui.model.Task

sealed class CategoryTasksListState {

    object Loading : CategoryTasksListState()
    class Success(val tasks: Map<HeaderType, List<Task>>) : CategoryTasksListState()

    class Error(val throwable: Throwable) : CategoryTasksListState()

    object Empty : CategoryTasksListState()
}