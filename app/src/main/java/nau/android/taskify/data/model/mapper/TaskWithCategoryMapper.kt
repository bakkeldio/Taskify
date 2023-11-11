package nau.android.taskify.data.model.mapper

import dagger.hilt.android.scopes.ActivityScoped
import nau.android.taskify.data.model.TaskWithCategory
import javax.inject.Inject
import javax.inject.Singleton
import nau.android.taskify.ui.model.TaskWithCategory as TaskWithCategoryUI

@Singleton
class TaskWithCategoryMapper @Inject constructor(
    private val taskMapper: TaskMapper,
    private val categoryMapper: CategoryMapper
) {

    fun toUI(taskWithCategory: TaskWithCategory): TaskWithCategoryUI {
        return TaskWithCategoryUI(taskMapper.toUI(taskWithCategory.task),
            taskWithCategory.category?.let {
                categoryMapper.toUI(it)
            })
    }
}