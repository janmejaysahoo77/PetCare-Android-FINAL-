package com.example.petcaresuperapp.presentation.screens.lostfound

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.example.petcaresuperapp.data.model.LostFoundPost
import com.example.petcaresuperapp.domain.repository.LostFoundRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

@HiltViewModel
class LostFoundViewModel @Inject constructor(
    private val repository: LostFoundRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _posts = MutableStateFlow<List<LostFoundPost>>(emptyList())
    val posts: StateFlow<List<LostFoundPost>> = _posts.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    init {
        fetchPosts()
    }

    private fun fetchPosts() {
        repository.getLostFoundPosts()
            .onEach { _posts.value = it }
            .launchIn(viewModelScope)
    }

    fun submitReport(
        petName: String,
        animalType: String,
        breed: String,
        location: String,
        description: String,
        status: String,
        reward: String?,
        mediaUris: List<Uri>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isSubmitting.value = true
            try {
                val imageUrl = if (mediaUris.isNotEmpty()) {
                    uploadImage(mediaUris.first()) ?: throw Exception("Image upload failed")
                } else {
                    "" // Default or placeholder
                }

                val post = LostFoundPost(
                    imageUrl = imageUrl,
                    petName = petName,
                    animalType = animalType,
                    breed = breed,
                    status = status,
                    location = location,
                    description = description,
                    reward = reward,
                    ownerId = auth.currentUser?.uid ?: "",
                    timestamp = System.currentTimeMillis()
                )

                repository.addLostFoundPost(post).onSuccess {
                    onSuccess()
                }.onFailure {
                    onError(it.message ?: "Unknown error")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Failed to submit report")
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    private suspend fun uploadImage(uri: Uri): String? = suspendCancellableCoroutine { continuation ->
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
}
