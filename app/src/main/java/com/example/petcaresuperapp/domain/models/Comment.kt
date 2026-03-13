package com.example.petcaresuperapp.domain.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Comment(
    @DocumentId val commentId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userProfileImage: String = "",
    val comment: String = "",
    val createdAt: Timestamp? = null
)
