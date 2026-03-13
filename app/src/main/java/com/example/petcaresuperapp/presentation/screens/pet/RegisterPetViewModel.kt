package com.example.petcaresuperapp.presentation.screens.pet

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@HiltViewModel
class RegisterPetViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterPetState>(RegisterPetState.Idle)
    val registerState: StateFlow<RegisterPetState> = _registerState.asStateFlow()

    fun registerPet(
        petName: String,
        breed: String,
        age: String,
        weight: String,
        gender: String,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            _registerState.value = RegisterPetState.Loading
            try {
                var imageUrl = ""
                if (imageUri != null) {
                    imageUrl = uploadPetPhoto(imageUri) ?: throw Exception("Failed to upload photo")
                }

                val userId = auth.currentUser?.uid ?: ""
                val petData = hashMapOf(
                    "petName" to petName,
                    "breed" to breed,
                    "age" to age,
                    "weight" to weight,
                    "gender" to gender,
                    "imageUrl" to imageUrl,
                    "userId" to userId,
                    "createdAt" to com.google.firebase.Timestamp.now()
                )
                firestore.collection("petslist").add(petData).await()
                _registerState.value = RegisterPetState.Success
            } catch (e: Exception) {
                _registerState.value = RegisterPetState.Error(e.message ?: "Failed to register pet")
            }
        }
    }

    private suspend fun uploadPetPhoto(uri: Uri): String? = suspendCancellableCoroutine { continuation ->
        MediaManager.get().upload(uri)
            .unsigned("pet_upload")
            .callback(object : com.cloudinary.android.callback.UploadCallback {
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

    fun resetState() {
        _registerState.value = RegisterPetState.Idle
    }
}

sealed class RegisterPetState {
    object Idle : RegisterPetState()
    object Loading : RegisterPetState()
    object Success : RegisterPetState()
    data class Error(val message: String) : RegisterPetState()
}
