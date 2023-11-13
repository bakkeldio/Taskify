package nau.android.taskify.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import nau.android.taskify.data.dataSource.ICategoriesLocalDataSource
import nau.android.taskify.data.model.mapper.CategoryMapper
import nau.android.taskify.ui.model.Category
import javax.inject.Inject

class CategoryRepository @Inject constructor(
    private val localDataSource: ICategoriesLocalDataSource,
    private val categoryMapper: CategoryMapper
) :
    ICategoryRepository {
    override fun getAllCategories(): Flow<List<Category>> {
        return localDataSource.getCategories().map { items ->
            items.map { category ->
                categoryMapper.toUI(category)
            }
        }
    }

    override suspend fun getCategoryById(id: Long): Category? {
        return localDataSource.getCategoryById(id)?.let {
            categoryMapper.toUI(it)
        }
    }

    override suspend fun editCategory(category: Category) {
        localDataSource.editCategory(categoryMapper.toRepo(category))
    }

    override suspend fun createCategory(category: Category) {
        localDataSource.createCategory(categoryMapper.toRepo(category))
    }

    override suspend fun deleteCategory(category: Category) {
        localDataSource.deleteCategory(categoryMapper.toRepo(category))
    }

}