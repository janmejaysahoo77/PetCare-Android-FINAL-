package com.example.petcaresuperapp.domain.repository

import com.example.petcaresuperapp.data.model.LostFoundPost
import kotlinx.coroutines.flow.Flow

interface LostFoundRepository {
    fun getLostFoundPosts(): Flow<List<LostFoundPost>>
    suspend fun addLostFoundPost(post: LostFoundPost): Result<Unit>
}
