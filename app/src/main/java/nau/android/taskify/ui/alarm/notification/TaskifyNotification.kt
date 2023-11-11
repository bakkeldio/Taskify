package nau.android.taskify.ui.alarm.notification

interface TaskifyNotification {


    suspend fun show(taskId: Long)

    fun hide(notificationId: Long)
}