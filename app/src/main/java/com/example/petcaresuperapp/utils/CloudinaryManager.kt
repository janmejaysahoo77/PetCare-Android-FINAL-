package com.example.petcaresuperapp.utils

import android.content.Context
import com.cloudinary.android.MediaManager

object CloudinaryManager {
    fun initialize(context: Context) {
        val config = mapOf(
            "cloud_name" to "drtjvypq7",
            "api_key" to "578155299669965",
            "api_secret" to "jonjam7wRpL3n85CxozB6GJnmc4"
        )
        try {
            MediaManager.init(context, config)
        } catch (e: Exception) {
            // Already initialized
        }
    }
}
