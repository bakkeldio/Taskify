package nau.android.taskify.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import nau.android.taskify.data.database.dao.CategoryDao
import nau.android.taskify.data.database.dao.TaskDao
import nau.android.taskify.data.database.typeConverters.Converters
import nau.android.taskify.data.model.Category
import nau.android.taskify.data.model.Task

@Database(entities = [Task::class, Category::class], version = 1)
@TypeConverters(Converters::class)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    abstract fun categoryDao(): CategoryDao

}