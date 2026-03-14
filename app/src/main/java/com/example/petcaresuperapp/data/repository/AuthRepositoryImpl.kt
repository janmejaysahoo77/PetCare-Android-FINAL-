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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import com.google.firebase.auth.userProfileChangeRequest
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import android.net.Uri
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
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
                // Save name and email in firestore
                val user = hashMapOf(
                    "name" to name,
                    "email" to email,
                    "createdAt" to Timestamp.now()
                )
                firebaseAuth.currentUser?.uid?.let { uid ->
                    firestore.collection("users").document(uid).set(user).await()
                }
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

    override fun getUserData(): Flow<Resource<com.example.petcaresuperapp.domain.models.User>> = flow {
        emit(Resource.Loading())
        try {
            val uid = firebaseAuth.currentUser?.uid
            if (uid != null) {
                val document = firestore.collection("users").document(uid).get().await()
                if (document.exists()) {
                    val name = document.getString("name") ?: ""
                    val email = document.getString("email") ?: ""
                    val photoUrl = document.getString("photoUrl")
                    emit(Resource.Success(com.example.petcaresuperapp.domain.models.User(id = uid, name = name, email = email, photoUrl = photoUrl)))
                } else {
                    emit(Resource.Error("No user profile found"))
                }
            } else {
                emit(Resource.Error("User not logged in"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }

    override fun uploadProfilePhoto(uri: Uri): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val secureUrl = uploadToCloudinary(uri)
            if (secureUrl != null) {
                val uid = firebaseAuth.currentUser?.uid
                if (uid != null) {
                    // Update Firestore
                    firestore.collection("users").document(uid).update("photoUrl", secureUrl).await()
                    
                    // Update FirebaseAuth
                    val profileUpdates = userProfileChangeRequest {
                        photoUri = Uri.parse(secureUrl)
                    }
                    firebaseAuth.currentUser?.updateProfile(profileUpdates)?.await()
                    
                    emit(Resource.Success(secureUrl))
                } else {
                    emit(Resource.Error("User not logged in"))
                }
            } else {
                emit(Resource.Error("Failed to upload image to Cloudinary"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }

    private suspend fun uploadToCloudinary(uri: Uri): String? = suspendCancellableCoroutine { continuation ->
        MediaManager.get().upload(uri)
            .unsigned("pet_upload")
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {}
                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {
                    val secureUrl = resultData?.get("secure_url") as? String
                    if (continuation.isActive) continuation.resume(secureUrl)
                }
                override fun onError(requestId: String?, error: ErrorInfo?) {
                    if (continuation.isActive) continuation.resume(null)
                }
                override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                    if (continuation.isActive) continuation.resume(null)
                }
            })
            .dispatch()
    }
}
