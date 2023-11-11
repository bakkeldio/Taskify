package nau.android.taskify.ui.alarm.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import nau.android.taskify.R
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
import nau.android.taskify.data.extensions.getNotificationManager
import nau.android.taskify.data.repository.ITaskRepository
import nau.android.taskify.ui.alarm.TaskReceiver
import nau.android.taskify.ui.model.Task
import javax.inject.Inject

class TaskifyNotificationImpl @Inject constructor(
    private val taskRepo: ITaskRepository,
    @ApplicationContext private val context: Context
) :
    TaskifyNotification {

    init {
        createNotificationChannel()
    }

    override suspend fun show(taskId: Long) {

        val task = taskRepo.getTaskById(taskId) ?: return

        context.getNotificationManager()?.notify(task.id.toInt(), buildNotification(task).build())


    }

    override fun hide(notificationId: Long) {
        context.getNotificationManager()?.cancel(notificationId.toInt())
    }

    private fun buildNotification(task: Task) =
        NotificationCompat.Builder(context, CHANNEL_ID).apply {
            setSmallIcon(R.drawable.ic_alarm)
            setContentTitle(context.getString(R.string.app_name))
            setContentText(task.name)
            //used to support priorities on devices with API level below 26
            priority = NotificationCompat.PRIORITY_HIGH
            setContentIntent(buildContentPendingIntent(task))
            setAutoCancel(true)
            addAction(actionComplete(task.id))
        }


    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.channel_name)
            val descriptionText = context.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system.
            context.getNotificationManager()?.createNotificationChannel(channel)
        }
    }

    private fun buildContentPendingIntent(task: Task): PendingIntent {
        val openTaskIntent = Intent(Intent.ACTION_VIEW, task.id.toString().toUri())

        return TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(openTaskIntent)
            getPendingIntent(
                REQUEST_CODE_OPEN_TASK,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }

    private fun actionComplete(taskId: Long): NotificationCompat.Action {
        val actionTitle = context.getString(R.string.complete_action)
        val intent = Intent(context, TaskReceiver::class.java).apply {
            action = TaskReceiver.COMPLETE_TASK
            putExtra(TaskReceiver.EXTRA_TASK, taskId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_COMPLETE_TASK,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )

        return NotificationCompat.Action(0, actionTitle, pendingIntent)

    }

    companion object {
        private const val CHANNEL_ID = "task_notification_channel"
        private const val REQUEST_CODE_OPEN_TASK = 1
        private const val REQUEST_CODE_COMPLETE_TASK = 2
        private const val ACTION_NO_ICON = 0
    }

}