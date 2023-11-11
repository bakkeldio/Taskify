package nau.android.taskify.data.dataSource

import kotlinx.coroutines.flow.Flow
import nau.android.taskify.data.model.Task
import nau.android.taskify.data.model.TaskWithCategory

interface ITasksLocalDataSource {


    fun getAllTasks(): Flow<List<TaskWithCategory>>

    fun getTaskByIdWithFlow(id: Long): Flow<Task>

    suspend fun getTaskById(id: Long): Task?

    suspend fun getTaskByRowId(rowId: Long): Task

    suspend fun deleteTask(task: Task)

    suspend fun updateTask(task: Task)

    suspend fun createTask(task: Task): Long
}