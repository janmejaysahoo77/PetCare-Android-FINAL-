package com.example.petcaresuperapp.data.remote

import com.example.petcaresuperapp.data.model.RegisterRequest
import com.example.petcaresuperapp.data.model.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApiService {

    @POST("api/v1/auth/register")
    suspend fun registerUser(
        @Body request: RegisterRequest
    ): Response<UserResponse>

    @GET("api/v1/auth/me")
    suspend fun getCurrentUser(): Response<UserResponse>

    @POST("api/v1/auth/verify")
    suspend fun verifyToken(): Response<UserResponse>
}
