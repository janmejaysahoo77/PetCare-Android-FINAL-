package com.example.petcaresuperapp.data.source

import com.example.petcaresuperapp.data.model.Pet
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestorePetDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val petsCollection = firestore.collection("pets")

    fun getAvailablePets(): Flow<List<Pet>> = callbackFlow {
        val listenerRegistration = petsCollection
            .whereEqualTo("status", "Available")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val pets = snapshot.documents.mapNotNull {
                        it.toObject(Pet::class.java)
                    }
                    trySend(pets)
                } else {
                    trySend(emptyList())
                }
            }
            
        awaitClose { listenerRegistration.remove() }
    }

    suspend fun getPetDetails(petId: String): Pet? {
        return try {
            val documentSnapshot = petsCollection.document(petId).get().await()
            documentSnapshot.toObject(Pet::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
