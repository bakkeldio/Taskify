package nau.android.taskify.data.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import nau.android.taskify.data.repository.AuthRepository
import nau.android.taskify.data.repository.IAuthRepository
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class AuthModule {

    companion object{
        @Singleton
        @Provides
        fun provideFirebaseAuth(): FirebaseAuth {
            return Firebase.auth
        }
    }

    @Binds
    abstract fun bindAuthRepo(authRepository: AuthRepository): IAuthRepository
}