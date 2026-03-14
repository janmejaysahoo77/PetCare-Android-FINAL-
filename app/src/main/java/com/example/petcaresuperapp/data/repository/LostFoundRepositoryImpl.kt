package com.example.petcaresuperapp.data.repository

import com.example.petcaresuperapp.data.model.LostFoundPost
import com.example.petcaresuperapp.domain.repository.LostFoundRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LostFoundRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : LostFoundRepository {

    private val postsCollection = firestore.collection("lost_found_posts")

    override fun getLostFoundPosts(): Flow<List<LostFoundPost>> = callbackFlow {
        val subscription = postsCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val posts = snapshot.toObjects(LostFoundPost::class.java)
                    trySend(posts)
                }
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun addLostFoundPost(post: LostFoundPost): Result<Unit> = try {
        postsCollection.add(post).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
