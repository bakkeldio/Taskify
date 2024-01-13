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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import nau.android.taskify.R
import nau.android.taskify.ui.customElements.LoginEmail
import nau.android.taskify.ui.customElements.LoginPassword
import nau.android.taskify.ui.customElements.TaskifyArrowBack
import nau.android.taskify.ui.customElements.TaskifyLoginErrorMessage


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpPage(navigateUp: () -> Unit, signUpViewModel: SignUpViewModel = hiltViewModel()) {

    var email by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    var confirmPassword by remember {
        mutableStateOf("")
    }

    var showEmailError by remember {
        mutableStateOf(false)
    }

    var showPasswordError by remember {
        mutableStateOf(false)
    }

    var showConfirmPasswordError by remember {
        mutableStateOf(false)
    }

    Scaffold(topBar = {
        TopAppBar(
            title = {},
            navigationIcon = {
                TaskifyArrowBack {
                    navigateUp()
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )
    }, containerColor = Color.White) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(15.dp))
            LoginEmail(email = email, placeHolder = stringResource(id = R.string.email), onEmailChange = {
                email = it
                showEmailError = false
            })
            AnimatedVisibility(visible = showEmailError) {
                TaskifyLoginErrorMessage(message = stringResource(id = R.string.email_content_error))
            }
            Spacer(modifier = Modifier.height(20.dp))
            LoginPassword(
                password = password,
                placeHolder = stringResource(id = R.string.password),
                onPasswordChange = {
                    password = it
                    showPasswordError = false
                })
            AnimatedVisibility(visible = showPasswordError) {
                TaskifyLoginErrorMessage(message = stringResource(id = R.string.password_content_error_message))
            }
            Spacer(modifier = Modifier.height(20.dp))
            LoginPassword(
                password = confirmPassword,
                placeHolder = stringResource(id = R.string.confirm_password),
                onPasswordChange = {
                    confirmPassword = it
                    showConfirmPasswordError = false
                })
            AnimatedVisibility(visible = showConfirmPasswordError) {
                TaskifyLoginErrorMessage(message = stringResource(id = R.string.confirm_password_error))
            }
            Spacer(modifier = Modifier.height(30.dp))
            Button(
                onClick = {
                    val emailValid = signUpViewModel.isEmailValid(email)
                    val passwordValid = signUpViewModel.isPasswordValid(password)
                    val confirmPasswordValid = password == confirmPassword

                    if (confirmPasswordValid && passwordValid && emailValid) {
                        signUpViewModel.createNewAccount(email, password)
                    } else {
                        if (!emailValid) {
                            showEmailError = true
                        }
                        if (!passwordValid) {
                            showPasswordError = true
                        }
                        if (!confirmPasswordValid) {
                            showConfirmPasswordError = true
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(0.8f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.create_account).uppercase(),
                    modifier = Modifier.padding(vertical = 5.dp)
                )
            }
        }
    }
}