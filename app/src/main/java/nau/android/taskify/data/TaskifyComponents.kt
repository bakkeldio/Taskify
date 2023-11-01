package nau.android.taskify.data

import android.app.TaskInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import java.text.SimpleDateFormat
import nau.android.taskify.R
import nau.android.taskify.ui.TaskifyTimePickerDialog
import java.util.Calendar
import java.util.Locale

@Composable
fun TaskifySearchBar(modifier: Modifier, value: String = "", onValueChange: (String) -> Unit) {
    TextField(
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search, contentDescription = null
            )
        },
        placeholder = { Text(text = "Search") },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 13.dp),
        shape = RoundedCornerShape(10.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        maxLines = 1

    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallerSearchBar(modifier: Modifier, value: String = "", onValueChange: (String) -> Unit) {

    BasicTextField(modifier = modifier
        .fillMaxWidth()
        .height(45.dp),
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        singleLine = true,
        decorationBox = { innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = value,
                innerTextField = {
                    Box(
                        modifier = Modifier.fillMaxHeight(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        innerTextField()
                    }
                },
                singleLine = true,
                placeholder = {
                    Text(text = "Search")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search, contentDescription = null
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                contentPadding = TextFieldDefaults.contentPaddingWithoutLabel(
                    top = 0.dp,
                    bottom = 0.dp,
                ),
                visualTransformation = VisualTransformation.None,
                interactionSource = MutableInteractionSource(),
                enabled = true,
                shape = RoundedCornerShape(10.dp)
            )
        })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskifyDatePickerDialog(
    onDismiss: () -> Unit,
    onConfirmDateInfo: (DateInfo) -> Unit
) {
    var selectedDate by remember {
        mutableStateOf(Calendar.getInstance())
    }
    var openTimePickerDialog by remember {
        mutableStateOf(false)
    }

    var selectedReminders by remember {
        mutableStateOf(emptySet<ReminderType>())
    }

    var dialogTypes by remember {
        mutableStateOf(DialogType.DatePickerDialog)
    }

    var selectedRepeatIntervalType by remember {
        mutableStateOf(RepeatIntervalType.None)
    }

    var time by remember {
        mutableStateOf(
            Triple(
                selectedDate[Calendar.HOUR], selectedDate[Calendar.MINUTE], false
            )
        )
    }

    if (openTimePickerDialog) {
        TaskifyTimePickerDialog(onDismiss = {
            openTimePickerDialog = false
        },
            initialMinute = time.second,
            initialHour = time.first,
            onConfirm = { hourMinute ->
                openTimePickerDialog = false

                time = hourMinute
            })
    }

    AlertDialog(
        onDismissRequest = onDismiss, content = {

            when (dialogTypes) {
                DialogType.DatePickerDialog -> {
                    DatePicker(selectedDate,
                        time,
                        selectedReminders,
                        selectedRepeatIntervalType,
                        {
                            selectedDate = it
                        },
                        onDismiss,
                        onConfirmDate = { date ->
                            onConfirmDateInfo(
                                DateInfo(
                                    date,
                                    Pair(time.first, time.second),
                                    selectedRepeatIntervalType,
                                    selectedReminders
                                )
                            )
                        },
                        openTimePicker = {
                            openTimePickerDialog = true
                        },
                        openReminder = {
                            dialogTypes = DialogType.ReminderDialog
                        }, openRepeatIntervalScreen = {
                            dialogTypes = DialogType.RepeatIntervalDialog
                        })
                }

                DialogType.ReminderDialog -> {
                    RemainderSelection(selectedReminders = selectedReminders, clearReminders = {
                        selectedReminders = selectedReminders.toMutableSet().apply {
                            clear()
                        }
                    }, addReminder = {

                        selectedReminders = selectedReminders.toMutableSet().apply {
                            add(it)
                        }
                    }, removeReminder = {
                        selectedReminders = selectedReminders.toMutableSet().apply {
                            remove(it)
                        }
                    }) {
                        dialogTypes = DialogType.DatePickerDialog
                    }
                }

                DialogType.RepeatIntervalDialog -> {
                    RepeatScreen(
                        selectedRepeatInterval = selectedRepeatIntervalType,
                        selectRepeatInterval = { newRepeatInterval ->
                            selectedRepeatIntervalType = newRepeatInterval
                            dialogTypes = DialogType.DatePickerDialog
                        }) {
                        dialogTypes = DialogType.DatePickerDialog
                    }
                }
            }

        }, modifier = Modifier
            .fillMaxWidth(0.9f)
            .fillMaxHeight(0.8f)
            .background(
                color = MaterialTheme.colorScheme.background, shape = RoundedCornerShape(5.dp)
            ), properties = DialogProperties(usePlatformDefaultWidth = false)
    )

}

enum class ReminderType(val title: String) {
    OnTime("On time"), FiveMinutesBefore("5 minutes before"), TenMinutesBefore("10 minutes before"), FifteenMinutesBefore(
        "15 minutes before"
    ),
    ThirtyMinutesBefore("30 minutes before"), DayBefore("1 day before"), TwoDaysBefore("2 days before"), Customize(
        "Customize"
    );
}

enum class RepeatIntervalType(val title: String) {
    Daily("Daily"),
    EveryWeekDay("Every weekday"),
    Weekly("Weekly"),
    Monthly("Monthly"),
    Yearly("Yearly"),
    Custom("Custom"),
    None("None");
}

@Composable
private fun DatePicker(
    selectedDate: Calendar,
    time: Triple<Int, Int, Boolean>,
    selectedRemainders: Set<ReminderType>,
    selectedRepeatInterval: RepeatIntervalType,
    onDateSelected: (Calendar?) -> Unit,
    onDismiss: () -> Unit,
    onConfirmDate: (Calendar) -> Unit,
    openTimePicker: () -> Unit,
    openReminder: () -> Unit,
    openRepeatIntervalScreen: () -> Unit
) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp)
        ) {
            TextButton(onClick = {

            }, modifier = Modifier.align(Alignment.CenterStart)) {
                Text(text = "Clear", color = MaterialTheme.colorScheme.error)
            }
            Text(
                text = "Date & Time",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        CalendarHeader(currentMonth = selectedDate, onMonthChange = { newMonth ->
            onDateSelected(newMonth)
        })
        CalendarGrid(selectedDate, onDateSelected)

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

            OptionalDateSelection(
                OptionalDate.fromCalendar(selectedDate),
                selectedDatePreference = {
                    onDateSelected(OptionalDate.getDate(it))
                })

            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp, end = 15.dp, top = 15.dp)
                .clickable {
                    openTimePicker()
                }) {
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(vertical = 5.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_alarm),
                        contentDescription = null
                    )
                    Text(
                        text = "Time", modifier = Modifier.padding(start = 5.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(vertical = 5.dp)
                ) {
                    Text(
                        text = if (time.third) formatToAmPmTime(
                            time.first,
                            time.second
                        ) else "None",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        contentDescription = null
                    )
                }
            }

            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp, end = 15.dp, top = 15.dp)
                .clickable {
                    openReminder()
                }) {
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(vertical = 5.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_notification),
                        contentDescription = null
                    )
                    Text(
                        text = "Reminder", modifier = Modifier.padding(start = 5.dp)
                    )
                }
                Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                    Text(
                        text = if (selectedRemainders.isEmpty()) "None"
                        else if (selectedRemainders.size == 1) selectedRemainders.first().title else "${selectedRemainders.size} remainders",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        contentDescription = null
                    )
                }
            }

            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp, end = 15.dp, top = 15.dp)
                .clickable {
                    openRepeatIntervalScreen()
                }) {
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(vertical = 5.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_repeat),
                        contentDescription = null
                    )
                    Text(
                        text = "Repeat", modifier = Modifier.padding(start = 5.dp)
                    )
                }
                Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                    Text(
                        text = selectedRepeatInterval.title,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurfaceVariant

                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Divider(
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                TextButton(onClick = {
                    onDismiss()
                }) {
                    Text(text = "Cancel", fontSize = 16.sp)
                }
                TextButton(onClick = {
                    onConfirmDate(selectedDate)
                }) {
                    Text(text = "Done", fontSize = 16.sp)
                }

            }

        }

    }
}

