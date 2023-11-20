package nau.android.taskify.ui.tasksList

import nau.android.taskify.ui.model.TaskWithCategory

sealed class TasksListState {

    object Loading : TasksListState()

    data class Error(val cause: Throwable) : TasksListState()

    data class Success(val tasks: Map<HeaderType, List<TaskWithCategory>>) : TasksListState()

    object Empty : TasksListState()
}