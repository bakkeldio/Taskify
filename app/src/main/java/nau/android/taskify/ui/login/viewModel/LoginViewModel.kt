package nau.android.taskify.ui.login.viewModel

import dagger.hilt.android.lifecycle.HiltViewModel
import nau.android.taskify.data.repository.IAuthRepository
import nau.android.taskify.ui.login.viewModel.BaseLoginViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val authRepository: IAuthRepository) :
    BaseLoginViewModel() {

    fun signInWithWithGoogle(googleTokenId: String?) {
        googleTokenId ?: return
        launchCatching {
            authRepository.signInWithGoogle(googleTokenId)
        }
    }
}