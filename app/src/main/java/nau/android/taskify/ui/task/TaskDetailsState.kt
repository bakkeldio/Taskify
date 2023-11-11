package nau.android.taskify.ui.task

import nau.android.taskify.ui.model.Task

sealed class TaskDetailsState {

    data class Success(val task: Task) : TaskDetailsState()
    data class Error(val throwable: Throwable): TaskDetailsState()

    object Loading: TaskDetailsState()
}