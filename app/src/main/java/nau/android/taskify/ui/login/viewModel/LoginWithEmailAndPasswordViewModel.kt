package nau.android.taskify.ui.login.viewModel

import dagger.hilt.android.lifecycle.HiltViewModel
import nau.android.taskify.data.repository.IAuthRepository
import javax.inject.Inject


@HiltViewModel
class LoginWithEmailAndPasswordViewModel @Inject constructor(
    private val authRepository: IAuthRepository
) : BaseLoginViewModel() {

    fun signIndWithEmailPassword(email: String, password: String) {
        launchCatching {
            authRepository.signIn(email, password)
        }

    }
}