package com.example.petcaresuperapp.data.model

import com.google.firebase.firestore.DocumentId

data class LostFoundPost(
    @DocumentId val id: String = "",
    val imageUrl: String = "",
    val petName: String = "",
    val animalType: String = "",
    val breed: String = "",
    val status: String = "",
    val location: String = "",
    val description: String = "",
    val reward: String? = null,
    val ownerId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
