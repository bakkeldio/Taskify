package nau.android.taskify.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMap
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import nau.android.taskify.data.dataSource.ITasksLocalDataSource
import nau.android.taskify.data.model.mapper.TaskMapper
import nau.android.taskify.data.model.mapper.TaskWithCategoryMapper
import nau.android.taskify.ui.model.Task
import nau.android.taskify.ui.model.TaskWithCategory
import java.util.Calendar
import javax.inject.Inject
import nau.android.taskify.ui.model.TaskWithCategory as TaskWithCategoryUI

class TaskRepository @Inject constructor(
    private val localDataSource: ITasksLocalDataSource,
    private val taskWithCategoryMapper: TaskWithCategoryMapper,
    private val taskMapper: TaskMapper
) : ITaskRepository {
    override fun getAllTasksWithCategories(): Flow<List<TaskWithCategoryUI>> {
        return localDataSource.getAllTasksWithCategories().map { taskWithCategory ->
            taskWithCategory.map {
                taskWithCategoryMapper.toUI(it)
            }
        }
    }

    override fun getAllTasks(): Flow<List<Task>> {
        return localDataSource.getAllTasks().map {
            it.map { task ->
                taskMapper.toUI(task)
            }
        }
    }

    override fun getCategoryTasks(categoryId: Long): Flow<List<Task>> {
        return localDataSource.getCategoryTasks(categoryId).map {
            it.map { task ->
                taskMapper.toUI(task)
            }
        }
    }

    override fun getTasksByDate(date: Calendar): Flow<List<Task>> {
        val clone = date.clone() as Calendar
        val startDateInMillis = clone.apply { set(Calendar.HOUR_OF_DAY, 0) }.timeInMillis
        val endDateInMillis = clone.apply { set(Calendar.HOUR_OF_DAY, 24) }.timeInMillis
        return localDataSource.getTasksByDate(startDateInMillis, endDateInMillis).map {
            it.map { task ->
                taskMapper.toUI(task)
            }
        }
    }

    override fun getCompletedTasks(): Flow<List<TaskWithCategory>> {
        return localDataSource.getCompletedTasks().map { tasks ->
            tasks.map {
                taskWithCategoryMapper.toUI(it)
            }
        }
    }

    override fun getCompletedTasksOfTheCategory(categoryId: Long): Flow<List<Task>> {
        return localDataSource.getCompletedTasksOfCategory(categoryId).map { tasks ->
            tasks.map {
                taskMapper.toUI(it)
            }
        }
    }

    override suspend fun updateTask(task: Task) {
        localDataSource.updateTask(taskMapper.toRepo(task))
    }

    override suspend fun deleteTask(task: Task) {
        localDataSource.deleteTask(taskMapper.toRepo(task))
    }

    override suspend fun deleteMultipleTasks(tasks: List<Task>) {
        localDataSource.deleteMultipleTasks(tasks.map {
            taskMapper.toRepo(it)
        })
    }

    override suspend fun createTask(task: Task): Long {
        return localDataSource.createTask(taskMapper.toRepo(task))
    }

    override suspend fun getTaskById(id: Long): Task? {
        return localDataSource.getTaskById(id)?.let {
            taskMapper.toUI(it)
        }
    }


    override fun getTaskByIdFlow(id: Long): Flow<Task> {
        return localDataSource.getTaskByIdWithFlow(id).map {
            taskMapper.toUI(it)
        }
    }

}