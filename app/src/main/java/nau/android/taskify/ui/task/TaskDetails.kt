package nau.android.taskify.ui.task

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import nau.android.taskify.R
import nau.android.taskify.ui.DateInfo
import nau.android.taskify.ui.category.ChangeCategoryBottomSheet
import nau.android.taskify.ui.category.TaskCategoryState
import nau.android.taskify.ui.customElements.TaskifyPrioritySelectionDropdownMenu
import nau.android.taskify.ui.customElements.TaskifyTextField
import nau.android.taskify.ui.dialogs.TaskifyDatePickerDialog
import nau.android.taskify.ui.dialogs.formatToAmPmTime
import nau.android.taskify.ui.enums.Priority
import nau.android.taskify.ui.enums.TaskRepeatInterval
import nau.android.taskify.ui.extensions.formatTaskifyDate
import nau.android.taskify.ui.extensions.formatToAmPm
import nau.android.taskify.ui.extensions.keyboardAsState
import nau.android.taskify.ui.extensions.toast
import nau.android.taskify.ui.model.Task
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetails(
    navController: NavController,
    taskId: Long?,
    viewModel: TaskViewModel = hiltViewModel()
) {

    var showCategoryBottomSheet by remember {
        mutableStateOf(false)
    }

    var showTaskDetailBottomSheet by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = true) {
        viewModel.getTaskDetails(taskId)
        viewModel.getTaskCategory(taskId)
    }

    val taskDetail =
        viewModel.taskDetailsStateFlow.collectAsStateWithLifecycle(initialValue = TaskDetailsState.Loading)

    val taskCategory = viewModel.categoryState.collectAsStateWithLifecycle()


    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = {

                TaskCategory(state = taskCategory.value) {
                    showCategoryBottomSheet = true
                }
            },
            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null)
                }
            },
            actions = {
                IconButton(onClick = {
                    showTaskDetailBottomSheet = true
                }) {
                    Icon(Icons.Default.Menu, contentDescription = null)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
        )
    }) { paddingValues ->

        when (val result = taskDetail.value) {
            is TaskDetailsState.Success -> {
                TaskDetailsStateSuccess(
                    paddingValues = paddingValues,
                    taskDetail = result.task,
                    showCategoryBottomSheet = showCategoryBottomSheet,
                    showTaskDetailBottomSheet = showTaskDetailBottomSheet,
                    completeTask = {
                        viewModel.completeTask()
                    },
                    updateTaskDescription = { newDescription ->
                        viewModel.updateTaskDescription(newDescription)
                    },
                    updateTaskPriority = { newPriority ->
                        viewModel.updateTaskPriority(newPriority)
                    },
                    updateTaskTitle = { newTitle ->
                        viewModel.updateTaskTitle(newTitle)
                    },
                    updateDateOfTheTask = {
                        viewModel.updateDateOfTask(it)
                    },
                    updateTaskCategory = {
                        viewModel.updateTaskCategory(it)
                    }, hideCategoryBottomSheet = {
                        showCategoryBottomSheet = false
                    }, hideTaskDetailsBottomSheet = {
                        showTaskDetailBottomSheet = false
                    }
                )
            }

            is TaskDetailsState.Error -> {

            }

            is TaskDetailsState.Loading -> {

            }
        }
    }

}

@Composable
fun TaskCategory(state: TaskCategoryState, showCategoryBottomSheet: () -> Unit) {
    val context = LocalContext.current
    when (state) {
        is TaskCategoryState.Success -> TaskCategoryContent(
            title = state.taskCategory.name,
            showCategoryBottomSheet
        )

        is TaskCategoryState.Empty -> TaskCategoryContent(
            title = context.getString(
                R.string.no_category
            ), showCategoryBottomSheet
        )

        is TaskCategoryState.Error -> {
            context.toast(state.message)
        }
    }
}

