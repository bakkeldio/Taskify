package nau.android.taskify.ui.category

import nau.android.taskify.ui.model.Category

sealed class CategoriesListState {
    class Success(val categories: List<Category>) : CategoriesListState()

    object Empty : CategoriesListState()

    class Error(val throwable: Throwable) : CategoriesListState()

    object Loading : CategoriesListState()
}