package nau.android.taskify.data

import androidx.room.Database
import androidx.room.RoomDatabase
import nau.android.taskify.data.model.Category
import nau.android.taskify.data.model.Task

@Database(entities = [Task::class, Category::class], version = 1)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    abstract fun categoryDao(): CategoryDao

}