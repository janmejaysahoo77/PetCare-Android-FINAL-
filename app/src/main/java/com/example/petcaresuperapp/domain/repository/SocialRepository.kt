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

    val currentUserId: String? get() = auth.currentUser?.uid

    val currentUserIdFlow: Flow<String?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { 
            trySend(it.currentUser?.uid)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

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
        
        // Fetch user data from Firestore
        val userDoc = firestore.collection("users").document(currentUser.uid).get().await()
        val userName = userDoc.getString("name") ?: currentUser.displayName ?: "User"
        val userProfileImage = userDoc.getString("photoUrl") ?: currentUser.photoUrl?.toString() ?: ""
        
        val post = SocialPost(
            postId = postId,
            userId = currentUser.uid,
            userName = userName,
            userProfileImage = userProfileImage,
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
        
        // Fetch user data from Firestore
        val userDoc = firestore.collection("users").document(currentUser.uid).get().await()
        val userName = userDoc.getString("name") ?: currentUser.displayName ?: "User"
        val userProfileImage = userDoc.getString("photoUrl") ?: currentUser.photoUrl?.toString() ?: ""
        
        val comment = Comment(
            commentId = commentId,
            userId = currentUser.uid,
            userName = userName,
            userProfileImage = userProfileImage,
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

    suspend fun deletePost(postId: String) {
        postsCollection.document(postId).delete().await()
    }

    fun getUsers(): Flow<List<com.example.petcaresuperapp.domain.models.User>> = callbackFlow {
        val listener = firestore.collection("users")
            .limit(100)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val users = snapshot?.documents?.mapNotNull { doc ->
                    val user = doc.toObject(com.example.petcaresuperapp.domain.models.User::class.java)
                    user?.copy(id = doc.id)
                } ?: emptyList()
                trySend(users)
            }
        awaitClose { listener.remove() }
    }
}
