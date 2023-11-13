package nau.android.taskify.ui.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import nau.android.taskify.data.repository.ICategoryRepository
import nau.android.taskify.ui.model.Category
import javax.inject.Inject


@HiltViewModel
class CategoryCreateViewModel @Inject constructor(private val categoryRepository: ICategoryRepository) :
    ViewModel() {


    fun createCategory(newCategory: Category) {
        viewModelScope.launch {
            categoryRepository.createCategory(newCategory)
        }
    }
}