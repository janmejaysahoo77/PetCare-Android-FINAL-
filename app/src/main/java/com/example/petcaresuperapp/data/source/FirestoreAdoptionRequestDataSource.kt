package com.example.petcaresuperapp.data.source

import com.example.petcaresuperapp.data.model.AdoptionRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreAdoptionRequestDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val requestsCollection = firestore.collection("adoption_requests")

    suspend fun sendAdoptionRequest(request: AdoptionRequest): Result<Unit> {
        return try {
            val data = hashMapOf(
                "petId" to request.petId,
                "petName" to request.petName,
                "userId" to request.userId,
                "userName" to request.userName,
                "contactInfo" to request.contactInfo,
                "message" to request.message,
                "status" to "PENDING",
                "createdAt" to FieldValue.serverTimestamp()
            )
            requestsCollection.add(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getUserAdoptionRequests(userId: String): Flow<List<AdoptionRequest>> = callbackFlow {
        val listenerRegistration = requestsCollection
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val requests = snapshot.documents.mapNotNull {
                        it.toObject(AdoptionRequest::class.java)
                    }
                    trySend(requests)
                } else {
                    trySend(emptyList())
                }
            }

        awaitClose { listenerRegistration.remove() }
    }
}
