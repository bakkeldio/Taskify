package nau.android.taskify.ui

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import dagger.hilt.android.qualifiers.ApplicationContext
import nau.android.taskify.R
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GoogleAuthClient @Inject constructor(
    @ApplicationContext private val context: Context,
    private val oneTapClient: SignInClient
) {
    suspend fun getIntentSender(): IntentSender? {
        val result = try {
            oneTapClient.beginSignIn(
                buildSignInRequest()
            ).await()
        } catch(e: Exception) {
            e.printStackTrace()
            null
        }
        return result?.pendingIntent?.intentSender
    }

    fun signInWithIntent(intent: Intent) : String? {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        return credential.googleIdToken
    }
    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.web_client_id))
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }
}