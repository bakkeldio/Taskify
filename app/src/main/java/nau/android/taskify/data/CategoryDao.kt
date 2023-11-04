package nau.android.taskify.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import nau.android.taskify.data.model.Category


@Dao
interface CategoryDao {

    @Insert
    fun createCategory(newCategory: Category)

    @Update
    fun updateCategory(category: Category)

    @Delete
    fun deleteCategory(category: Category)
}