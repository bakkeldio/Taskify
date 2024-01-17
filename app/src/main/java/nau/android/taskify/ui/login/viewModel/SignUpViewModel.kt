package nau.android.taskify.ui.login.viewModel

import dagger.hilt.android.lifecycle.HiltViewModel
import nau.android.taskify.data.repository.IAuthRepository
import nau.android.taskify.ui.login.viewModel.BaseLoginViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepo: IAuthRepository
) : BaseLoginViewModel() {

    fun createNewAccount(email: String, password: String) {
        launchCatching {
            authRepo.createAccount(email, password)
        }
    }

}