package com.example.petcaresuperapp.domain.models

import com.google.firebase.firestore.DocumentId

data class StoreProduct(
    @DocumentId val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val description: String = "",
    val category: String = "",
    val stock: Int = 0,
    val imageUrl: String = ""
)
