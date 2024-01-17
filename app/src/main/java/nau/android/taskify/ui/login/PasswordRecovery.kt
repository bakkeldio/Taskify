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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import nau.android.taskify.R
import nau.android.taskify.ui.customElements.LoginEmail
import nau.android.taskify.ui.customElements.TaskifyArrowBack
import nau.android.taskify.ui.customElements.TaskifyLoginErrorMessage
import nau.android.taskify.ui.login.viewModel.PasswordRecoveryViewModel
import nau.android.taskify.ui.model.EmailSentState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordRecovery(
    navigateUp: () -> Unit,
    passwordRecoveryViewModel: PasswordRecoveryViewModel = hiltViewModel()
) {


    var email by remember {
        mutableStateOf("")
    }

    val failureMessage = passwordRecoveryViewModel.failureMessage.observeAsState()

    val keyboardController = LocalSoftwareKeyboardController.current

    val focusManager = LocalFocusManager.current

    val emailSentState = passwordRecoveryViewModel.emailSentState.observeAsState()

    var showEmailError by remember {
        mutableStateOf(false)
    }

    val snackBarHostState = remember {
        SnackbarHostState()
    }

    LaunchedEffect(key1 = failureMessage) {
        failureMessage.value?.let {
            snackBarHostState.showSnackbar(it)
            passwordRecoveryViewModel.resetErrorMessage()
        }
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

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            when (val result = emailSentState.value) {
                is EmailSentState.EmailSent -> {
                    ShowSnackBar(
                        message = stringResource(id = R.string.email_sent_description),
                        snackbarHostState = snackBarHostState
                    )
                }

                is EmailSentState.Error -> {
                    ShowSnackBar(message = result.message, snackbarHostState = snackBarHostState)
                    passwordRecoveryViewModel.resetEmailSentErrorState()
                }

                else -> Unit
            }
            LoginEmail(
                email = email,
                placeHolder = stringResource(id = R.string.write_email_to_send_password_reset_link),
                onEmailChange = {
                    email = it
                    showEmailError = false
                })

            AnimatedVisibility(visible = showEmailError) {
                TaskifyLoginErrorMessage(message = stringResource(id = R.string.email_content_error))
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(onClick = {
                if (passwordRecoveryViewModel.isEmailValid(email)) {
                    passwordRecoveryViewModel.sendPasswordRecoveryEmail(email)
                } else {
                    showEmailError = true
                }
                keyboardController?.hide()
                focusManager.clearFocus()
            }, modifier = Modifier.fillMaxWidth(0.8f), shape = RoundedCornerShape(10.dp)) {
                Text(
                    text = when (emailSentState.value) {
                        null -> stringResource(id = R.string.send)
                        EmailSentState.EmailSent -> stringResource(
                            id = R.string.email_sent
                        )

                        else -> stringResource(id = R.string.resend)
                    }
                )
            }
        }
    }
}

@Composable
private fun ShowSnackBar(message: String, snackbarHostState: SnackbarHostState) {
    LaunchedEffect(key1 = message) {
        snackbarHostState.showSnackbar(message)
    }
}