package nau.android.taskify.ui.category

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import nau.android.taskify.ui.model.Category
import nau.android.taskify.data.repository.ICategoryRepository
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(private val categoryRepo: ICategoryRepository) :
    ViewModel() {

    private val _categoryLiveData: MutableLiveData<Category> = MutableLiveData()
    val categoryLiveData: LiveData<Category> = _categoryLiveData

    fun getCategories() = flow {

        categoryRepo.getAllCategories().catch {
            emit(CategoriesListState.Error(it))
        }.collect { items ->
            if (items.isEmpty()) {
                emit(CategoriesListState.Empty)
            } else {
                emit(CategoriesListState.Success(items))
            }
        }

    }

    fun createCategory(name: String, color: Int) {

    }

    fun getCategoryById(id: Long?) {
        if (id == null) {
            return
        }
        viewModelScope.launch {
            _categoryLiveData.value = categoryRepo.getCategoryById(id)
        }
    }
}