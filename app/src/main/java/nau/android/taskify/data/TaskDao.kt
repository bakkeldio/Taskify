package nau.android.taskify.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import nau.android.taskify.data.model.Task
import nau.android.taskify.data.model.TaskPriority
import nau.android.taskify.data.model.TaskWithCategory
import nau.android.taskify.data.model.TasksOrderBy


@Dao
interface TaskDao {

    @Insert
    fun insertTask(task: Task)

    @Update
    fun update(task: Task)

    @Delete
    fun deleteTask(task: Task)

    @Query("select * from task left join category on task.task_id = category_id")
    fun getAllTasks(): Flow<List<TaskWithCategory>>

}