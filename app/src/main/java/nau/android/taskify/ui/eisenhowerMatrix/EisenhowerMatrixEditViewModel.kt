package nau.android.taskify.ui.eisenhowerMatrix

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EisenhowerMatrixEditViewModel @Inject constructor(private val matrixConfigManager: EisenhowerMatrixConfigManager) :
    ViewModel() {

    private val _matrixConfigurations: MutableLiveData<Map<EisenhowerMatrixQuadrant, QuadrantConfig>> = MutableLiveData()
    val matrixConfigurations: LiveData<Map<EisenhowerMatrixQuadrant, QuadrantConfig>> = _matrixConfigurations
    fun getAllConfigurations() {
        _matrixConfigurations.value = matrixConfigManager.getAllConfigurations()
    }




}