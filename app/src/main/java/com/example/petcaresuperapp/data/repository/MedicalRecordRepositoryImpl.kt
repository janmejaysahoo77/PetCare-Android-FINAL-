package com.example.petcaresuperapp.data.repository

import com.example.petcaresuperapp.data.model.MedicalRecord
import com.example.petcaresuperapp.domain.repository.MedicalRecordRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MedicalRecordRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : MedicalRecordRepository {

    override suspend fun getMedicalRecordsForPet(petId: String): Result<List<MedicalRecord>> {
        return try {
            val querySnapshot = firestore.collection("medical_records")
                .whereEqualTo("petId", petId)
                .get()
                .await()
                
            val records = querySnapshot.documents.mapNotNull { it.toObject(MedicalRecord::class.java) }
            Result.success(records)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
