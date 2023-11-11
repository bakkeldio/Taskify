package nau.android.taskify.ui.alarm.permission

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import nau.android.taskify.data.extensions.getAlarmManager
import javax.inject.Inject

class PermissionCheckerImpl @Inject constructor(@ApplicationContext private val context: Context) : PermissionChecker {

    override fun checkPermission(permission: String): Boolean =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    override fun canScheduleExactAlarms(): Boolean {
        val alarmManager = context.getAlarmManager() ?: return false
        return alarmManager.canScheduleExactAlarms()
    }
}
