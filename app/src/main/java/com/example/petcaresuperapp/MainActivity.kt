package com.example.petcaresuperapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import android.content.Intent
import com.example.petcaresuperapp.presentation.navigation.SetupNavGraph
import com.example.petcaresuperapp.service.PetCareNotificationService
import com.example.petcaresuperapp.ui.theme.PetCareSuperAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PetCareSuperAppTheme {
                val navController = rememberNavController()
                SetupNavGraph(navController = navController)
            }
        }
        
        // Start Notification Service
        val intent = Intent(this, PetCareNotificationService::class.java)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }
}
