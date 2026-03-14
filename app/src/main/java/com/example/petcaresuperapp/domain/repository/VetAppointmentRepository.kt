package com.example.petcaresuperapp.domain.repository

import com.example.petcaresuperapp.data.model.VetAppointment
import com.example.petcaresuperapp.domain.models.Doctor
import kotlinx.coroutines.flow.Flow

interface VetAppointmentRepository {
    suspend fun createAppointment(appointment: VetAppointment): Result<String>
    fun getDoctors(): Flow<List<Doctor>>
}
