package com.example.petcaresuperapp.data.repository

import com.example.petcaresuperapp.data.model.Pet
import com.example.petcaresuperapp.data.source.FirestorePetDataSource
import com.example.petcaresuperapp.domain.repository.PetRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PetRepositoryImpl @Inject constructor(
    private val petDataSource: FirestorePetDataSource
) : PetRepository {
    override fun getAvailablePets(): Flow<List<Pet>> {
        return petDataSource.getAvailablePets()
    }

    override suspend fun getPetDetails(petId: String): Pet? {
        return petDataSource.getPetDetails(petId)
    }
}