@Composable
private fun TaskCategoryContent(title: String, showCategoryBottomSheet: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Icon(imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            modifier = Modifier.clickable {
                showCategoryBottomSheet()
            })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailsStateSuccess(
    paddingValues: PaddingValues,
    showCategoryBottomSheet: Boolean,
    showTaskDetailBottomSheet: Boolean,
    taskDetail: Task,
    completeTask: (Boolean) -> Unit,
    updateTaskDescription: (String) -> Unit,
    updateTaskPriority: (Priority) -> Unit,
    updateDateOfTheTask: (DateInfo) -> Unit,
    updateTaskTitle: (String) -> Unit,
    updateTaskCategory: (Long) -> Unit,
    hideCategoryBottomSheet: () -> Unit,
    hideTaskDetailsBottomSheet: () -> Unit
) {

    val showDatePickerDialog = remember {
        mutableStateOf(false)
    }

    var taskTitle by remember {
        mutableStateOf(taskDetail.name)
    }

    var taskDescription by remember {
        mutableStateOf(taskDetail.description)
    }

    val focusManager = LocalFocusManager.current

    val categorySheetState = rememberModalBottomSheetState()

    val taskDetailSheetState = rememberModalBottomSheetState()

    val keyboardState = keyboardAsState()

    if (!keyboardState.value) {
        focusManager.clearFocus()
    }

    if (showCategoryBottomSheet) {
        ChangeCategoryBottomSheet(
            currentCategoryId = taskDetail.categoryId, onCategoryChanged = { newCategory ->
                updateTaskCategory(newCategory.id)
                hideCategoryBottomSheet()
            }, sheetState = categorySheetState
        ) {
            hideCategoryBottomSheet()
        }
    }

    if (showTaskDetailBottomSheet) {
        TaskDetailsBottomSheet(onDismissRequest = {
            hideTaskDetailsBottomSheet()
        }, modalBottomSheetState = taskDetailSheetState)
    }

    if (showDatePickerDialog.value) {
        TaskifyDatePickerDialog(
            DateInfo(
                date = taskDetail.dueDate,
                repeatInterval = taskDetail.repeatInterval,
                reminderTypes = taskDetail.reminders,
                timeIncluded = taskDetail.timeIncluded
            ), onDismiss = {
                showDatePickerDialog.value = false
            }, onDateChanged = { dateInfo ->
                updateDateOfTheTask(dateInfo)
                showDatePickerDialog.value = false
            })
    }

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(horizontal = 10.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(checked = taskDetail.completed, onCheckedChange = {
                completeTask(it)
            }, modifier = Modifier.align(Alignment.CenterStart))
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .clickable {
                        showDatePickerDialog.value = true
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = taskDetail.dueDate?.let { date ->
                        applyColorForDateTime(
                            selectedDate = date,
                            date = date.formatTaskifyDate(
                                if (taskDetail.timeIncluded) Pair(
                                    date[Calendar.HOUR_OF_DAY],
                                    date[Calendar.MINUTE]
                                ).formatToAmPm() else ""
                            )
                        )
                    } ?: buildAnnotatedString { append("Date & Time") },
                    style = MaterialTheme.typography.titleMedium
                )
                if (taskDetail.repeatInterval != TaskRepeatInterval.NONE) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_repeat),
                            contentDescription = "Repeat",
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = taskDetail.repeatInterval.title,
                            modifier = Modifier.padding(start = 5.dp),
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.outline)
                        )
                    }
                }
            }
            DropDownMenuForCategories(taskDetail.priority) { newPriority ->
                updateTaskPriority(newPriority)
            }
        }

        TaskifyTextField(
            value = taskTitle,
            placeHolder = "What would you like to do?",
            onValueChange = { newTitle ->
                updateTaskTitle(newTitle)
                taskTitle = newTitle
            })
        TaskifyTextField(
            Modifier.padding(top = 5.dp),
            value = taskDescription ?: "",
            onValueChange = { newDescription ->
                updateTaskDescription(newDescription)
                taskDescription = newDescription
            })

    }
}

@Composable
private fun BoxScope.DropDownMenuForCategories(
    taskPriority: Priority, changeTaskPriority: (Priority) -> Unit
) {
    var dropDownMenuOpen by remember {
        mutableStateOf(false)
    }
    Box(modifier = Modifier.align(Alignment.CenterEnd)) {
        IconButton(onClick = {
            dropDownMenuOpen = true
        }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_flag),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.outline
            )
        }

        TaskifyPrioritySelectionDropdownMenu(
            taskPriority = taskPriority,
            dropDownMenuOpen = dropDownMenuOpen,
            changeTaskPriority = { newPriority ->
                changeTaskPriority(newPriority)
                dropDownMenuOpen = false
            }, closeDropDown = {
                dropDownMenuOpen = false
            })
    }
}

@Composable
private fun applyColorForDateTime(selectedDate: Calendar, date: String): AnnotatedString {
    val currentDate = Calendar.getInstance()
    return buildAnnotatedString {
        withStyle(
            SpanStyle(
                color = if (selectedDate[Calendar.YEAR] == currentDate[Calendar.YEAR] && selectedDate[Calendar.MONTH] == currentDate[Calendar.MONTH] &&
                    selectedDate[Calendar.DAY_OF_MONTH] == currentDate[Calendar.DAY_OF_MONTH]
                ) MaterialTheme.colorScheme.primary else if (currentDate.before(selectedDate)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        ) {
            append(date)
        }
    }
}


