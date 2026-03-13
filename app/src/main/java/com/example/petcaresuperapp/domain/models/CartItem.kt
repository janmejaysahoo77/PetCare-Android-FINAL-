package com.example.petcaresuperapp.domain.models

data class CartItem(
    val productId: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val quantity: Int = 0
)
