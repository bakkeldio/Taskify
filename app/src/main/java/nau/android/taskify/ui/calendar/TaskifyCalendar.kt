package nau.android.taskify.ui.calendar

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import nau.android.taskify.FloatingActionButton
import nau.android.taskify.R
import nau.android.taskify.TaskItem
import nau.android.taskify.ui.model.DateInfo
import nau.android.taskify.ui.customElements.NoTasks
import nau.android.taskify.ui.dialogs.CalendarGrid
import nau.android.taskify.ui.dialogs.TaskifyDatePickerDialog
import nau.android.taskify.ui.dialogs.WeeklyCalendar
import nau.android.taskify.ui.extensions.addMonth
import nau.android.taskify.ui.extensions.addWeek
import nau.android.taskify.ui.extensions.formatDateInPattern
import nau.android.taskify.ui.extensions.minusMonth
import nau.android.taskify.ui.extensions.minusWeek
import nau.android.taskify.ui.extensions.noRippleClickable
import nau.android.taskify.ui.model.Category
import nau.android.taskify.ui.model.Task
import nau.android.taskify.ui.tasksList.CreateTaskBottomSheet
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskifyCalendar(
    calendarViewModel: CalendarViewModel = hiltViewModel(), navigateToTaskDetails: (Long) -> Unit
) {

    var selectedDate by remember {
        mutableStateOf(Calendar.getInstance())
    }


    val dateForNewTask = remember {
        mutableStateOf(DateInfo())
    }

    var newTask by remember {
        mutableStateOf<Task?>(null)
    }

    var taskCategory by remember {
        mutableStateOf<Category?>(null)
    }

    var showDataPickerDialog by remember {
        mutableStateOf(false)
    }

    var expanded by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(key1 = Unit) {
        calendarViewModel.getTasksByDate(selectedDate)
    }

    var showTaskCreateBottomSheet by remember {
        mutableStateOf(false)
    }

    val createTaskBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val scope = rememberCoroutineScope()

    val calendarTasksState = calendarViewModel.calendarTasksStateFlow.collectAsStateWithLifecycle()

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "arrow left",
                        Modifier
                            .size(28.dp)
                            .clickable {
                                selectedDate = if (expanded) {
                                    selectedDate.minusMonth()
                                } else {
                                    selectedDate.minusWeek()
                                }
                            })
                    Text(text = selectedDate.formatDateInPattern("MMM YYYY").uppercase())
                    Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "arrow right",
                        Modifier
                            .size(28.dp)
                            .clickable {
                                selectedDate = if (expanded) {
                                    selectedDate.addMonth()
                                } else {
                                    selectedDate.addWeek()
                                }
                            })
                }
            },
            actions = {
                if (expanded) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "arrow down",
                        modifier = Modifier
                            .noRippleClickable {
                                expanded = false
                            }
                            .size(28.dp),
                    )
                } else {
                    Icon(imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "arrow up",
                        modifier = Modifier
                            .noRippleClickable {
                                expanded = true
                            }
                            .size(28.dp))
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            modifier = Modifier.padding(end = 9.dp)
        )
    }, floatingActionButton = {
        FloatingActionButton {
            showTaskCreateBottomSheet = true
        }
    }, contentWindowInsets = WindowInsets(bottom = 0.dp)) { paddingValues ->

        if (showTaskCreateBottomSheet) {
            CreateTaskBottomSheet(dateInfo = DateInfo(),
                task = Task(name = ""),
                category = null,
                sheetState = createTaskBottomSheetState,
                onDismissBottomSheet = {
                    showTaskCreateBottomSheet = false
                },
                openDatePickerDialog = { task, category, dateInfo ->
                    newTask = task
                    taskCategory = category
                    dateForNewTask.value = dateInfo
                    showDataPickerDialog = true
                    showTaskCreateBottomSheet = false
                },
                createTask = {
                    calendarViewModel.createTask(it)
                    scope.launch {
                        createTaskBottomSheetState.hide()
                    }.invokeOnCompletion {
                        if (!createTaskBottomSheetState.isVisible) {
                            showTaskCreateBottomSheet = false
                        }
                    }
                })
        }

        if (showDataPickerDialog) {
            TaskifyDatePickerDialog(dateForNewTask.value, onDismiss = {
                showDataPickerDialog = false
                showTaskCreateBottomSheet = true
            }, onDateChanged = { dateInfo ->
                dateForNewTask.value = dateInfo
                showDataPickerDialog = false
                showTaskCreateBottomSheet = true
            })
        }
        Column(Modifier.padding(paddingValues)) {
            Box(
                modifier = Modifier.animateContentSize(
                    animationSpec = tween(
                        300, easing = LinearOutSlowInEasing
                    )
                )
            ) {
                if (expanded) {
                    CalendarGrid(selectedDate = selectedDate, onDateSelected = {
                        selectedDate = it
                        calendarViewModel.getTasksByDate(it)
                    })
                } else {
                    WeeklyCalendar(selectedDate = selectedDate, onSelectDate = {
                        selectedDate = it
                        calendarViewModel.getTasksByDate(it)
                    })
                }
            }
            when (val state = calendarTasksState.value) {
                is CalendarTasksListState.Success -> {
                    CalendarsTasksLoaded(list = state.tasks, deleteTask = {
                        calendarViewModel.deleteTask(it)
                    }, navigateToTaskDetails = navigateToTaskDetails, completeTask = {
                        calendarViewModel.completeTask(it)
                    }, expanded = {
                        expanded = it
                    })
                }

                is CalendarTasksListState.Empty -> {
                    NoTasks(stringResource(id = R.string.you_have_free_day))
                }

                is CalendarTasksListState.Error -> {

                }
            }
        }
    }
}

@Composable
fun CalendarsTasksLoaded(
    list: List<Task>,
    navigateToTaskDetails: (Long) -> Unit,
    completeTask: (Task) -> Unit,
    deleteTask: (Task) -> Unit,
    expanded: (Boolean) -> Unit
) {

    var scrollDirection by remember { mutableStateOf(ScrollDirection.NONE) }

    if (scrollDirection == ScrollDirection.DOWN) {
        expanded(true)
    } else if (scrollDirection == ScrollDirection.UP) {
        expanded(false)
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y

                if (delta > 0) {
                    scrollDirection = ScrollDirection.DOWN
                } else if (delta < 0) {
                    scrollDirection = ScrollDirection.UP
                }
                // called when you scroll the content
                return Offset.Zero
            }
        }
    }

    LazyColumn(
        content = {
            items(list, key = { task ->
                task.id
            }) { task ->
                TaskItem(
                    task = task, showDetails = false, onComplete = {
                        completeTask(task)
                    }, navigateToTaskDetails = navigateToTaskDetails, deleteTask = deleteTask
                )
            }
        },
        verticalArrangement = Arrangement.spacedBy(15.dp),
        modifier = Modifier
            .padding(start = 9.dp, end = 9.dp, top = 20.dp)
            .nestedScroll(nestedScrollConnection)
    )

}

enum class ScrollDirection {
    UP, DOWN, NONE;
}