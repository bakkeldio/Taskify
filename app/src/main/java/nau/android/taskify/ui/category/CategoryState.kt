package nau.android.taskify.ui.category

import nau.android.taskify.ui.model.Category

sealed class CategoryState {

    object Empty : CategoryState()

    class Success(val category: Category) : CategoryState()
}