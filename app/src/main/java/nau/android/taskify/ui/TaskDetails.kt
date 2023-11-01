package nau.android.taskify.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import nau.android.taskify.R
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nau.android.taskify.data.DateInfo
import nau.android.taskify.data.RepeatIntervalType
import nau.android.taskify.data.TaskifyDatePickerDialog
import nau.android.taskify.data.formatToAmPmTime
import java.text.SimpleDateFormat
import java.time.Year
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.abs


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetails(navController: NavController) {

    var completed by remember {
        mutableStateOf(false)
    }

    var taskTitle by remember {
        mutableStateOf("")
    }

    var taskDescription by remember {
        mutableStateOf("")
    }
    var taskPriority by remember {
        mutableStateOf(TaskPriority.NoPriority)
    }

    var showCategoryBottomSheet by remember {
        mutableStateOf(false)
    }

    var showTaskDetailBottomSheet by remember {
        mutableStateOf(false)
    }

    var selectedDate by remember {
        mutableStateOf(Calendar.getInstance())
    }

    var selectedTime by remember {
        mutableStateOf("")
    }

    var taskDateInfo by remember {
        mutableStateOf(DateInfo())
    }

    var selectedRepeatInterval by remember {
        mutableStateOf(RepeatIntervalType.None)
    }

    var taskCategory by remember {
        mutableStateOf(Category("uid2", "No category"))
    }

    val categorySheetState = rememberModalBottomSheetState()

    val taskDetailSheetState = rememberModalBottomSheetState()

    val showDatePickerDialog = remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()


    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = taskCategory.name, style = MaterialTheme.typography.titleMedium)
                    Icon(imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            showCategoryBottomSheet = true
                        })
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

        if (showCategoryBottomSheet) {
            ChangeCategoryBottomSheet(
                currentCategoryId = taskCategory.uid, onCategoryChanged = { newCategory ->
                    taskCategory = newCategory
                    scope.launch {
                        delay(300)
                        categorySheetState.hide()
                    }.invokeOnCompletion {
                        if (!categorySheetState.isVisible) {
                            showCategoryBottomSheet = false
                        }
                    }
                }, categorySheetState
            ) {
                showCategoryBottomSheet = false
            }
        }

        if (showTaskDetailBottomSheet) {
            TaskDetailsBottomSheet(onDismissRequest = {
                showTaskDetailBottomSheet = false
            }, modalBottomSheetState = taskDetailSheetState) {
                scope.launch {
                    taskDetailSheetState.hide()
                }.invokeOnCompletion {
                    if (!taskDetailSheetState.isVisible) {
                        showTaskDetailBottomSheet = false
                    }
                }
            }
        }

        if (showDatePickerDialog.value) {
            TaskifyDatePickerDialog(onDismiss = {
                showDatePickerDialog.value = false
            }, { dateInfo ->

                taskDateInfo = dateInfo
                showDatePickerDialog.value = false
                selectedTime = dateInfo.time?.let {
                    formatToAmPmTime(it.first, it.second)
                } ?: ""
                selectedRepeatInterval = dateInfo.repeatInterval
                selectedDate = dateInfo.date
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
                Checkbox(checked = completed, onCheckedChange = {
                    completed = it
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
                        text = taskDateInfo.date?.let { date ->
                            applyColorForDateTime(
                                selectedDate = date,
                                date = getFormattedDate(date, taskDateInfo.time?.let {
                                    formatToAmPmTime(it.first, it.second)
                                } ?: ""))
                        } ?: buildAnnotatedString { append("Date & Time") },
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (taskDateInfo.repeatInterval != RepeatIntervalType.None) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_repeat),
                                contentDescription = "Repeat",
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = taskDateInfo.repeatInterval.title,
                                modifier = Modifier.padding(start = 5.dp),
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.outline)
                            )
                        }
                    }
                }
                DropDownMenuForCategories(taskPriority) { newPriority ->
                    taskPriority = newPriority
                }
            }
            TextField(value = taskTitle, onValueChange = { newValue ->
                taskTitle = newValue
            }, placeholder = {
                Text(text = "What would you like to do?")
            }, colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent
            ), maxLines = 2
            )
            TextField(
                value = taskDescription, onValueChange = { newDescription ->
                    taskDescription = newDescription
                }, modifier = Modifier.padding(top = 5.dp), colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent
                )
            )


        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskifyTimePickerDialog(
    initialMinute: Int,
    initialHour: Int,
    onDismiss: () -> Unit,
    onConfirm: (Triple<Int, Int, Boolean>) -> Unit
) {
    val timePickerState =
        rememberTimePickerState(initialHour = initialHour, initialMinute = initialMinute)

    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = {

        }, modifier = Modifier
            .fillMaxWidth(0.75f)
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(5.dp)
            )
    ) {

        Column(Modifier.padding(20.dp)) {
            TimeInput(
                state = timePickerState,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                TextButton(onClick = {
                    onDismiss()
                }) {
                    Text(text = "Cancel", fontSize = 16.sp)
                }
                TextButton(onClick = {
                    onConfirm(Triple(timePickerState.hour, timePickerState.minute, true))
                }) {
                    Text(text = "Done", fontSize = 16.sp)
                }

            }
        }
    }
}

