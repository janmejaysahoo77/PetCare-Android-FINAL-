package com.example.petcaresuperapp.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.petcaresuperapp.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class AppointmentTrackingService : Service() {

    private val CHANNEL_ID = "AppointmentTrackingChannel"
    private val NOTIFICATION_ID = 1001
    
    private var firestoreListener: ListenerRegistration? = null
    private val firestore = FirebaseFirestore.getInstance()

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val appointmentId = intent?.getStringExtra("APPOINTMENT_ID")
        
        // Start foreground immediately with "Pending" status
        val initialNotification = createNotification("Starting tracking...", "Your appointment is being processed.")
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, initialNotification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            startForeground(NOTIFICATION_ID, initialNotification)
        }

        if (appointmentId != null) {
            trackStatus(appointmentId)
        } else {
            stopSelf()
        }

        return START_STICKY
    }

    private fun trackStatus(appointmentId: String) {
        firestoreListener?.remove() // Remove previous listener if any

        firestoreListener = firestore.collection("vet_appointments")
            .document(appointmentId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    updateNotification("Error tracking appointment", "Could not fetch status.", null, 0, 0)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val status = snapshot.getString("status") ?: "pending"
                    val vetName = snapshot.getString("vetName") ?: "Vet"
                    val petName = snapshot.getString("petName") ?: "your pet"
                    val timestamp = snapshot.getLong("appointmentTimestamp") ?: 0L
                    
                    val timeString = if (timestamp > 0L) {
                        try {
                            val sdf = java.text.SimpleDateFormat("dd MMM yyyy 'at' hh:mm a", java.util.Locale.getDefault())
                            sdf.format(java.util.Date(timestamp))
                        } catch (e: Exception) {
                            "Scheduled Time"
                        }
                    } else {
                        "Scheduled Time"
                    }

                    val title = "Appointment for $petName"
                    
                    var progress = 0
                    val maxProgress = 3
                    
                    val statusMessage = when (status.lowercase()) {
                        "pending" -> {
                            progress = 1
                            "Waiting for $vetName to confirm."
                        }
                        "confirmed" -> {
                            progress = 2
                            "$vetName confirmed your appointment!"
                        }
                        "completed" -> {
                            progress = 3
                            "Appointment finished. Thanks!"
                        }
                        "cancelled" -> {
                            progress = 0
                            "Appointment was cancelled."
                        }
                        else -> {
                            progress = 1
                            "Status: ${status.uppercase()}"
                        }
                    }
                    
                    val content = "Status: ${status.uppercase()}"
                    val detailedText = "$statusMessage\nTime: $timeString"
                    
                    updateNotification(title, content, detailedText, progress, maxProgress)
                    
                    // Automatically stop the service if the appointment is completed or cancelled
                    if (status.lowercase() == "completed" || status.lowercase() == "cancelled") {
                        stopSelf()
                    }
                }
            }
    }

    private fun updateNotification(title: String, content: String, detailedText: String?, progress: Int, maxProgress: Int) {
        val notification = createNotification(title, content, detailedText, progress, maxProgress)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotification(title: String, content: String, detailedText: String? = null, progress: Int = 0, maxProgress: Int = 0): Notification {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOnlyAlertOnce(true) // Prevent vibrating on every status update
            .setOngoing(true)
            
        if (maxProgress > 0) {
            builder.setProgress(maxProgress, progress, false)
        }
            
        if (detailedText != null) {
            builder.setStyle(NotificationCompat.BigTextStyle().bigText(detailedText))
        }
            
        return builder.build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Appointment Tracking",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        firestoreListener?.remove()
    }
}
