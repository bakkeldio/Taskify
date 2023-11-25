package nau.android.taskify.data.dataSource

import kotlinx.coroutines.flow.Flow
import nau.android.taskify.data.model.Task
import nau.android.taskify.data.model.TaskWithCategory
import java.util.Calendar

interface ITasksLocalDataSource {

    fun getAllTasksWithCategories(): Flow<List<TaskWithCategory>>

    fun getTasksByDate(startDateInMillis: Long, endDateInMillis: Long): Flow<List<Task>>

    fun getAllTasks(): Flow<List<Task>>

    fun getCategoryTasks(id: Long): Flow<List<Task>>

    fun getTaskByIdWithFlow(id: Long): Flow<Task>

    suspend fun getTaskById(id: Long): Task?

    suspend fun getTaskByRowId(rowId: Long): Task

    suspend fun deleteTask(task: Task)

    suspend fun deleteMultipleTasks(tasks: List<Task>)

    suspend fun updateTask(task: Task)

    suspend fun createTask(task: Task): Long
}