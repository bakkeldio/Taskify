package nau.android.taskify.ui.task

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nau.android.taskify.R
import nau.android.taskify.ui.DateInfo
import nau.android.taskify.ui.category.Category
import nau.android.taskify.ui.category.ChangeCategoryBottomSheet
import nau.android.taskify.ui.dialogs.TaskifyDatePickerDialog
import nau.android.taskify.ui.dialogs.formatToAmPmTime
import nau.android.taskify.ui.enums.RepeatIntervalType
import nau.android.taskify.ui.extensions.getDateDifferenceInDays
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


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
            }, onDateChanged = { dateInfo ->
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

@Composable
private fun BoxScope.DropDownMenuForCategories(
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

private fun getFormattedDate(date: Calendar, time: String): String {
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
                d + ", ${Pair(currentCalendar.time, date.time).getDateDifferenceInDays()}d overdue"
            } else {
                d + ", ${Pair(currentCalendar.time, date.time).getDateDifferenceInDays()}d left"
            }
        }
    }
}


