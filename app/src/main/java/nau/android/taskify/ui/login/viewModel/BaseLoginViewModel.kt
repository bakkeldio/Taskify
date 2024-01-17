package nau.android.taskify.ui.login.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

open class BaseLoginViewModel : ViewModel() {

    private val _failureMessage: MutableLiveData<String> = MutableLiveData()
    val failureMessage: LiveData<String> = _failureMessage
    fun launchCatching(block: suspend CoroutineScope.() -> Unit) =
        viewModelScope.launch(
            CoroutineExceptionHandler { _, throwable ->
                _failureMessage.value = throwable.message
            }, block = block
        )

    fun isEmailValid(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})")
        return emailRegex.matches(email)
    }

    fun isPasswordValid(password: String): Boolean {
        val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")
        return passwordRegex.matches(password)
    }

    fun resetErrorMessage(){
        _failureMessage.value = null
    }

    companion object {
        const val ERROR_TAG = "Login flow error"
    }
}