package com.example.petcaresuperapp.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Pet(
    @DocumentId
    var petIdInternal: String? = null,
    val name: String = "",
    val breed: String = "",
    val species: String = "",
    val gender: String = "",
    val age: String = "",
    val weight: String = "",
    val dateOfBirth: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val status: String = "", // Available / Adopted
    val shelterId: String = "",
    val createdAt: Timestamp? = null
)