@Composable
fun BoxScope.DropDownMenuForCategories(
    taskPriority: TaskPriority, changeTaskPriority: (TaskPriority) -> Unit
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
                tint = taskPriority.color
            )
        }
        DropdownMenu(expanded = dropDownMenuOpen, onDismissRequest = {
            dropDownMenuOpen = false
        }) {
            TaskPriority.values().forEach { priority ->
                DropdownMenuItem(text = {
                    Text(text = "${priority.name} priority")
                }, onClick = {


                }, trailingIcon = {
                    RadioButton(selected = taskPriority == priority, onClick = {
                        changeTaskPriority(priority)
                        dropDownMenuOpen = false
                    })
                }, leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_flag),
                        contentDescription = "Priority",
                        tint = priority.color
                    )
                })
            }
        }
    }
}

@Composable
fun applyColorForDateTime(selectedDate: Calendar, date: String): AnnotatedString {
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

fun getFormattedDate(date: Calendar, time: String): String {
    val currentCalendar = Calendar.getInstance()

    val monthFormatter = SimpleDateFormat("MMM dd", Locale.getDefault())
    val yearFormatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    return if (currentCalendar[Calendar.YEAR] == date[Calendar.YEAR] &&
        currentCalendar[Calendar.MONTH] == date[Calendar.MONTH]
        && currentCalendar[Calendar.DAY_OF_MONTH] == date[Calendar.DAY_OF_MONTH]
    ) {
        "Today" + if (time.isNotEmpty()) ", $time" else ""
    } else if (date[Calendar.YEAR] != currentCalendar[Calendar.YEAR]) {
        yearFormatter.format(date.time) + if (time.isNotEmpty()) ", $time" else ""
    } else {
        if (date[Calendar.MONTH] == currentCalendar[Calendar.MONTH] && date[Calendar.DAY_OF_MONTH] + 1 == currentCalendar[Calendar.DAY_OF_MONTH]) {
            "Yesterday" + if (time.isNotEmpty()) ", $time" else ""
        } else if (date[Calendar.MONTH] == currentCalendar[Calendar.MONTH] && date[Calendar.DAY_OF_MONTH] == currentCalendar[Calendar.DAY_OF_MONTH] + 1) {
            "Tomorrow" + if (time.isNotEmpty()) ", $time" else ""
        } else {
            val d = monthFormatter.format(date.time) + if (time.isNotEmpty()) ", $time" else time
            if (currentCalendar.after(date)) {
                d + ", ${getDifference(currentCalendar.time, date.time)}d overdue"
            } else {
                d + ", ${getDifference(currentCalendar.time, date.time)}d left"
            }
        }
    }
}

private fun getDifference(date1: Date, date2: Date): Long {
    val time1 = date1.time
    val time2 = date2.time

    val duration = abs(time1 - time2)

    return TimeUnit.MILLISECONDS.toDays(duration)
}


