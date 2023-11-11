package nau.android.taskify.ui.alarm.permission

import android.Manifest
import android.os.Build
import javax.inject.Inject

class AlarmPermissionImpl @Inject constructor(
    private val permissionChecker: PermissionChecker
) : AlarmPermission {

    override fun hasExactAlarmPermission(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionChecker.canScheduleExactAlarms()
        } else {
            true
        }

    override fun hasNotificationPermission(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionChecker.checkPermission(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            true
        }

    override fun shouldCheckNotificationPermission(): Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
}
