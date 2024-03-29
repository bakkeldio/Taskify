package nau.android.taskify.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import nau.android.taskify.R
import nau.android.taskify.ui.model.DateInfo
import nau.android.taskify.ui.enums.OptionalDate
import nau.android.taskify.ui.enums.ReminderType
import nau.android.taskify.ui.enums.TaskRepeatInterval
import nau.android.taskify.ui.extensions.addMonth
import nau.android.taskify.ui.extensions.formatDateInPattern
import nau.android.taskify.ui.extensions.isSameDay
import nau.android.taskify.ui.extensions.minusMonth
import nau.android.taskify.ui.extensions.noRippleClickable
import nau.android.taskify.ui.theme.TaskifyTheme
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskifyDatePickerDialog(
    dateInfo: DateInfo, onDismiss: () -> Unit, onDateChanged: (DateInfo) -> Unit
) {
    var selectedDate by remember {
        mutableStateOf(dateInfo.date ?: Calendar.getInstance())
    }
    var openTimePickerContent by remember {
        mutableStateOf(false)
    }

    var selectedReminders by remember {
        mutableStateOf(dateInfo.reminderTypes)
    }

    var contentTypes by remember {
        mutableStateOf(ContentTypeForDialog.DatePickerContent)
    }

    var selectedRepeatIntervalType by remember {
        mutableStateOf(dateInfo.repeatInterval)
    }

    var timeIncluded by remember {
        mutableStateOf(dateInfo.timeIncluded)
    }

    if (openTimePickerContent) {
        TaskifyTimePickerDialog(onDismiss = {
            openTimePickerContent = false
        },
            initialMinute = getMinutes(selectedDate, timeIncluded),
            initialHour = getHourOfTheDay(selectedDate, timeIncluded),
            onConfirm = { hourMinute ->
                openTimePickerContent = false
                val newDateWithTime = selectedDate.clone() as Calendar
                newDateWithTime.set(Calendar.HOUR_OF_DAY, hourMinute.first)
                newDateWithTime.set(Calendar.MINUTE, hourMinute.second)
                selectedDate = newDateWithTime
                timeIncluded = true
            })
    }

    AlertDialog(
        onDismissRequest = onDismiss, content = {

            when (contentTypes) {
                ContentTypeForDialog.DatePickerContent -> {
                    MainDatePickerContent(selectedDate,
                        timeIncluded,
                        selectedReminders,
                        selectedRepeatIntervalType,
                        {
                            selectedDate = it
                        },
                        onDismiss,
                        onConfirmDate = { date ->
                            onDateChanged(
                                DateInfo(
                                    date,
                                    timeIncluded,
                                    selectedRepeatIntervalType,
                                    selectedReminders
                                )
                            )
                        },
                        openTimePicker = {
                            openTimePickerContent = true
                        },
                        openReminder = {
                            contentTypes = ContentTypeForDialog.ReminderContent
                        },
                        openRepeatIntervalScreen = {
                            contentTypes = ContentTypeForDialog.RepeatIntervalContent
                        },
                        clearDate = {
                            onDateChanged(DateInfo())
                        },
                        clearReminders = {
                            selectedReminders = emptyList()
                        },
                        clearRepeatInterval = {
                            selectedRepeatIntervalType = TaskRepeatInterval.NONE
                        },
                        clearTime = {
                            timeIncluded = false
                        })
                }

                ContentTypeForDialog.ReminderContent -> {
                    RemainderContent(selectedReminders = selectedReminders, clearReminders = {
                        selectedReminders = emptyList()
                    }, addReminder = {
                        selectedReminders = selectedReminders.toMutableList().apply {
                            add(it)
                        }
                    }, removeReminder = {
                        selectedReminders = selectedReminders.toMutableList().apply {
                            remove(it)
                        }
                    }) {
                        contentTypes = ContentTypeForDialog.DatePickerContent
                    }
                }

                ContentTypeForDialog.RepeatIntervalContent -> {
                    RepeatIntervalContent(selectedRepeatInterval = selectedRepeatIntervalType,
                        selectRepeatInterval = { newRepeatInterval ->
                            selectedRepeatIntervalType = newRepeatInterval
                            contentTypes = ContentTypeForDialog.DatePickerContent
                        }) {
                        contentTypes = ContentTypeForDialog.DatePickerContent
                    }
                }
            }

        }, modifier = Modifier
            .fillMaxWidth(0.9f)
            .wrapContentHeight()
            .background(
                color = MaterialTheme.colorScheme.background, shape = RoundedCornerShape(5.dp)
            ), properties = DialogProperties(usePlatformDefaultWidth = false)
    )

}

