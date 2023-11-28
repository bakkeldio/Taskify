package nau.android.taskify.data.repository

import kotlinx.coroutines.flow.Flow
import nau.android.taskify.ui.model.Task
import nau.android.taskify.ui.model.TaskWithCategory
import java.util.Calendar

interface ITaskRepository {

    fun getAllTasksWithCategories(): Flow<List<TaskWithCategory>>

    fun getAllTasks(): Flow<List<Task>>

    fun getCategoryTasks(categoryId: Long): Flow<List<Task>>

    fun getTasksByDate(date: Calendar): Flow<List<Task>>

    fun getCompletedTasks(): Flow<List<TaskWithCategory>>

    fun getCompletedTasksOfTheCategory(categoryId: Long): Flow<List<Task>>

    suspend fun updateTask(task: Task)

    suspend fun deleteTask(task: Task)

    suspend fun deleteMultipleTasks(tasks: List<Task>)

    suspend fun createTask(task: Task): Long

    suspend fun getTaskById(id: Long): Task?

    fun getTaskByIdFlow(id: Long): Flow<Task>
}