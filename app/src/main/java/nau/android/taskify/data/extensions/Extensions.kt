package nau.android.taskify.data.extensions

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.AlarmManagerCompat
import androidx.core.app.NotificationManagerCompat
import java.util.Calendar

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