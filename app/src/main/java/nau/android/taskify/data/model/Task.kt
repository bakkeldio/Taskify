package nau.android.taskify.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import nau.android.taskify.data.model.ReminderType
import java.util.Calendar


@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["category_id"],
            childColumns = ["task_category_id"],
            onDelete = ForeignKey.CASCADE
        )
    ], indices = [androidx.room.Index(value = ["task_category_id"])]
)
data class Task(
    @PrimaryKey(true)
    @ColumnInfo("task_id")
    val id: Long = 0,
    @ColumnInfo("task_name")
    val name: String,
    @ColumnInfo("task_description")
    val description: String? = null,
    @ColumnInfo("task_due_date")
    val dueDate: Calendar? = null,
    @ColumnInfo("task_priority")
    val priority: TaskPriority = TaskPriority.NONE,
    @ColumnInfo("task_is_completed")
    val completed: Boolean = false,
    @ColumnInfo("task_is_repeating")
    val isRepeating: Boolean = false,
    @ColumnInfo("task_category_id")
    val categoryId: Long? = null,
    @ColumnInfo("time_included")
    val timeIncluded: Boolean = false,
    @ColumnInfo("task_reminders")
    val reminders: List<ReminderType>? = null,
    @ColumnInfo("task_alarm_interval")
    val alarmInterval: AlarmInterval = AlarmInterval.NONE,
    @ColumnInfo("task_creation_date")
    val creationDate: Calendar? = null,
    @ColumnInfo("task_completion_date")
    val completedDate: Calendar? = null
)