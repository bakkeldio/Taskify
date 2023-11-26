package nau.android.taskify.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import nau.android.taskify.data.repository.IAuthRepository
import javax.inject.Inject


@HiltViewModel
class TaskifyProfileViewModel @Inject constructor(
    private val authRepository: IAuthRepository
) : ViewModel() {


    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }
}