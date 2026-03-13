package com.example.petcaresuperapp.domain.usecase

import com.example.petcaresuperapp.data.model.AdoptionRequest
import com.example.petcaresuperapp.domain.repository.AdoptionRepository
import javax.inject.Inject

class SendAdoptionRequestUseCase @Inject constructor(
    private val adoptionRepository: AdoptionRepository
) {
    suspend operator fun invoke(request: AdoptionRequest): Result<Unit> {
        return adoptionRepository.sendAdoptionRequest(request)
    }
}
