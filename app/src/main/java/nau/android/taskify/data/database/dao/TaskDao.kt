package nau.android.taskify.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import nau.android.taskify.data.model.Task
import nau.android.taskify.data.model.TaskWithCategory


@Dao
interface TaskDao {

    @Insert
    suspend fun insertTask(task: Task): Long

    @Query("select * from task where rowId=:id")
    suspend fun getTaskWithRowId(id: Long): Task

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("select * from task left join category on task.task_id = category_id")
    fun getAllTasks(): Flow<List<TaskWithCategory>>

    @Query("select * from task where task_id=:id")
    fun getTaskByIdWithFlow(id: Long): Flow<Task>

    @Query("select * from task where task_id =:id")
    suspend fun getTaskById(id: Long): Task


}