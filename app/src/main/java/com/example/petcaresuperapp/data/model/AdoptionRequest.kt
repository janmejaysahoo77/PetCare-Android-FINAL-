package com.example.petcaresuperapp.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class AdoptionRequest(
    @DocumentId
    var requestId: String? = null,
    val petId: String = "",
    val petName: String = "",
    val userId: String = "",
    val userName: String = "",
    val contactInfo: String = "",
    val message: String = "",
    val status: String = "PENDING", // PENDING / APPROVED / REJECTED
    val createdAt: Timestamp? = null
)
