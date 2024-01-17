package nau.android.taskify.ui.login.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import nau.android.taskify.data.model.Result
import nau.android.taskify.data.repository.IAuthRepository
import nau.android.taskify.ui.login.viewModel.BaseLoginViewModel
import nau.android.taskify.ui.model.EmailSentState
import javax.inject.Inject


@HiltViewModel
class PasswordRecoveryViewModel @Inject constructor(
    private val authRepo: IAuthRepository
) : BaseLoginViewModel() {

    private val _emailSentState: MutableLiveData<EmailSentState> = MutableLiveData()
    val emailSentState: LiveData<EmailSentState> = _emailSentState

    fun sendPasswordRecoveryEmail(email: String) {
        viewModelScope.launch {
            when (val result = authRepo.sendPasswordResetEmail(email)) {
                is Result.Success -> _emailSentState.value = EmailSentState.EmailSent
                is Result.Error -> _emailSentState.value =
                    EmailSentState.Error(result.error.message ?: "")
            }
        }
    }

    fun resetEmailSentErrorState() {
        _emailSentState.value = null
    }
}