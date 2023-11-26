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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import nau.android.taskify.R
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import nau.android.taskify.FloatingActionButton
import nau.android.taskify.TaskItem
import nau.android.taskify.ui.DateInfo
import nau.android.taskify.ui.customElements.NoTasks
import nau.android.taskify.ui.dialogs.CalendarGrid
import nau.android.taskify.ui.dialogs.WeeklyCalendar
import nau.android.taskify.ui.extensions.addMonth
import nau.android.taskify.ui.extensions.addWeek
import nau.android.taskify.ui.extensions.formatDateInPattern
import nau.android.taskify.ui.extensions.minusMonth
import nau.android.taskify.ui.extensions.minusWeek
import nau.android.taskify.ui.extensions.noRippleClickable
import nau.android.taskify.ui.model.Task
import nau.android.taskify.ui.tasksList.CreateTaskBottomSheet
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskifyCalendar(calendarViewModel: CalendarViewModel = hiltViewModel()) {


    var selectedDate by remember {
        mutableStateOf(Calendar.getInstance())
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
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "arrow left",
                        Modifier
                            .size(28.dp)
                            .clickable {
                                selectedDate = if (expanded) {
                                    selectedDate.minusMonth()
                                } else {
                                    selectedDate.minusWeek()
                                }
                            }
                    )
                    Text(text = selectedDate.formatDateInPattern("MMM YYYY").uppercase())
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "arrow right",
                        Modifier
                            .size(28.dp)
                            .clickable {
                                selectedDate = if (expanded) {
                                    selectedDate.addMonth()
                                } else {
                                    selectedDate.addWeek()
                                }
                            }
                    )
                }
            }, actions = {
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
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "arrow up",
                        modifier = Modifier
                            .noRippleClickable {
                                expanded = true
                            }
                            .size(28.dp)
                    )
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
            CreateTaskBottomSheet(
                dateInfo = DateInfo(),
                task = Task(name = ""),
                category = null,
                sheetState = createTaskBottomSheetState,
                onDismissBottomSheet = {
                    showTaskCreateBottomSheet = false
                },
                openDatePickerDialog = { task, category, dateInfo ->

                },
                createTask = {
                    scope.launch {
                        createTaskBottomSheetState.hide()
                    }.invokeOnCompletion {
                        if (!createTaskBottomSheetState.isVisible) {
                            showTaskCreateBottomSheet = false
                        }
                    }
                }
            )
        }
        Column(Modifier.padding(paddingValues)) {
            Box(
                modifier = Modifier.animateContentSize(
                    animationSpec = tween(
                        300,
                        easing = LinearOutSlowInEasing
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
                    CalendarsTasksLoaded(list = state.tasks, navigateToTaskDetails = {}) {

                    }
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
    completeTask: () -> Unit
) {
    LazyColumn(
        content = {
            items(list, key = {
                it.id
            }) { task ->
                TaskItem(
                    task = task,
                    showDetails = false,
                    onComplete = completeTask,
                    navigateToTaskDetails = navigateToTaskDetails
                )
            }
        },
        verticalArrangement = Arrangement.spacedBy(15.dp),
        modifier = Modifier.padding(start = 9.dp, end = 9.dp, top = 20.dp)
    )

}