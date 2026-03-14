package com.example.petcaresuperapp.data.model

data class VetAppointment(
    val appointmentId: String = "",
    val userId: String = "",
    val petId: String = "",
    val petName: String = "",
    val vetId: String = "",
    val vetName: String = "",
    val clinicName: String = "",
    val appointmentTimestamp: Long = 0L,
    val problemDescription: String = "",
    val status: String = "pending",
    val createdAt: Long = System.currentTimeMillis()
)
