package nau.android.taskify.ui.login

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import nau.android.taskify.R
import nau.android.taskify.googleAuth.GoogleAuthClient
import nau.android.taskify.ui.login.viewModel.LoginViewModel
import nau.android.taskify.ui.theme.TaskifyTheme


@Composable
fun LoginPage(
    googleAuthUiClient: GoogleAuthClient,
    navigateToLoginWithEmailPassword: () -> Unit,
    loginViewModel: LoginViewModel = hiltViewModel()
) {

    val coroutineScope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val googleTokedId = result.data?.let { googleAuthUiClient.signInWithIntent(it) }
                loginViewModel.signInWithWithGoogle(googleTokedId)
            }
        }
    )
    Box(
        modifier = Modifier.fillMaxSize().background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_task_logo),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(15.dp))
                Text(
                    text = stringResource(id = R.string.taskify_title),
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
            Spacer(modifier = Modifier.height(25.dp))
            Icon(
                painter = painterResource(id = R.drawable.ic_progress), contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(30.dp))

            OutlinedButton(
                onClick = {
                    coroutineScope.launch {
                        launcher.launch(
                            IntentSenderRequest.Builder(
                                googleAuthUiClient.getIntentSender() ?: return@launch
                            ).build()
                        )
                    }
                },
                colors = ButtonDefaults.outlinedButtonColors(),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth(0.65f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = stringResource(
                            id = R.string.google_icon
                        ),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(15.dp))
                    Text(
                        text = stringResource(id = R.string.sign_in_with_google),
                        color = Color.Black,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            OutlinedButton(
                onClick = {
                    navigateToLoginWithEmailPassword()
                },
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                colors = ButtonDefaults.outlinedButtonColors(),
                modifier = Modifier.fillMaxWidth(0.65f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_outline_email),
                        contentDescription = stringResource(
                            id = R.string.email_icon
                        )
                    )
                    Spacer(modifier = Modifier.width(15.dp))
                    Text(
                        text = stringResource(id = R.string.sign_in_with_email_password),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, device = Devices.PIXEL_4_XL)
@Composable
fun LoginPagePreview() {
    TaskifyTheme {
        //LoginPage({})
    }
}