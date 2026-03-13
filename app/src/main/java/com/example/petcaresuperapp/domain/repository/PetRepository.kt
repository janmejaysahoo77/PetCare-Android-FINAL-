package com.example.petcaresuperapp.domain.repository

import com.example.petcaresuperapp.data.model.Pet
import kotlinx.coroutines.flow.Flow

interface PetRepository {
    fun getAvailablePets(): Flow<List<Pet>>
    suspend fun getPetDetails(petId: String): Pet?
}
