package nau.android.taskify.ui.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import nau.android.taskify.ui.alarm.notification.TaskifyNotification
import nau.android.taskify.ui.alarm.notification.TaskifyNotificationImpl
import nau.android.taskify.ui.alarm.permission.AlarmPermission
import nau.android.taskify.ui.alarm.permission.AlarmPermissionImpl
import nau.android.taskify.ui.alarm.permission.PermissionChecker
import nau.android.taskify.ui.alarm.permission.PermissionCheckerImpl
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class AlarmModule {


    @Binds
    @Singleton
    abstract fun bindNotification(taskifyNotificationImpl: TaskifyNotificationImpl): TaskifyNotification


}