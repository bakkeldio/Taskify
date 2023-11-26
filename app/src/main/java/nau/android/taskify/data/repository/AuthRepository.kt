package nau.android.taskify.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import nau.android.taskify.data.model.Result
import javax.inject.Inject

class AuthRepository @Inject constructor(private val firebaseAuth: FirebaseAuth) : IAuthRepository {

    override suspend fun checkIfSignedIn(): Flow<Boolean> {
        return callbackFlow {
            val authStateListener = FirebaseAuth.AuthStateListener { auth ->
                trySend(auth.currentUser != null)
            }
            firebaseAuth.addAuthStateListener(authStateListener)
            awaitClose {
                firebaseAuth.removeAuthStateListener(authStateListener)
            }
        }
    }

    override suspend fun createAccount(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).await()
    }

    override suspend fun signIn(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password).await()
    }

    override suspend fun signInWithGoogle(tokedId: String) {
        val credential = GoogleAuthProvider.getCredential(tokedId, null)
        firebaseAuth.signInWithCredential(credential).await()
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }


}