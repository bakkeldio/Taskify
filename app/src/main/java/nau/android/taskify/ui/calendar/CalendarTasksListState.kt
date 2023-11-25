package nau.android.taskify.ui.calendar

import nau.android.taskify.ui.model.Task

sealed class CalendarTasksListState {
    object Empty : CalendarTasksListState()
    class Error(val throwable: Throwable) : CalendarTasksListState()
    class Success(val tasks: List<Task>) : CalendarTasksListState()
}