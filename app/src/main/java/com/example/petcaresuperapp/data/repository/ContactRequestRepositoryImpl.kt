package com.example.petcaresuperapp.data.repository

import com.example.petcaresuperapp.data.model.ContactRequest
import com.example.petcaresuperapp.domain.repository.ContactRequestRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ContactRequestRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ContactRequestRepository {

    override suspend fun sendContactRequest(postId: String, petName: String, receiverId: String): Result<Unit> = try {
        val senderId = auth.currentUser?.uid ?: throw Exception("User not authenticated")
        val requestId = firestore.collection("contact_requests").document().id
        
        val request = ContactRequest(
            requestId = requestId,
            postId = postId,
            petName = petName,
            senderId = senderId,
            receiverId = receiverId,
            status = "pending",
            timestamp = System.currentTimeMillis()
        )
        
        firestore.collection("contact_requests").document(requestId).set(request).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun listenForUserRequests(userId: String): Flow<List<ContactRequest>> = callbackFlow {
        val listener = firestore.collection("contact_requests")
            .whereEqualTo("receiverId", userId)
            .whereEqualTo("status", "pending")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val requests = snapshot?.documents?.mapNotNull { it.toObject(ContactRequest::class.java) } ?: emptyList()
                trySend(requests)
            }
        
        awaitClose { listener.remove() }
    }

    override suspend fun updateRequestStatus(requestId: String, status: String): Result<Unit> = try {
        firestore.collection("contact_requests").document(requestId)
            .update("status", status)
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun checkIfRequestAccepted(postId: String, senderId: String): Boolean {
        return try {
            val snapshot = firestore.collection("contact_requests")
                .whereEqualTo("postId", postId)
                .whereEqualTo("senderId", senderId)
                .whereEqualTo("status", "accepted")
                .get()
                .await()
            !snapshot.isEmpty
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun checkIfRequestPending(postId: String, senderId: String): Boolean {
        return try {
            val snapshot = firestore.collection("contact_requests")
                .whereEqualTo("postId", postId)
                .whereEqualTo("senderId", senderId)
                .whereEqualTo("status", "pending")
                .get()
                .await()
            !snapshot.isEmpty
        } catch (e: Exception) {
            false
        }
    }
}
