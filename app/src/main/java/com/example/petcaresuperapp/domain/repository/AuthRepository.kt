package com.example.petcaresuperapp.domain.repository

import android.net.Uri
import com.example.petcaresuperapp.core.util.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun login(email: String, password: String): Flow<Resource<Boolean>>
    fun signUp(name: String, email: String, password: String): Flow<Resource<Boolean>>
    fun logout(): Flow<Resource<Unit>>
    val currentUser: Flow<Boolean>

    /**
     * Syncs the user registration with the PetCare+ backend.
     */
    fun registerWithBackend(
        name: String,
        email: String,
        phoneNumber: String? = null,
        fcmToken: String? = null
    ): Flow<Resource<Unit>>

    /**
     * Fetches the current user profile from the PetCare+ backend.
     */
    fun fetchUserProfile(): Flow<Resource<Unit>>

    /**
     * Fetches the user data from Firestore.
     */
    fun getUserData(): Flow<Resource<com.example.petcaresuperapp.domain.models.User>>

    /**
     * Uploads the user's profile photo to Cloudinary and updates Firestore.
     */
    fun uploadProfilePhoto(uri: Uri): Flow<Resource<String>>
}
