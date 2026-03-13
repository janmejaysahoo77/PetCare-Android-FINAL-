package com.example.petcaresuperapp.data.repository

import com.example.petcaresuperapp.core.util.Resource
import com.example.petcaresuperapp.data.model.RegisterRequest
import com.example.petcaresuperapp.data.remote.AuthApiService
import com.example.petcaresuperapp.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val authApiService: AuthApiService
) : AuthRepository {

    override fun login(email: String, password: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            // After Firebase login, verify with backend
            val response = authApiService.getCurrentUser()
            if (response.isSuccessful) {
                emit(Resource.Success(true))
            } else {
                emit(Resource.Error("Backend verification failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }

    override fun signUp(name: String, email: String, password: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            // After Firebase signup, register with backend
            val registerRequest = RegisterRequest(
                email = email,
                displayName = name
            )
            val response = authApiService.registerUser(registerRequest)
            if (response.isSuccessful) {
                emit(Resource.Success(true))
            } else {
                emit(Resource.Error("Backend registration failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }

    override fun logout(): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            firebaseAuth.signOut()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }

    override val currentUser: Flow<Boolean> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser != null)
        }
        firebaseAuth.addAuthStateListener(authStateListener)
        awaitClose {
            firebaseAuth.removeAuthStateListener(authStateListener)
        }
    }

    override fun registerWithBackend(
        name: String,
        email: String,
        phoneNumber: String?,
        fcmToken: String?
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val request = RegisterRequest(
                email = email,
                displayName = name,
                phoneNumber = phoneNumber,
                fcmToken = fcmToken
            )
            val response = authApiService.registerUser(request)
            if (response.isSuccessful) {
                emit(Resource.Success(Unit))
            } else {
                emit(Resource.Error("Backend registration failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }

    override fun fetchUserProfile(): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val response = authApiService.getCurrentUser()
            if (response.isSuccessful) {
                emit(Resource.Success(Unit))
            } else {
                emit(Resource.Error("Failed to fetch profile: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }
}
