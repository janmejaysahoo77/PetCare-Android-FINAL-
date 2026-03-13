package com.example.petcaresuperapp.domain.repository

import com.example.petcaresuperapp.data.model.AdoptionRequest
import kotlinx.coroutines.flow.Flow

interface AdoptionRepository {
    suspend fun sendAdoptionRequest(request: AdoptionRequest): Result<Unit>
    fun getUserAdoptionRequests(userId: String): Flow<List<AdoptionRequest>>
}
