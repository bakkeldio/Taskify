package nau.android.taskify.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * [Entity] to represent a category
 * @property id category id
 * @property name category name
 * @property iconReference the reference to the file where icon is stored
 */
@Entity
data class Category(
    @PrimaryKey(true)
    @ColumnInfo("category_id")
    val id: Long = 0,
    val name: String,
    @ColumnInfo("category_icon_reference")
    val iconReference: String? = null
)