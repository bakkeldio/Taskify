package nau.android.taskify.data.model

import androidx.room.Embedded


/**
 * [Entity] represents a wrapper of [Task] and [Category]
 * @property task Task
 * @property category Category
 */
data class TaskWithCategory(
    @Embedded val task: Task,
    @Embedded val category: Category? = null
)