package nau.android.taskify.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import nau.android.taskify.data.model.Category


@Dao
interface CategoryDao {

    @Insert
    suspend fun createCategory(newCategory: Category)

    @Update
    suspend fun updateCategory(category: Category)

    @Query("SELECT * FROM category where category_id = :id")
    suspend fun getCategoryById(id: Long): Category

    @Delete
    fun deleteCategory(category: Category)

    @Query("select * from Category")
    fun getAllCategories(): Flow<List<Category>>
}