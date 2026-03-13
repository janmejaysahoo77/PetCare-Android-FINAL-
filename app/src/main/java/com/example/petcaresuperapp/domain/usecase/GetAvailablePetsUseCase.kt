package com.example.petcaresuperapp.domain.usecase

import com.example.petcaresuperapp.data.model.Pet
import com.example.petcaresuperapp.domain.repository.PetRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAvailablePetsUseCase @Inject constructor(
    private val petRepository: PetRepository
) {
    operator fun invoke(): Flow<List<Pet>> {
        return petRepository.getAvailablePets()
    }
}