@Composable
private fun MainDatePickerContent(
    selectedDate: Calendar,
    timeIncluded: Boolean,
    selectedRemainders: List<ReminderType>,
    selectedRepeatInterval: TaskRepeatInterval,
    onDateSelected: (Calendar?) -> Unit,
    onDismiss: () -> Unit,
    onConfirmDate: (Calendar) -> Unit,
    openTimePicker: () -> Unit,
    openReminder: () -> Unit,
    openRepeatIntervalScreen: () -> Unit,
    clearReminders: () -> Unit,
    clearTime: () -> Unit,
    clearRepeatInterval: () -> Unit,
    clearDate: () -> Unit
) {
    Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp)
        ) {
            TextButton(onClick = {
                clearDate()
            }, modifier = Modifier.align(Alignment.CenterStart)) {
                Text(
                    text = stringResource(id = R.string.clear),
                    color = MaterialTheme.colorScheme.error
                )
            }
            Text(
                text = stringResource(id = R.string.date_time_title),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        CalendarHeader(currentMonth = selectedDate, onMonthChange = { newMonth ->
            onDateSelected(newMonth)
        })
        CalendarGrid(selectedDate, onDateSelected)

        Column {

            OptionalDateSelection(OptionalDate.fromCalendar(selectedDate),
                selectedDatePreference = {
                    onDateSelected(OptionalDate.getDate(it))
                })


            DateSection.values().forEach { section ->
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp, end = 15.dp, top = 15.dp)
                    .clickable {
                        when (section) {
                            DateSection.TIME -> openTimePicker()
                            DateSection.REMINDERS -> openReminder()
                            DateSection.REPEAT_INTERVAL -> openRepeatIntervalScreen()
                        }
                    }) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(vertical = 5.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = section.icon), contentDescription = null
                        )
                        Text(
                            text = stringResource(id = section.titleR),
                            modifier = Modifier.padding(start = 5.dp)
                        )
                    }
                    Row(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when (section) {
                                DateSection.TIME -> {
                                    if (timeIncluded) formatToAmPmTime(
                                        selectedDate.get(Calendar.HOUR_OF_DAY),
                                        selectedDate.get(Calendar.MINUTE)
                                    ) else section.none
                                }

                                DateSection.REPEAT_INTERVAL -> {
                                    selectedRepeatInterval.title
                                }

                                DateSection.REMINDERS -> {
                                    if (selectedRemainders.isEmpty()) section.none
                                    else if (selectedRemainders.size == 1) selectedRemainders.first().title else "${selectedRemainders.size} remainders"
                                }
                            }, style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        when (section) {
                            DateSection.TIME -> {
                                IconForDateSection(hasInfo = timeIncluded) {
                                    clearTime()
                                }
                            }

                            DateSection.REPEAT_INTERVAL -> {
                                IconForDateSection(hasInfo = selectedRepeatInterval != TaskRepeatInterval.NONE) {
                                    clearRepeatInterval()
                                }
                            }

                            DateSection.REMINDERS -> {
                                IconForDateSection(hasInfo = selectedRemainders.isNotEmpty()) {
                                    clearReminders()
                                }
                            }
                        }
                    }
                }

            }
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround
            ) {
                TextButton(onClick = {
                    onDismiss()
                }) {
                    Text(text = stringResource(id = R.string.cancel), fontSize = 16.sp)
                }
                TextButton(onClick = {
                    onConfirmDate(selectedDate)
                }) {
                    Text(text = stringResource(id = R.string.done), fontSize = 16.sp)
                }

            }

        }

    }
}

