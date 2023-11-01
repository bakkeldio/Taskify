package nau.android.taskify.ui.extensions

import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit
import kotlin.math.abs

fun Pair<Calendar, Calendar>.isSameDay(): Boolean {
    return this.first.get(Calendar.YEAR) == this.second.get(Calendar.YEAR) && this.first.get(Calendar.MONTH) == this.second.get(
        Calendar.MONTH
    ) && this.first.get(Calendar.DAY_OF_MONTH) == this.second.get(Calendar.DAY_OF_MONTH)
}
fun Pair<Date, Date>.getDateDifferenceInDays(): Long {
    val time1 = this.first.time
    val time2 = this.second.time

    val duration = abs(time1 - time2)

    return TimeUnit.MILLISECONDS.toDays(duration)
}