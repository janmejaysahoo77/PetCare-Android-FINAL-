package com.example.petcaresuperapp.domain.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Order(
    @DocumentId val orderId: String = "",
    val userId: String = "",
    val items: List<CartItem> = emptyList(),
    val totalAmount: Double = 0.0,
    val status: String = "",
    val createdAt: Timestamp? = null
)
