package nau.android.taskify.ui.tasksList

class TasksListMenuDropdownParameters(
    val displayMenu: Boolean,
    val showDetails: Boolean,
    val showCompleted: Boolean,
    val showDetailsChanged: (Boolean) -> Unit,
    val onShowCompletedChanged: (Boolean) -> Unit,
    val displayMenuChanged: (Boolean) -> Unit,
    val onShowSortBottomSheet: () -> Unit,
    val openMultiSelectionOption: () -> Unit
)