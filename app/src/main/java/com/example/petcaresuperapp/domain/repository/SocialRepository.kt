package com.example.petcaresuperapp.domain.repository

import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.petcaresuperapp.domain.models.Comment
import com.example.petcaresuperapp.domain.models.SocialPost
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@Singleton
class SocialRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val postsCollection = firestore.collection("posts")

    fun getPosts(): Flow<List<SocialPost>> = callbackFlow {
        val listener = postsCollection
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(20)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val posts = snapshot?.documents?.mapNotNull { it.toObject(SocialPost::class.java) } ?: emptyList()
                trySend(posts)
            }
        awaitClose { listener.remove() }
    }

    suspend fun uploadImage(uri: Uri): String? = suspendCancellableCoroutine { continuation ->
        MediaManager.get().upload(uri)
            .unsigned("pet_upload") // Using existing preset
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {}
                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {
                    val secureUrl = resultData?.get("secure_url") as? String
                    if (continuation.isActive) continuation.resume(secureUrl)
                }
                override fun onError(requestId: String?, error: ErrorInfo?) {
                    if (continuation.isActive) continuation.resume(null)
                }
                override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                    if (continuation.isActive) continuation.resume(null)
                }
            })
            .dispatch()
    }

    suspend fun createPost(caption: String, imageUrl: String) {
        val currentUser = auth.currentUser ?: return
        val postId = UUID.randomUUID().toString()
        
        // Fetch user details for the post (In a real app, this might come from a 'users' collection)
        // For simplicity, we'll use current user info
        val post = SocialPost(
            postId = postId,
            userId = currentUser.uid,
            userName = currentUser.displayName ?: "User",
            userProfileImage = currentUser.photoUrl?.toString() ?: "",
            imageUrl = imageUrl,
            caption = caption,
            createdAt = Timestamp.now()
        )
        
        postsCollection.document(postId).set(post).await()
    }

    suspend fun toggleLike(postId: String) {
        val userId = auth.currentUser?.uid ?: return
        val likeRef = postsCollection.document(postId).collection("likes").document(userId)
        
        val likeDoc = likeRef.get().await()
        if (likeDoc.exists()) {
            likeRef.delete().await()
            postsCollection.document(postId).update("likeCount", FieldValue.increment(-1)).await()
        } else {
            likeRef.set(mapOf("userId" to userId, "liked" to true, "createdAt" to Timestamp.now())).await()
            postsCollection.document(postId).update("likeCount", FieldValue.increment(1)).await()
        }
    }

    fun getComments(postId: String): Flow<List<Comment>> = callbackFlow {
        val listener = postsCollection.document(postId).collection("comments")
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val comments = snapshot?.documents?.mapNotNull { it.toObject(Comment::class.java) } ?: emptyList()
                trySend(comments)
            }
        awaitClose { listener.remove() }
    }

    suspend fun addComment(postId: String, commentText: String) {
        val currentUser = auth.currentUser ?: return
        val commentId = UUID.randomUUID().toString()
        
        val comment = Comment(
            commentId = commentId,
            userId = currentUser.uid,
            userName = currentUser.displayName ?: "User",
            userProfileImage = currentUser.photoUrl?.toString() ?: "",
            comment = commentText,
            createdAt = Timestamp.now()
        )
        
        postsCollection.document(postId).collection("comments").document(commentId).set(comment).await()
        postsCollection.document(postId).update("commentCount", FieldValue.increment(1)).await()
    }

    suspend fun isLiked(postId: String): Boolean {
        val userId = auth.currentUser?.uid ?: return false
        return postsCollection.document(postId).collection("likes").document(userId).get().await().exists()
    }
}
