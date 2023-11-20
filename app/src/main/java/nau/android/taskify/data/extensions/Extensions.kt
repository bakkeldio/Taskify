package nau.android.taskify.data.extensions

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.AlarmManagerCompat
import androidx.core.app.NotificationManagerCompat
import nau.android.taskify.ui.enums.Priority
import nau.android.taskify.ui.extensions.isItOverdue
import nau.android.taskify.ui.extensions.isItToday
import nau.android.taskify.ui.extensions.isItTomorrow
import nau.android.taskify.ui.model.Task
import java.util.Calendar


fun Task.isItImportant(): Boolean {
    return (priority == Priority.HIGH) || (priority == Priority.MEDIUM)
}

fun Task.isItUrgent(): Boolean {
    if (dueDate == null) {
        return false
    }
    return dueDate.isItOverdue() || dueDate.isItToday() || dueDate.isItTomorrow()
}

fun Context.getAlarmManager() = getSystemService(Context.ALARM_SERVICE) as? AlarmManager

fun Context.setExactAlarm(
    time: Long,
    pendingIntent: PendingIntent,
    alarmType: Int = AlarmManager.RTC_WAKEUP
) {
    val currentTime = Calendar.getInstance().timeInMillis

    if (time <= currentTime) {
        return
    }
    val alarmManager = getAlarmManager()
    alarmManager?.apply {
        AlarmManagerCompat.setExactAndAllowWhileIdle(this, alarmType, time, pendingIntent)
    }

}

fun Context.cancelAlarm(pendingIntent: PendingIntent) {
    val alarmManager = getAlarmManager()
    alarmManager?.cancel(pendingIntent)
}

fun Context.getNotificationManager() =
    getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

fun Int.toStringColor() =
    String.format(HexFormat, HexWhite and this)

private const val HexFormat = "#%06X"

private const val HexWhite = 0xFFFFFF
