package com.example.petcaresuperapp.domain.repository

import com.example.petcaresuperapp.data.model.VetAppointment

interface VetAppointmentRepository {
    suspend fun createAppointment(appointment: VetAppointment): Result<Unit>
}
