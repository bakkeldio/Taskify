package nau.android.taskify.ui.alarm.permission

interface AlarmPermission {

    /**
     * Verifies if the permission [android.Manifest.permission.SCHEDULE_EXACT_ALARM] is granted.
     *
     * @return `true` if the permission is granted, `false` otherwise
     */
    fun hasExactAlarmPermission(): Boolean

    /**
     * Verifies if the [android.Manifest.permission.POST_NOTIFICATIONS] is granted.
     *
     * @return `true` if the permission is granted, `false` otherwise
     */
    fun hasNotificationPermission(): Boolean

    /**
     * Verifies if the [android.Manifest.permission.POST_NOTIFICATIONS] is supported and should be
     * requested.
     */
    @Suppress("FunctionMaxLength")
    fun shouldCheckNotificationPermission(): Boolean
}