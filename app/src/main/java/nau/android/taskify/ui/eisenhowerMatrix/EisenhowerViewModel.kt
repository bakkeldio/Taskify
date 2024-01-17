package nau.android.taskify.ui.eisenhowerMatrix

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import nau.android.taskify.data.extensions.isItImportant
import nau.android.taskify.data.extensions.isItUrgent
import nau.android.taskify.data.repository.ITaskRepository
import nau.android.taskify.ui.alarm.AlarmScheduler
import nau.android.taskify.ui.enums.Priority
import nau.android.taskify.ui.enums.TaskRepeatInterval
import nau.android.taskify.ui.extensions.updateDate
import nau.android.taskify.ui.model.EisenhowerMatrixModel
import nau.android.taskify.ui.model.Task
import java.util.Calendar
import javax.inject.Inject


@HiltViewModel
class EisenhowerViewModel @Inject constructor(
    private val taskRepository: ITaskRepository,
    private val alarmScheduler: AlarmScheduler
) :
    ViewModel() {
    fun getTasks() = flow {
        taskRepository.getAllTasks().catch {
            emit(MatrixState.Error(it))
        }.collect { tasks ->

            val importantAndUrgentTasks = tasks.filter { task ->
                task.isItImportant() && task.isItUrgent()
            }

            val importantAndNotUrgentTasks = tasks.filter { task ->
                task.isItImportant() && !task.isItUrgent()
            }

            val notImportantUrgentTasks = tasks.filter { task ->
                !task.isItImportant() && task.isItUrgent()
            }

            val notImportantNotUrgentTasks = tasks.filter { task ->
                !task.isItImportant() && !task.isItUrgent()
            }
            emit(
                MatrixState.Success(
                    EisenhowerMatrixModel(
                        importantAndUrgentTasks,
                        importantAndNotUrgentTasks,
                        notImportantUrgentTasks,
                        notImportantNotUrgentTasks
                    )
                )
            )

        }
    }

    fun completeTask(task: Task) {
        viewModelScope.launch {
            delay(500)
            taskRepository.updateTask(
                task.copy(
                    completed = true,
                    completionDate = Calendar.getInstance(),
                    repeatInterval = TaskRepeatInterval.NONE
                )
            )
            alarmScheduler.cancelTaskAlarm(task.id)
            if (task.repeatInterval != TaskRepeatInterval.NONE) {
                if (task.dueDate != null && task.timeIncluded) {
                    do {
                        task.repeatInterval.updateDate(task.dueDate)
                    } while (Calendar.getInstance().after(task.dueDate))
                    val id = taskRepository.createTask(
                        task.copy(
                            id = 0,
                            creationDate = Calendar.getInstance(),
                            dueDate = task.dueDate
                        )
                    )
                    alarmScheduler.scheduleTaskAlarm(id, task.dueDate.timeInMillis)
                }
            }
        }
    }

    fun updateTask(task: Task, eisenhowerMatrixQuadrant: EisenhowerMatrixQuadrant) {

        val newTask = when (eisenhowerMatrixQuadrant) {
            EisenhowerMatrixQuadrant.IMPORTANT_URGENT -> {
                task.copy(dueDate = Calendar.getInstance(), priority = Priority.HIGH)
            }

            EisenhowerMatrixQuadrant.NOT_URGENT_IMPORTANT -> {
                val newDate = Calendar.getInstance()
                newDate[Calendar.DAY_OF_MONTH] += 2
                task.copy(dueDate = newDate, priority = Priority.HIGH)
            }

            EisenhowerMatrixQuadrant.URGENT_UNIMPORTANT -> {
                task.copy(dueDate = Calendar.getInstance(), priority = Priority.LOW)
            }

            EisenhowerMatrixQuadrant.NOT_URGENT_UNIMPORTANT -> {
                val newDate = Calendar.getInstance()
                newDate[Calendar.DAY_OF_MONTH] += 2
                task.copy(dueDate = newDate, priority = Priority.LOW)
            }
        }
        viewModelScope.launch {
            taskRepository.updateTask(newTask)
        }
    }

}