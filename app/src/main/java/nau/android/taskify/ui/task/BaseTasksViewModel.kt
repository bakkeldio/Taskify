package nau.android.taskify.ui.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nau.android.taskify.data.repository.ITaskRepository
import nau.android.taskify.ui.alarm.AlarmScheduler
import nau.android.taskify.ui.enums.TaskRepeatInterval
import nau.android.taskify.ui.extensions.updateDate
import nau.android.taskify.ui.model.Task
import java.util.Calendar
import javax.inject.Inject


