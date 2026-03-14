package com.example.petcaresuperapp.presentation.screens.pet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcaresuperapp.data.model.ContactRequest
import com.example.petcaresuperapp.data.model.LostFoundPost
import com.example.petcaresuperapp.data.model.VetAppointment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val currentUserId: String get() = auth.currentUser?.uid ?: ""

    init {
        fetchNotifications()
    }

    private fun fetchNotifications() {
        val currentUser = auth.currentUser ?: return
        
        viewModelScope.launch {
            _isLoading.value = true
            
            // Listen to Vet Appointments
            val appointmentFlow = callbackFlow {
                val listener = firestore.collection("vet_appointments")
                    .whereEqualTo("userId", currentUser.uid)
                    .orderBy("appointmentTimestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener { snapshot, _ ->
                        val items = snapshot?.documents?.mapNotNull { doc ->
                            try {
                                val appt = doc.toObject(VetAppointment::class.java)?.copy(appointmentId = doc.id)
                                appt?.let { NotificationItem.Appointment(it) }
                            } catch (e: Exception) { null }
                        } ?: emptyList()
                        trySend(items)
                    }
                awaitClose { listener.remove() }
            }

            // Listen to Lost & Found Posts (Tracking)
            val trackingFlow = callbackFlow {
                val listener = firestore.collection("lost_found_posts")
                    .whereEqualTo("ownerId", currentUser.uid)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener { snapshot, _ ->
                        val items = snapshot?.documents?.mapNotNull { doc ->
                            try {
                                val post = doc.toObject(LostFoundPost::class.java)?.copy(id = doc.id)
                                post?.let { NotificationItem.Tracking(it) }
                            } catch (e: Exception) { null }
                        } ?: emptyList()
                        trySend(items)
                    }
                awaitClose { listener.remove() }
            }

            // Listen to Contact Requests (Both received and sent)
            val receivedRequestsFlow = callbackFlow {
                val listener = firestore.collection("contact_requests")
                    .whereEqualTo("receiverId", currentUser.uid)
                    .addSnapshotListener { snapshot, _ ->
                        val items = snapshot?.documents?.mapNotNull { doc ->
                            try {
                                val req = doc.toObject(ContactRequest::class.java)
                                req?.let { NotificationItem.Contact(it) }
                            } catch (e: Exception) { null }
                        } ?: emptyList()
                        trySend(items)
                    }
                awaitClose { listener.remove() }
            }

            val sentRequestsFlow = callbackFlow {
                val listener = firestore.collection("contact_requests")
                    .whereEqualTo("senderId", currentUser.uid)
                    .addSnapshotListener { snapshot, _ ->
                        val items = snapshot?.documents?.mapNotNull { doc ->
                            try {
                                val req = doc.toObject(ContactRequest::class.java)
                                req?.let { NotificationItem.Contact(it) }
                            } catch (e: Exception) { null }
                        } ?: emptyList()
                        trySend(items)
                    }
                awaitClose { listener.remove() }
            }

            // Combine all flows
            combine(appointmentFlow, trackingFlow, receivedRequestsFlow, sentRequestsFlow) { appointments, tracking, received, sent ->
                val allRequests = (received + sent).distinctBy { (it as NotificationItem.Contact).request.requestId }
                (appointments + tracking + allRequests).sortedByDescending { item ->
                    when (item) {
                        is NotificationItem.Appointment -> item.appointment.createdAt
                        is NotificationItem.Tracking -> item.post.timestamp
                        is NotificationItem.Contact -> item.request.timestamp
                    }
                }
            }.collect { combinedList ->
                _notifications.value = combinedList
                _isLoading.value = false
            }
        }
    }

    fun updateRequestStatus(requestId: String, status: String) {
        viewModelScope.launch {
            try {
                firestore.collection("contact_requests").document(requestId)
                    .update("status", status)
                    .await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

sealed class NotificationItem {
    data class Appointment(val appointment: VetAppointment) : NotificationItem()
    data class Tracking(val post: LostFoundPost) : NotificationItem()
    data class Contact(val request: ContactRequest) : NotificationItem()
}
