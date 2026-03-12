package com.example.petcaresuperapp.domain.repository

import com.example.petcaresuperapp.core.util.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun login(email: String, password: String): Flow<Resource<Boolean>>
    fun signUp(name: String, email: String, password: String): Flow<Resource<Boolean>>
    fun logout(): Flow<Resource<Unit>>
    val currentUser: Flow<Boolean>
}
