package nau.android.taskify.data.repository

import kotlinx.coroutines.flow.Flow
import nau.android.taskify.ui.model.Task
import nau.android.taskify.ui.model.TaskWithCategory

interface ITaskRepository {

    fun getAllTasks(): Flow<List<TaskWithCategory>>

    suspend fun updateTask(task: Task)

    suspend fun deleteTask(task: Task)

    suspend fun createTask(task: Task): Long

    suspend fun getTaskById(id: Long): Task?

    fun getTaskByIdFlow(id: Long): Flow<Task>
}