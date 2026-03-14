package com.example.petcaresuperapp.domain.repository

import com.example.petcaresuperapp.data.model.ContactRequest
import kotlinx.coroutines.flow.Flow

interface ContactRequestRepository {
    suspend fun sendContactRequest(postId: String, petName: String, receiverId: String): Result<Unit>
    fun listenForUserRequests(userId: String): Flow<List<ContactRequest>>
    suspend fun updateRequestStatus(requestId: String, status: String): Result<Unit>
    suspend fun checkIfRequestAccepted(postId: String, senderId: String): Boolean
    suspend fun checkIfRequestPending(postId: String, senderId: String): Boolean
}
