package com.example.petcaresuperapp.domain.usecase

import com.example.petcaresuperapp.data.model.AdoptionRequest
import com.example.petcaresuperapp.domain.repository.AdoptionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserAdoptionRequestsUseCase @Inject constructor(
    private val adoptionRepository: AdoptionRepository
) {
    operator fun invoke(userId: String): Flow<List<AdoptionRequest>> {
        return adoptionRepository.getUserAdoptionRequests(userId)
    }
}
