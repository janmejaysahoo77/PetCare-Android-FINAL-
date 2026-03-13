package com.example.petcaresuperapp.domain.repository

import com.example.petcaresuperapp.data.model.MedicalRecord

interface MedicalRecordRepository {
    suspend fun getMedicalRecordsForPet(petId: String): Result<List<MedicalRecord>>
}
