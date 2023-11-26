package nau.android.taskify.data.repository

import kotlinx.coroutines.flow.Flow
import nau.android.taskify.data.model.Result

interface IAuthRepository {


    suspend fun checkIfSignedIn(): Flow<Boolean>

    suspend fun createAccount(email: String, password: String)

    suspend fun signIn(email: String, password: String)

    suspend fun signInWithGoogle(tokedId: String)

    suspend fun signOut()

    suspend fun sendPasswordResetEmail(email: String): Result<Unit>

}