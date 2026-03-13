package com.example.petcaresuperapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PetCareApp : Application() {
    override fun onCreate() {
        super.onCreate()
        com.example.petcaresuperapp.utils.CloudinaryManager.initialize(this)
    }
}