@Composable
private fun RepeatScreen(
    selectedRepeatInterval: RepeatIntervalType,
    selectRepeatInterval: (RepeatIntervalType) -> Unit,
    onDismiss: () -> Unit
) {
    Column(modifier = Modifier.padding(top = 10.dp, start = 15.dp, end = 15.dp)) {

        Box(modifier = Modifier.fillMaxWidth()) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .clickable {
                        onDismiss()
                    }
                    .align(Alignment.CenterStart))
            Text(
                text = "Repeat",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        RepeatIntervalType.values().forEach { type ->
            Row(
                modifier = Modifier.padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = selectedRepeatInterval == type, onClick = {
                    selectRepeatInterval(type)
                })
                Text(text = type.title, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun RemainderSelection(
    selectedReminders: Set<ReminderType>,
    addReminder: (ReminderType) -> Unit,
    removeReminder: (ReminderType) -> Unit,
    clearReminders: () -> Unit,
    onDismiss: () -> Unit
) {

    Column(modifier = Modifier.padding(top = 10.dp)) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .clickable {
                        onDismiss()
                    }
                    .align(Alignment.CenterStart))
            Text(
                text = "Remainder",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Row(
            modifier = Modifier
                .padding(top = 20.dp)
                .padding(start = 20.dp, end = 20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Reminder is " + if (selectedReminders.isNotEmpty()) "on" else "off", style =
                MaterialTheme.typography.titleMedium
            )
            Switch(checked =
            selectedReminders.isNotEmpty(), onCheckedChange = { checked ->
                if (checked) {
                    addReminder(ReminderType.FiveMinutesBefore)
                } else {
                    clearReminders()
                }
            })
        }

        ReminderType.values().forEach { type ->
            Row(
                modifier = Modifier.padding(top = 10.dp, start = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = selectedReminders.contains(type),
                    onCheckedChange = {
                        if (selectedReminders.contains(type)) {
                            removeReminder(type)
                        } else {
                            addReminder(type)
                        }
                    }, modifier = Modifier.requiredWidth(20.dp)
                )
                Text(text = type.title, modifier = Modifier.padding(start = 10.dp))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun OptionalDateSelection(
    selectedDate: OptionalDate? = null, selectedDatePreference: (OptionalDate) -> Unit
) {

    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp, start = 15.dp, end = 15.dp),
        horizontalArrangement = Arrangement.spacedBy(15.dp)
    ) {

        OptionalDate.values().forEach {
            if (it != OptionalDate.NoSelection) {
                FilterChip(onClick = {
                    selectedDatePreference(it)
                }, label = {
                    Text(text = it.title!!)
                }, selected = selectedDate == it, border = null
                )
            }
        }
    }

}

private enum class OptionalDate(val title: String? = null) {
    Today("Today"), Tomorrow("Tomorrow"), ThreeDaysLater("Three days later"), NoSelection;

    companion object {
        fun getDate(optionalDate: OptionalDate): Calendar? {
            val calendar = Calendar.getInstance().clone() as Calendar
            return when (optionalDate) {
                Today -> calendar
                Tomorrow -> {
                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                    calendar
                }

                ThreeDaysLater -> {
                    calendar.add(Calendar.DAY_OF_MONTH, 3)
                    calendar
                }

                NoSelection -> null
            }
        }

        fun fromCalendar(calendar: Calendar): OptionalDate {
            val today = Calendar.getInstance()
            val tomorrow = Calendar.getInstance()
            tomorrow.add(Calendar.DAY_OF_MONTH, 1)
            val threeDaysLater = Calendar.getInstance()
            threeDaysLater.add(Calendar.DAY_OF_MONTH, 3)
            val weekLater = Calendar.getInstance()
            weekLater.add(Calendar.DAY_OF_MONTH, 7)

            return when {
                isSameDay(calendar, today) -> Today
                isSameDay(calendar, tomorrow) -> Tomorrow
                isSameDay(calendar, threeDaysLater) -> ThreeDaysLater
                else -> NoSelection
            }
        }

        private fun isSameDay(calendar1: Calendar, calendar2: Calendar): Boolean {
            return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) && calendar1.get(
                Calendar.MONTH
            ) == calendar2.get(Calendar.MONTH) && calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(
                Calendar.DAY_OF_MONTH
            )
        }

    }
}

@Composable
private fun CalendarHeader(currentMonth: Calendar, onMonthChange: (Calendar) -> Unit) {
    val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    val formattedDate = dateFormat.format(currentMonth.time)

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 15.dp, end = 15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        content = {
            IconButton(onClick = {
                val previousMonth = currentMonth.clone() as Calendar
                previousMonth.add(Calendar.MONTH, -1)
                onMonthChange(previousMonth)
            }) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Previous Month"
                )
            }

            Text(
                text = formattedDate, style = MaterialTheme.typography.titleMedium
            )

            IconButton(onClick = {
                val nextMonth = currentMonth.clone() as Calendar
                nextMonth.add(Calendar.MONTH, 1)
                onMonthChange(nextMonth)
            }) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Next Month"
                )
            }
        })
}


