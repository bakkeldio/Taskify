package nau.android.taskify.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import nau.android.taskify.data.model.Task
import nau.android.taskify.data.model.TaskPriority
import nau.android.taskify.data.model.TaskWithCategory
import java.util.Calendar


@Dao
interface TaskDao {

    @Insert
    suspend fun insertTask(task: Task): Long

    @Query("select * from task where rowId=:id")
    suspend fun getTaskWithRowId(id: Long): Task

    @Update
    suspend fun update(task: Task)

    @Update
    suspend fun updateTasks(tasks: List<Task>)

    @Delete
    suspend fun deleteTask(task: Task)

    @Delete
    suspend fun deleteTasks(tasks: List<Task>)

    @Query(
        "select * from task left join category on task.task_category_id = category_id where not task_is_completed " +
                "and (:searchQuery is NULL OR task_name LIKE '%' || :searchQuery || '%')"
    )
    fun getAllTasksWithCategories(searchQuery: String?): Flow<List<TaskWithCategory>>

    @Query("select * from task where not task_is_completed")
    fun getAllTasks(): Flow<List<Task>>

    @Query("select * from task where task_category_id=:categoryId and task_is_completed")
    fun getCategoryCompletedTasks(categoryId: Long): Flow<List<Task>>

    @Query("select * from task left join category on task.task_category_id = category_id where task_is_completed")
    fun getCompletedTasks(): Flow<List<TaskWithCategory>>

    @Query("select * from task where task_category_id=:categoryId and not task_is_completed")
    fun getCategoryTasks(categoryId: Long): Flow<List<Task>>

    @Query("select * from task where not task_is_completed and (task_due_date >= :startDateInMillis AND task_due_date < :endDateInMillis)")
    fun getTasksByDate(startDateInMillis: Long, endDateInMillis: Long): Flow<List<Task>>

    @Query("select * from task where task_id=:id")
    fun getTaskByIdWithFlow(id: Long): Flow<Task>

    @Query("select * from task where task_id =:id")
    suspend fun getTaskById(id: Long): Task


}