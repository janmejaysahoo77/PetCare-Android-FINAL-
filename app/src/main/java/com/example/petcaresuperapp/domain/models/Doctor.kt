package com.example.petcaresuperapp.domain.models

import com.google.firebase.firestore.DocumentSnapshot

data class Doctor(
    val uid: String = "",
    val name: String = "",
    val clinicName: String = "",
    val specialization: String = "",
    val rating: String = "0.0",
    val location: Map<String, String> = emptyMap(),
    val profileImage: String = "",
    val clinicAddress: String = "",
    val bio: String = "",
    val experience: String = "",
    val email: String = "",
    val status: String = "offline"
) {
    val specialty: String get() = specialization
    val latitude: Double get() = location["latitude"]?.toDoubleOrNull() ?: 20.247856
    val longitude: Double get() = location["longitude"]?.toDoubleOrNull() ?: 85.801154

    companion object {
        fun fromDocument(doc: DocumentSnapshot): Doctor {
            // Safely parse the map avoiding ClassCastExceptions on values
            val locationMap = doc.get("location") as? Map<*, *>
            val parsedLocation = mutableMapOf<String, String>()
            locationMap?.forEach { (k, v) ->
                if (k != null && v != null) {
                    parsedLocation[k.toString()] = v.toString()
                }
            }

            return Doctor(
                uid = doc.id,
                name = doc.getString("name") ?: "",
                clinicName = doc.getString("clinicName") ?: "",
                specialization = doc.getString("specialization") ?: "",
                rating = doc.get("rating")?.toString() ?: "0.0",
                location = parsedLocation,
                profileImage = doc.getString("profileImage") ?: "",
                clinicAddress = doc.getString("clinicAddress") ?: "",
                bio = doc.getString("bio") ?: "",
                experience = doc.get("experience")?.toString() ?: "",
                email = doc.getString("email") ?: "",
                status = doc.getString("status") ?: "offline"
            )
        }
    }
}
