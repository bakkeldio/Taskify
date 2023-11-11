package nau.android.taskify.ui.tasksList

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import nau.android.taskify.data.repository.ITaskRepository
import nau.android.taskify.ui.alarm.AlarmScheduler
import nau.android.taskify.ui.alarm.permission.AlarmPermission
import nau.android.taskify.ui.enums.DateEnum
import nau.android.taskify.ui.model.Task
import nau.android.taskify.ui.model.TaskWithCategory
import java.util.Calendar
import javax.inject.Inject


@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val taskRepository: ITaskRepository,
    private val alarmScheduler: AlarmScheduler,
    private val permission: AlarmPermission
) :
    ViewModel() {


    fun createTask(task: Task) {

        viewModelScope.launch {
            val id = taskRepository.createTask(task)
            Log.d("Created task id: ", id.toString())
            if (task.dueDate != null && task.timeIncluded) {
                alarmScheduler.scheduleTaskAlarm(id, task.dueDate.timeInMillis)
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
        }
    }

    fun getAllTasks(groupingType: GroupingType, sortingType: SortingType = SortingType.Date) =
        flow {
            taskRepository.getAllTasks().map { items ->
                val groupedItems = when (groupingType) {
                    GroupingType.None -> {
                        mapOf(Pair(HeaderType.NoHeader, items))
                    }

                    GroupingType.Category -> {
                        items.groupBy {
                            it.category
                        }.mapKeys {
                            HeaderType.Category(it.key?.name ?: "No category")
                        }
                    }

                    GroupingType.Priority -> {
                        items.groupBy {
                            it.task.priority
                        }.mapKeys {
                            HeaderType.Priority(it.key.name, it.key.id)
                        }
                    }

                    GroupingType.Date -> {
                        items.groupBy { taskWithCategory ->
                            taskWithCategory.task.dueDate?.let {
                                convertCalendarToDateEnum(it)
                            } ?: DateEnum.NO_DATE
                        }.mapKeys {
                            HeaderType.Date(it.key)
                        }
                    }
                }
                sortList(sortingType, groupedItems)
            }.catch {
                emit(TasksListState.Error(it))
            }.collect { items ->
                if (items.isEmpty()) {
                    emit(TasksListState.Empty)
                } else {
                    emit(TasksListState.Success(items))
                }
            }
        }

    private fun sortList(
        sortingType: SortingType,
        tasks: Map<out HeaderType, List<TaskWithCategory>>
    ) =
        tasks.mapValues { map ->
            when (sortingType) {
                SortingType.Date -> map.value.sortedBy {
                    it.task.dueDate
                }

                SortingType.Priority -> map.value.sortedBy {
                    it.task.priority.id
                }

                SortingType.Title -> map.value.sortedBy {
                    it.task.name
                }

            }
        }

    private fun convertCalendarToDateEnum(taskDueDate: Calendar): DateEnum {
        val currentDate = Calendar.getInstance()
        return if (currentDate[Calendar.YEAR] == taskDueDate[Calendar.YEAR] &&
            currentDate[Calendar.MONTH] == taskDueDate[Calendar.MONTH] &&
            currentDate[Calendar.DAY_OF_MONTH] == taskDueDate[Calendar.DAY_OF_MONTH]
        ) {
            DateEnum.TODAY
        } else if (taskDueDate.after(currentDate)) {
            val tomorrow = currentDate.clone() as Calendar
            tomorrow[Calendar.DAY_OF_MONTH] += 1
            val nextSevenDays = currentDate.clone() as Calendar
            nextSevenDays[Calendar.DAY_OF_MONTH] += 7

            if (taskDueDate[Calendar.YEAR] == tomorrow[Calendar.YEAR] &&
                taskDueDate[Calendar.MONTH] == tomorrow[Calendar.MONTH] &&
                taskDueDate[Calendar.DAY_OF_MONTH] == tomorrow[Calendar.DAY_OF_MONTH]
            ) {
                DateEnum.TOMORROW
            } else if (taskDueDate.after(nextSevenDays)) {
                DateEnum.LATER
            } else {
                DateEnum.NEXT_SEVEN_DAYS
            }
        } else {
            DateEnum.OVERDUE
        }
    }


}