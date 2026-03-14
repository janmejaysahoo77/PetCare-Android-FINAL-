package com.example.petcaresuperapp.data.model

data class ContactRequest(
    val requestId: String = "",
    val postId: String = "",
    val petName: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val status: String = "pending",
    val timestamp: Long = System.currentTimeMillis()
)
