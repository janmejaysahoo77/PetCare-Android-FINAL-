package com.example.petcaresuperapp.presentation.screens.pet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcaresuperapp.data.model.VetAppointment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _appointments = MutableStateFlow<List<VetAppointment>>(emptyList())
    val appointments: StateFlow<List<VetAppointment>> = _appointments.asStateFlow()

    init {
        fetchUserAppointments()
    }

    private fun fetchUserAppointments() {
        val currentUser = auth.currentUser ?: return
        
        firestore.collection("vet_appointments")
            .whereEqualTo("userId", currentUser.uid)
            .orderBy("appointmentTimestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    error.printStackTrace()
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val appointmentsList = snapshot.documents.mapNotNull { doc ->
                        try {
                            VetAppointment(
                                appointmentId = doc.id,
                                userId = doc.getString("userId") ?: "",
                                petId = doc.getString("petId") ?: "",
                                petName = doc.getString("petName") ?: "",
                                vetId = doc.getString("vetId") ?: "",
                                vetName = doc.getString("vetName") ?: "",
                                clinicName = doc.getString("clinicName") ?: "",
                                appointmentTimestamp = doc.getLong("appointmentTimestamp") ?: 0L,
                                problemDescription = doc.getString("problemDescription") ?: "",
                                status = doc.getString("status") ?: "pending",
                                createdAt = doc.getLong("createdAt") ?: 0L
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    _appointments.value = appointmentsList
                }
            }
    }
}
