package nau.android.taskify.data.dataSource

import kotlinx.coroutines.flow.Flow
import nau.android.taskify.data.model.Category

interface ICategoriesLocalDataSource {

    fun getCategories(): Flow<List<Category>>

    suspend fun getCategoryById(id: Long): Category?

    suspend fun editCategory(category: Category)

    suspend fun createCategory(category: Category)


    suspend fun deleteCategory(category: Category)
}