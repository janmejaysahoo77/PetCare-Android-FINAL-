package com.example.petcaresuperapp.data.repository

import com.example.petcaresuperapp.data.model.AdoptionRequest
import com.example.petcaresuperapp.data.source.FirestoreAdoptionRequestDataSource
import com.example.petcaresuperapp.domain.repository.AdoptionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AdoptionRepositoryImpl @Inject constructor(
    private val adoptionRequestDataSource: FirestoreAdoptionRequestDataSource
) : AdoptionRepository {
    override suspend fun sendAdoptionRequest(request: AdoptionRequest): Result<Unit> {
        return adoptionRequestDataSource.sendAdoptionRequest(request)
    }

    override fun getUserAdoptionRequests(userId: String): Flow<List<AdoptionRequest>> {
        return adoptionRequestDataSource.getUserAdoptionRequests(userId)
    }
}
