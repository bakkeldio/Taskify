package nau.android.taskify.data.model.mapper

import android.graphics.Color
import nau.android.taskify.data.extensions.toStringColor
import nau.android.taskify.data.model.Category
import javax.inject.Inject
import javax.inject.Singleton
import nau.android.taskify.ui.model.Category as CategoryUI

@Singleton
class CategoryMapper @Inject constructor() {

    fun toUI(category: Category): CategoryUI {
        return CategoryUI(category.id, category.name, Color.parseColor(category.color))
    }

    fun toRepo(category: CategoryUI): Category {
        return Category(category.id, category.name, category.color.toStringColor())
    }


}