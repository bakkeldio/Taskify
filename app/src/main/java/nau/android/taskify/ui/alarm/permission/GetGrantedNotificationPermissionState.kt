package nau.android.taskify.ui.alarm.permission

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus

object GetGrantedNotificationPermissionState {

    @OptIn(ExperimentalPermissionsApi::class)
    fun getGrantedPermissionState(permission: String) = object : PermissionState {
        override val permission: String
            get() = permission
        override val status: PermissionStatus
            get() = PermissionStatus.Granted

        override fun launchPermissionRequest() {

        }

    }
}