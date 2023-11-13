package nau.android.taskify.data.repository

import kotlinx.coroutines.flow.Flow
import nau.android.taskify.ui.model.Category

interface ICategoryRepository {

    fun getAllCategories(): Flow<List<Category>>

    suspend fun getCategoryById(id: Long): Category?

    suspend fun editCategory(category: Category)

    suspend fun createCategory(category: Category)

    suspend fun deleteCategory(category: Category)
}