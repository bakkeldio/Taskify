package nau.android.taskify.ui.auth

sealed class AuthState {

    class Error(val throwable: Throwable) : AuthState()
    class Success(val signedIn: Boolean) : AuthState()
}