@Composable
private fun RepeatIntervalContent(
    selectedRepeatInterval: TaskRepeatInterval,
    selectRepeatInterval: (TaskRepeatInterval) -> Unit,
    onDismiss: () -> Unit
) {
    Column(modifier = Modifier.padding(top = 10.dp, start = 15.dp, end = 15.dp)) {

        Box(modifier = Modifier.fillMaxWidth()) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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

        TaskRepeatInterval.values().forEach { type ->
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
private fun RemainderContent(
    selectedReminders: List<ReminderType>,
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
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                text = "Reminder is " + if (selectedReminders.isNotEmpty()) "on" else "off",
                style = MaterialTheme.typography.titleMedium
            )
            Switch(checked = selectedReminders.isNotEmpty(), onCheckedChange = { checked ->
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
                    checked = selectedReminders.contains(type), onCheckedChange = {
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

@Composable
private fun CalendarHeader(currentMonth: Calendar, onMonthChange: (Calendar) -> Unit) {

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 15.dp, end = 15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        content = {
            IconButton(onClick = {
                onMonthChange(currentMonth.minusMonth())
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Previous Month"
                )
            }

            Text(
                text = currentMonth.formatDateInPattern("MMMM yyyy"),
                style = MaterialTheme.typography.titleMedium
            )

            IconButton(onClick = {
                onMonthChange(currentMonth.addMonth())
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Next Month"
                )
            }
        })
}

@Composable
fun WeeklyCalendar(selectedDate: Calendar, onSelectDate: (Calendar) -> Unit) {

    val isCurrentMonth: (Calendar) -> Boolean = { date ->
        date.get(Calendar.MONTH) == selectedDate.get(Calendar.MONTH)
    }
    val dayOfTheWeek = selectedDate.get(Calendar.DAY_OF_WEEK)

    val offset = dayOfTheWeek - Calendar.SUNDAY

    val clonedDate = selectedDate.clone() as Calendar

    clonedDate[Calendar.DAY_OF_MONTH] -= (offset + 1)

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        content = {
            WeekNames()
            items(7) { index ->
                val currentDate = clonedDate.clone() as Calendar
                currentDate.add(Calendar.DAY_OF_MONTH, index + 1)
                DayCell(
                    date = currentDate,
                    isSelected = Pair(selectedDate, currentDate).isSameDay(),
                    isCurrentMonth = isCurrentMonth(currentDate),
                    onDateSelected = onSelectDate
                )
                //clonedDate.add(Calendar.DAY_OF_MONTH, 1)
            }
        },
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier.padding(start = 9.dp, end = 9.dp)
    )
}

@Composable
fun CalendarGrid(selectedDate: Calendar, onDateSelected: (Calendar) -> Unit) {
    val firstDay = selectedDate.clone() as Calendar
    firstDay.set(Calendar.DAY_OF_MONTH, 1)
    val firstDayOfWeek = firstDay.get(Calendar.DAY_OF_WEEK)
    val dayOffset = firstDayOfWeek - Calendar.SUNDAY
    val daysInMonth = selectedDate.getActualMaximum(Calendar.DAY_OF_MONTH)
    val isCurrentMonth: (Calendar) -> Boolean = { date ->
        date.get(Calendar.MONTH) == selectedDate.get(Calendar.MONTH)
    }
    val daysInPreviousMonth = selectedDate.clone() as Calendar
    daysInPreviousMonth.add(Calendar.MONTH, -1)
    val daysInPreviousMonthCount = daysInPreviousMonth.getActualMaximum(Calendar.DAY_OF_MONTH)

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 9.dp, end = 9.dp), verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {

        WeekNames()

        //Cells for the days of the previous month
        for (i in 1..dayOffset) {
            val day = daysInPreviousMonthCount - dayOffset + i
            val date = daysInPreviousMonth.clone() as Calendar
            date.set(Calendar.DAY_OF_MONTH, day)
            item {
                DayCell(
                    date = date,
                    isSelected = false,
                    isCurrentMonth = false,
                    onDateSelected = onDateSelected
                )
            }
        }

        //Cells for the days of the current month
        for (day in 1..daysInMonth) {
            val currentDate = selectedDate.clone() as Calendar
            currentDate.set(Calendar.DAY_OF_MONTH, day)
            val isSelected = Pair(selectedDate, currentDate).isSameDay()
            if (isCurrentMonth(currentDate)) {
                item {
                    DayCell(
                        date = currentDate,
                        isSelected = isSelected,
                        isCurrentMonth = true,
                        onDateSelected = onDateSelected
                    )
                }
            }
        }

        // Cells for the days of the upcoming month
        for (i in 1..(42 - dayOffset - daysInMonth) % 7) {
            val date = selectedDate.clone() as Calendar
            date[Calendar.MONTH] += 1
            date.set(Calendar.DAY_OF_MONTH, i)
            item {
                DayCell(
                    date = date,
                    isSelected = false,
                    onDateSelected = onDateSelected,
                    isCurrentMonth = false
                )
            }
        }
    }
}

fun LazyGridScope.WeekNames() {
    val dayNames = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
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
}

fun formatToAmPmTime(hour: Int, minute: Int): String {
    val period = if (hour < 12) "AM" else "PM"
    val hourOfDay = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
    val minuteStr = if (minute < 10) "0$minute" else minute.toString()

    return "$hourOfDay:$minuteStr $period"
}


@Composable
private fun DayCell(
    date: Calendar, isSelected: Boolean, isCurrentMonth: Boolean, onDateSelected: (Calendar) -> Unit
) {

    Box(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .aspectRatio(1f)
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = CircleShape
            )
            .noRippleClickable { onDateSelected(date) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.get(Calendar.DAY_OF_MONTH).toString(),
            style = if (isSelected) MaterialTheme.typography.bodyMedium.copy(color = Color.White)
            else MaterialTheme.typography.bodyMedium.copy(color = if (isCurrentMonth) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.outline),
            modifier = Modifier.padding(4.dp),
            maxLines = 1
        )
    }

}

private enum class ContentTypeForDialog {
    DatePickerContent, ReminderContent, RepeatIntervalContent
}

private enum class DateSection(val icon: Int, val titleR: Int, val none: String = "None") {
    TIME(R.drawable.ic_alarm, R.string.alarm_time), REMINDERS(
        R.drawable.ic_notification,
        R.string.reminder
    ),
    REPEAT_INTERVAL(R.drawable.ic_repeat, R.string.repeat_interval)
}

private fun getHourOfTheDay(calendar: Calendar, timeIncluded: Boolean = false): Int {
    return if (timeIncluded) calendar.get(Calendar.HOUR_OF_DAY) else Calendar.getInstance()
        .get(Calendar.HOUR_OF_DAY)
}

private fun getMinutes(calendar: Calendar, timeIncluded: Boolean = false): Int {
    return if (timeIncluded) calendar.get(Calendar.MINUTE) else Calendar.getInstance()
        .get(Calendar.MINUTE)
}

@Composable
private fun IconForDateSection(hasInfo: Boolean, clicked: () -> Unit) = if (hasInfo) Icon(
    painter = painterResource(id = R.drawable.ic_cross),
    tint = MaterialTheme.colorScheme.outline,
    contentDescription = "Remove",
    modifier = Modifier.clickable {
        clicked()
    }) else {
    Icon(
        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        contentDescription = "Follow"
    )
}

@Preview(showBackground = true, device = Devices.PIXEL_4)
@Composable
fun CalendarPreview() {
    TaskifyTheme {
        CalendarGrid(selectedDate = Calendar.getInstance(), onDateSelected = {})
    }
}