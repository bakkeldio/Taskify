package nau.android.taskify.ui.tasksList

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import nau.android.taskify.ui.model.Task

class TasksListParameters {
    var displayMenu by mutableStateOf(false)
    var showDetails by mutableStateOf(false)
    var showCompleted by mutableStateOf(false)
    var showCreateTaskBottomSheet by mutableStateOf(false)
    var showSortTasksBottomSheet by mutableStateOf(false)
    var groupingType by mutableStateOf(GroupingType.None)
    var sortingType by mutableStateOf(SortingType.Date)
    var showDataPickerDialog by mutableStateOf(false)
    var inMultiSelection by mutableStateOf(false)
    var showCategoriesBottomSheet by mutableStateOf(false)
    var selectedTasks by mutableStateOf(emptySet<Task>())
    var showBackNavigation by mutableStateOf(false)
}