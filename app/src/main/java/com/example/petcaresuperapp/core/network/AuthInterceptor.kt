package com.example.petcaresuperapp.core.network

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Interceptor that adds the Firebase ID Token to the Authorization header.
 */
class AuthInterceptor @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Skip adding token for specific endpoints if needed (e.g., health check)
        // For now, we apply it to all requests as per the guide.
        
        val currentUser = firebaseAuth.currentUser
        val requestBuilder = originalRequest.newBuilder()

        if (currentUser != null) {
            try {
                // Fetch the ID token synchronously
                // Note: Tasks.await() should not be called on the main thread.
                // OkHttp interceptors run on background threads.
                val tokenResult = Tasks.await(currentUser.getIdToken(false), 30, TimeUnit.SECONDS)
                val token = tokenResult.token
                
                if (token != null) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }
            } catch (e: Exception) {
                // Log error or handle token retrieval failure
                e.printStackTrace()
            }
        }

        return chain.proceed(requestBuilder.build())
    }
}
