package nau.android.taskify.data.dataSource

import kotlinx.coroutines.flow.Flow
import nau.android.taskify.data.database.TaskDatabase
import nau.android.taskify.data.model.Category
import javax.inject.Inject

class CategoriesLocalDataSource @Inject constructor(
    taskDatabase: TaskDatabase
) : ICategoriesLocalDataSource {

    private val categoryDao = taskDatabase.categoryDao()
    override fun getCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories()
    }

    override suspend fun getCategoryById(id: Long): Category {
        return categoryDao.getCategoryById(id)
    }
}