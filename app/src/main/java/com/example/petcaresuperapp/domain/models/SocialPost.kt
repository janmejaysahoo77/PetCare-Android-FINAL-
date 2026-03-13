package com.example.petcaresuperapp.domain.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class SocialPost(
    @DocumentId val postId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userProfileImage: String = "",
    val imageUrl: String = "",
    val caption: String = "",
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val createdAt: Timestamp? = null
)
