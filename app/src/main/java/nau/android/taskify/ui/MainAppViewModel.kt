package nau.android.taskify.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import nau.android.taskify.data.repository.IAuthRepository
import javax.inject.Inject


@HiltViewModel
class MainAppViewModel @Inject constructor(private val authRepo: IAuthRepository) : ViewModel() {

    fun isUserSignedIn() = channelFlow {
        authRepo.checkIfSignedIn().catch {
            //emit(AuthState.Error(it))
        }.collectLatest { signedIn ->
            send(signedIn)
        }
    }

}