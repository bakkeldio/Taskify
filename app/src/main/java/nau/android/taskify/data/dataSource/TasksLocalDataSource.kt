package nau.android.taskify.data.dataSource

import kotlinx.coroutines.flow.Flow
import nau.android.taskify.data.database.TaskDatabase
import nau.android.taskify.data.model.Task
import nau.android.taskify.data.model.TaskWithCategory
import java.util.Calendar
import javax.inject.Inject


class TasksLocalDataSource @Inject constructor(private val tasksDatabase: TaskDatabase) :
    ITasksLocalDataSource {

    private val taskDao = tasksDatabase.taskDao()

    override fun getAllTasksWithCategories(): Flow<List<TaskWithCategory>> {
        return taskDao.getAllTasksWithCategories()
    }

    override fun getTasksByDate(startDateInMillis: Long, endDateInMillis: Long): Flow<List<Task>> {
        return taskDao.getTasksByDate(startDateInMillis, endDateInMillis)
    }

    override fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks()
    }

    override fun getCategoryTasks(id: Long): Flow<List<Task>> {
        return taskDao.getCategoryTasks(id)
    }

    override fun getCompletedTasks(): Flow<List<TaskWithCategory>> {

        return taskDao.getCompletedTasks()
    }

    override fun getCompletedTasksOfCategory(categoryId: Long): Flow<List<Task>> {
        return taskDao.getCategoryCompletedTasks(categoryId)
    }

    override fun getTaskByIdWithFlow(id: Long): Flow<Task> {
        return taskDao.getTaskByIdWithFlow(id)
    }

    override suspend fun getTaskById(id: Long): Task {
        return taskDao.getTaskById(id)
    }

    override suspend fun getTaskByRowId(rowId: Long): Task {
        return taskDao.getTaskWithRowId(rowId)
    }

    override suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }

    override suspend fun deleteMultipleTasks(tasks: List<Task>) {
        taskDao.deleteTasks(tasks)
    }

    override suspend fun updateTask(task: Task) {
        taskDao.update(task)
    }

    override suspend fun createTask(task: Task): Long {
        return taskDao.insertTask(task)
    }
}