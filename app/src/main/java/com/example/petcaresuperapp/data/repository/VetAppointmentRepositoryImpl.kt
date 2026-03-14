package com.example.petcaresuperapp.data.repository

import com.example.petcaresuperapp.data.model.VetAppointment
import com.example.petcaresuperapp.domain.models.Doctor
import com.example.petcaresuperapp.domain.repository.VetAppointmentRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class VetAppointmentRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : VetAppointmentRepository {

    override suspend fun createAppointment(appointment: VetAppointment): Result<String> {
        return try {
            val docRef = firestore.collection("vet_appointments").document()
            val appointmentWithId = appointment.copy(appointmentId = docRef.id)
            docRef.set(appointmentWithId).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getDoctors(): Flow<List<Doctor>> = callbackFlow {
        val listener = firestore.collection("vets")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val doctorsList = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Doctor.fromDocument(doc)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                } ?: emptyList()
                trySend(doctorsList)
            }
        awaitClose { listener.remove() }
    }
}
