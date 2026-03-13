package com.example.petcaresuperapp.data.repository

import com.example.petcaresuperapp.data.model.VetAppointment
import com.example.petcaresuperapp.domain.repository.VetAppointmentRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class VetAppointmentRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : VetAppointmentRepository {

    override suspend fun createAppointment(appointment: VetAppointment): Result<Unit> {
        return try {
            val docRef = firestore.collection("vet_appointments").document()
            val appointmentWithId = appointment.copy(appointmentId = docRef.id)
            docRef.set(appointmentWithId).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
