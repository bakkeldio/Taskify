package nau.android.taskify.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import nau.android.taskify.data.dataSource.ITasksLocalDataSource
import nau.android.taskify.data.model.mapper.TaskMapper
import nau.android.taskify.data.model.mapper.TaskWithCategoryMapper
import nau.android.taskify.ui.alarm.AlarmScheduler
import nau.android.taskify.ui.model.Task
import javax.inject.Inject
import nau.android.taskify.ui.model.TaskWithCategory as TaskWithCategoryUI

class TaskRepository @Inject constructor(
    private val localDataSource: ITasksLocalDataSource,
    private val taskWithCategoryMapper: TaskWithCategoryMapper,
    private val taskMapper: TaskMapper
) : ITaskRepository {
    override fun getAllTasks(): Flow<List<TaskWithCategoryUI>> {
        return localDataSource.getAllTasks().map { taskWithCategory ->
            taskWithCategory.map {
                taskWithCategoryMapper.toUI(it)
            }
        }
    }

    override suspend fun updateTask(task: Task) {
        localDataSource.updateTask(taskMapper.toRepo(task))
    }

    override suspend fun deleteTask(task: Task) {
        localDataSource.deleteTask(taskMapper.toRepo(task))
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