package nau.android.taskify.data.module

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.components.SingletonComponent
import nau.android.taskify.data.dataSource.ITasksLocalDataSource
import nau.android.taskify.data.database.TaskDatabase
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