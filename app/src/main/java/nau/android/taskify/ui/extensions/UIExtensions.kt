package nau.android.taskify.ui.extensions

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.abs

fun Pair<Calendar, Calendar>.isSameDay(): Boolean {
    return this.first.get(Calendar.YEAR) == this.second.get(Calendar.YEAR) && this.first.get(
        Calendar.MONTH
    ) == this.second.get(
        Calendar.MONTH
    ) && this.first.get(Calendar.DAY_OF_MONTH) == this.second.get(Calendar.DAY_OF_MONTH)
}

fun Pair<Date, Date>.getDateDifferenceInDays(): Long {
    val time1 = this.first.time
    val time2 = this.second.time

    val duration = abs(time1 - time2)

    return TimeUnit.MILLISECONDS.toDays(duration)
}

fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    clickable(indication = null,
        interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}

fun Pair<Int, Int>.formatToAmPm(): String {
    val hour = this.first
    val minute = this.second
    val period = if (hour < 12) "AM" else "PM"
    val hourOfDay = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
    val minuteStr = if (minute < 10) "0$minute" else minute.toString()
    return "$hourOfDay:$minuteStr $period"
}

fun Calendar.formatTaskifyDate(time: String = "", showDays: Boolean = true): String {
    val currentCalendar = Calendar.getInstance()

    val monthFormatter = SimpleDateFormat("MMM dd", Locale.getDefault())
    val yearFormatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    return if (currentCalendar[Calendar.YEAR] == this[Calendar.YEAR] &&
        currentCalendar[Calendar.MONTH] == this[Calendar.MONTH]
        && currentCalendar[Calendar.DAY_OF_MONTH] == this[Calendar.DAY_OF_MONTH]
    ) {
        "Today" + if (time.isNotEmpty()) ", $time" else ""
    } else if (this[Calendar.YEAR] != currentCalendar[Calendar.YEAR]) {
        yearFormatter.format(this.time) + if (time.isNotEmpty()) ", $time" else ""
    } else {
        if (this[Calendar.MONTH] == currentCalendar[Calendar.MONTH] && this[Calendar.DAY_OF_MONTH] + 1 == currentCalendar[Calendar.DAY_OF_MONTH]) {
            "Yesterday" + if (time.isNotEmpty()) ", $time" else ""
        } else if (this[Calendar.MONTH] == currentCalendar[Calendar.MONTH] && this[Calendar.DAY_OF_MONTH] == currentCalendar[Calendar.DAY_OF_MONTH] + 1) {
            "Tomorrow" + if (time.isNotEmpty()) ", $time" else ""
        } else {
            val d = monthFormatter.format(this.time) + if (time.isNotEmpty()) ", $time" else time
            if (showDays) {
                if (currentCalendar.after(this)) {
                    d + ", ${
                        Pair(
                            currentCalendar.time,
                            this.time
                        ).getDateDifferenceInDays()
                    }d overdue"
                } else {
                    d + ", ${Pair(currentCalendar.time, this.time).getDateDifferenceInDays()}d left"
                }
            }
            d
        }
    }
}

fun Context.toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_LONG)