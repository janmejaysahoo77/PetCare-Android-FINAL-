package com.example.petcaresuperapp.data.model

import com.google.firebase.firestore.DocumentId

data class MedicalRecord(
    @DocumentId val recordId: String = "",
    val petId: String = "",
    val petName: String = "",
    val diagnosis: String = "",
    val prescription: String = "",
    val treatmentNotes: String = "",
    val vaccinationName: String = "",
    val vaccinationDate: String = "",
    val nextVisitDate: String = "",
    val vetName: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
