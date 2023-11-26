package nau.android.taskify.ui.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import nau.android.taskify.R
import nau.android.taskify.ui.customElements.LoginEmail
import nau.android.taskify.ui.customElements.LoginPassword
import nau.android.taskify.ui.customElements.TaskifyArrowBack
import nau.android.taskify.ui.customElements.TaskifyLoginErrorMessage
import nau.android.taskify.ui.extensions.keyboardAsState
import nau.android.taskify.ui.extensions.noRippleClickable
import nau.android.taskify.ui.theme.TaskifyTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginWithEmailPassword(
    loginWithEmailAndPasswordViewModel: LoginWithEmailAndPasswordViewModel = hiltViewModel(),
    navigateToSignUpPage: () -> Unit,
    navigateToPasswordRecovery: () -> Unit,
    navigateUp: () -> Unit
) {

    var showEmailContentErrorMessage by remember {
        mutableStateOf(false)
    }

    var showPasswordContentErrorMessage by remember {
        mutableStateOf(false)
    }

    val snackBarHostState = remember {
        SnackbarHostState()
    }

    val keyBoardController = LocalSoftwareKeyboardController.current

    val focusManager = LocalFocusManager.current

    val message = loginWithEmailAndPasswordViewModel.failureMessage.observeAsState()

    LaunchedEffect(key1 = message.value) {
        message.value?.let { message ->
            snackBarHostState.showSnackbar(message)
            loginWithEmailAndPasswordViewModel.resetErrorMessage()
        }
    }

    val keyboardState = keyboardAsState()

    if (!keyboardState.value) {
        focusManager.clearFocus()
    }

    Scaffold(topBar = {
        TopAppBar(
            title = {},
            navigationIcon = {
                TaskifyArrowBack {
                    navigateUp()
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
        )
    }, snackbarHost = {
        SnackbarHost(snackBarHostState) {
            Snackbar(
                snackbarData = it,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        }
    }) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {

            SignInPage(
                showEmailContentErrorMessage,
                showPasswordContentErrorMessage,
                onSignIn = { email, password ->
                    keyBoardController?.hide()
                    focusManager.clearFocus()
                    val emailValid = loginWithEmailAndPasswordViewModel.isEmailValid(email)
                    val passwordValid = loginWithEmailAndPasswordViewModel.isPasswordValid(password)
                    if (emailValid && passwordValid) {
                        loginWithEmailAndPasswordViewModel.signIndWithEmailPassword(email, password)
                    } else {
                        if (!emailValid) {
                            showEmailContentErrorMessage = true
                        }
                        if (!passwordValid) {
                            showPasswordContentErrorMessage = true
                        }
                    }
                },
                onSignUp = navigateToSignUpPage,
                onEmailChange = {
                    showEmailContentErrorMessage = false
                },
                onPasswordChange = {
                    showPasswordContentErrorMessage = false
                }, navigateToPasswordRecovery = navigateToPasswordRecovery
            )
        }
    }
}

@Composable
fun SignInPage(
    showEmailContentError: Boolean,
    showPasswordContentError: Boolean,
    onSignIn: (email: String, password: String) -> Unit,
    onSignUp: () -> Unit,
    onEmailChange: () -> Unit,
    onPasswordChange: () -> Unit,
    navigateToPasswordRecovery: () -> Unit
) {

    var email by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }

    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {

        LoginEmail(
            email = email,
            placeHolder = stringResource(id = R.string.email),
            onEmailChange = {
                email = it
                onEmailChange()
            })

        AnimatedVisibility(visible = showEmailContentError) {
            TaskifyLoginErrorMessage(message = stringResource(id = R.string.email_content_error))
        }

        Spacer(modifier = Modifier.height(20.dp))

        LoginPassword(
            password = password,
            placeHolder = stringResource(id = R.string.password),
            onPasswordChange = {
                password = it
                onPasswordChange()
            })

        AnimatedVisibility(visible = showPasswordContentError) {
            TaskifyLoginErrorMessage(message = stringResource(id = R.string.password_content_error_message))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(id = R.string.forgot_password),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .noRippleClickable(navigateToPasswordRecovery)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                onSignIn(email, password)
            },
            modifier = Modifier.fillMaxWidth(0.8f),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                text = stringResource(id = R.string.sign_in).uppercase(),
                modifier = Modifier.padding(vertical = 5.dp)
            )
        }

        Spacer(modifier = Modifier.height(15.dp))

        TextButton(onClick = onSignUp, modifier = Modifier.fillMaxWidth(0.8f)) {
            Text(text = stringResource(id = R.string.sign_up).uppercase(), color = Color.Black)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignInPagePreview(
) {

    TaskifyTheme {
        SignInPage(
            showEmailContentError = false,
            showPasswordContentError = false,
            onSignIn = { email, password -> },
            onSignUp = {},
            onPasswordChange = {},
            onEmailChange = {}, navigateToPasswordRecovery = {})
    }
}