@Composable
private fun CalendarGrid(selectedDate: Calendar, onDateSelected: (Calendar) -> Unit) {
    // Calculate the day of the week for the first day of the month (0 = Sunday, 1 = Monday, ...)
    val firstDay = selectedDate.clone() as Calendar
    firstDay.set(Calendar.DAY_OF_MONTH, 1)
    val firstDayOfWeek = firstDay.get(Calendar.DAY_OF_WEEK)

    // Determine the day to start with based on the selected day of the week
    val dayOffset = (firstDayOfWeek - Calendar.MONDAY + 7) % 7

    // Calculate the number of days in the month
    val daysInMonth = selectedDate.getActualMaximum(Calendar.DAY_OF_MONTH) + 1

    val dayNames = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    val isCurrentMonth: (Calendar) -> Boolean = { date ->
        date.get(Calendar.MONTH) == selectedDate.get(Calendar.MONTH)
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 9.dp, end = 9.dp)
    ) {
        // Display day names at the top

        for (dayName in dayNames) {
            item {
                Text(
                    text = dayName, style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ), textAlign = TextAlign.Center, modifier = Modifier.padding(6.dp)
                )
            }
        }

        // Skip empty cells before the first day of the month
        repeat(dayOffset) {
            item { /* Empty cell */ }
        }

        // Display date cells for the month
        items(daysInMonth) { day ->
            val currentDate = selectedDate.clone() as Calendar
            currentDate.set(Calendar.DAY_OF_MONTH, day)

            val isSelected = isSameDay(selectedDate, currentDate)
            if (isCurrentMonth(currentDate)) {
                DayCell(date = currentDate, isSelected = isSelected, onDateSelected = {
                    onDateSelected(it)
                })
            }

        }
    }
}

