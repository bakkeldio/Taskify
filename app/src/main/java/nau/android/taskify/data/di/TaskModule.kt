package nau.android.taskify.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import nau.android.taskify.data.dataSource.ITasksLocalDataSource
import nau.android.taskify.data.dataSource.TasksLocalDataSource
import nau.android.taskify.data.repository.ITaskRepository
import nau.android.taskify.data.repository.TaskRepository
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class TaskModule {
    @Singleton
    @Binds
    abstract fun bindLocalDataSource(tasksLocalDataSource: TasksLocalDataSource): ITasksLocalDataSource

    @Singleton
    @Binds
    abstract fun bindTaskRepo(tasksRepository: TaskRepository): ITaskRepository
}