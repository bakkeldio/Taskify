package nau.android.taskify.ui.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import nau.android.taskify.data.repository.ICategoryRepository
import nau.android.taskify.ui.model.Category
import javax.inject.Inject


@HiltViewModel
class CategoryEditViewModel @Inject constructor(
    private val categoryRepository: ICategoryRepository
) : ViewModel() {

    private val categoryMutableLiveData: MutableLiveData<CategoryState> = MutableLiveData()
    val categoryLiveData: LiveData<CategoryState> = categoryMutableLiveData


    fun editCategory(category: Category) {
        viewModelScope.launch {
            categoryRepository.editCategory(category)
        }
    }

    fun getCategoryById(id: Long) {
        viewModelScope.launch {
            val result = categoryRepository.getCategoryById(id)
            if (result != null) {
                categoryMutableLiveData.value = CategoryState.Success(result)
            } else {
                categoryMutableLiveData.value = CategoryState.Empty
            }
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            categoryRepository.deleteCategory(category)
        }
    }
}