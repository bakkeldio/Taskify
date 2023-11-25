package nau.android.taskify.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import nau.android.taskify.data.repository.ITaskRepository
import nau.android.taskify.ui.model.Task
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(private val taskRepo: ITaskRepository) : ViewModel() {

    private val _calendarTasksMutableStateFlow: MutableStateFlow<CalendarTasksListState> =
        MutableStateFlow(CalendarTasksListState.Empty)
    val calendarTasksStateFlow: StateFlow<CalendarTasksListState> = _calendarTasksMutableStateFlow

    fun getTasksByDate(date: Calendar) {
        viewModelScope.launch {
            taskRepo.getTasksByDate(date).catch {
                _calendarTasksMutableStateFlow.value = CalendarTasksListState.Error(it)
            }.collect {
                if (it.isEmpty()) {
                    _calendarTasksMutableStateFlow.value = CalendarTasksListState.Empty
                } else {
                    _calendarTasksMutableStateFlow.value = CalendarTasksListState.Success(it)
                }
            }
        }
    }
}