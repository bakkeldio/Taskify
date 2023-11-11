package nau.android.taskify.ui.alarm

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import nau.android.taskify.data.extensions.cancelAlarm
import nau.android.taskify.data.extensions.setExactAlarm
import nau.android.taskify.ui.model.Task
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmScheduler @Inject constructor(@ApplicationContext val context: Context) {

    fun scheduleTaskAlarm(taskId: Long, timeInMillis: Long) {

        val receiverIntent = Intent(context, TaskReceiver::class.java).apply {
            action = TaskReceiver.ACTION_ALARM
            putExtra(TaskReceiver.EXTRA_TASK, taskId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.toInt(),
            receiverIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )

        context.setExactAlarm(timeInMillis, pendingIntent)
    }

    fun cancelTaskAlarm(taskId: Long) {
        val intent = Intent(context, TaskReceiver::class.java)
        intent.action = TaskReceiver.ACTION_ALARM

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )

        context.cancelAlarm(pendingIntent)
    }

}