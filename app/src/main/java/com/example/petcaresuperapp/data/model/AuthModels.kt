package com.example.petcaresuperapp.data.model

import com.google.gson.annotations.SerializedName

/**
 * Request object for user registration.
 */
data class RegisterRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("displayName")
    val displayName: String,
    @SerializedName("phoneNumber")
    val phoneNumber: String? = null,
    @SerializedName("fcmToken")
    val fcmToken: String? = null,
    @SerializedName("role")
    val role: String? = "ROLE_PET_OWNER"
)

/**
 * Response object representing a user profile.
 */
data class UserResponse(
    @SerializedName("id")
    val id: String?,
    @SerializedName("email")
    val email: String,
    @SerializedName("displayName")
    val displayName: String?,
    @SerializedName("phoneNumber")
    val phoneNumber: String?,
    @SerializedName("photoUrl")
    val photoUrl: String?,
    @SerializedName("role")
    val role: String?,
    @SerializedName("fcmToken")
    val fcmToken: String?,
    @SerializedName("createdAt")
    val createdAt: String?
)
