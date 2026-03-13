package com.example.petcaresuperapp.domain.usecase

import com.example.petcaresuperapp.data.model.Pet
import com.example.petcaresuperapp.domain.repository.PetRepository
import javax.inject.Inject

class GetPetDetailsUseCase @Inject constructor(
    private val petRepository: PetRepository
) {
    suspend operator fun invoke(petId: String): Pet? {
        return petRepository.getPetDetails(petId)
    }
}