fun formatToAmPmTime(hour: Int, minute: Int): String {
    val period = if (hour < 12) "AM" else "PM"
    val hourOfDay = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
    val minuteStr = if (minute < 10) "0$minute" else minute.toString()

    return "$hourOfDay:$minuteStr $period"
}


@Composable
fun DayCell(
    date: Calendar, isSelected: Boolean, onDateSelected: (Calendar) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxSize()
            .clickable { onDateSelected(date) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.get(Calendar.DAY_OF_MONTH).toString(),
            style = if (isSelected) MaterialTheme.typography.bodySmall.copy(color = Color.White)
            else MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onBackground),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                    CircleShape
                )
                .padding(4.dp)
                .fillMaxSize()
        )
    }
}


private fun isSameDay(date1: Calendar, date2: Calendar): Boolean {
    return date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR) && date1.get(Calendar.MONTH) == date2.get(
        Calendar.MONTH
    ) && date1.get(Calendar.DAY_OF_MONTH) == date2.get(Calendar.DAY_OF_MONTH)
}

private enum class DialogType {
    DatePickerDialog,
    ReminderDialog,
    RepeatIntervalDialog
}

data class DateInfo(
    val date: Calendar? = null,
    val time: Pair<Int, Int>? = null,
    val repeatInterval: RepeatIntervalType = RepeatIntervalType.None,
    val reminderTypes: Set<ReminderType> = emptySet()
)
