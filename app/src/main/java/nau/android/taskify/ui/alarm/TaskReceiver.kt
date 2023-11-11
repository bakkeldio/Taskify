package nau.android.taskify.ui.alarm

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import nau.android.taskify.ui.alarm.notification.TaskifyNotification
import javax.inject.Inject

@AndroidEntryPoint
class TaskReceiver : BroadcastReceiver() {

    @Inject
    lateinit var taskifyNotification: TaskifyNotification

    @Inject
    lateinit var completeTask: CompleteTask

    @Inject
    lateinit var rescheduleFutureAlarms: RescheduleFutureAlarms


    val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob())


    override fun onReceive(p0: Context?, p1: Intent?) {
        getTaskId(p1)?.let {
            coroutineScope.launch {
                handleIntent(p1, it)
            }
        }
    }

    private suspend fun handleIntent(intent: Intent?, taskId: Long) {
        when (intent?.action) {
            ACTION_ALARM -> {
                taskifyNotification.show(taskId)
            }

            COMPLETE_TASK -> {
                completeTask(taskId)
            }

            Intent.ACTION_BOOT_COMPLETED,
            AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED -> {
                rescheduleFutureAlarms()
            }
        }
    }

    private fun getTaskId(intent: Intent?) = intent?.getLongExtra(EXTRA_TASK, 0)

    companion object {
        const val EXTRA_TASK = "Task"
        const val ACTION_ALARM = "nau.android.taskify.SET_ALARM"
        const val COMPLETE_TASK = "nau.android.taskify.COMPLETE_TASK"
    }
}