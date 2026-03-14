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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class PetCareNotificationService : Service() {

    private val CHANNEL_ID = "PetCareNotificationChannel"
    private val APPOINTMENT_NOTIFICATION_ID = 1001
    private val LOST_FOUND_NOTIFICATION_ID = 1002
    
    private var appointmentListener: ListenerRegistration? = null
    private var contactRequestListener: ListenerRegistration? = null
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startGeneralTracking()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val appointmentId = intent?.getStringExtra("APPOINTMENT_ID")
        
        // Initial service notification
        val initialNotification = createNotification("PetCare Active", "Monitoring your pet's updates.")
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1000, initialNotification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            startForeground(1000, initialNotification)
        }

        if (appointmentId != null) {
            trackAppointmentStatus(appointmentId)
        }

        return START_STICKY
    }

    private fun startGeneralTracking() {
        val currentUser = auth.currentUser ?: return
        
        // Listen for new contact requests (potential spottings)
        contactRequestListener?.remove()
        contactRequestListener = firestore.collection("contact_requests")
            .whereEqualTo("receiverId", currentUser.uid)
            .whereEqualTo("status", "pending")
            .addSnapshotListener { snapshot, _ ->
                snapshot?.documentChanges?.forEach { change ->
                    if (change.type == com.google.firebase.firestore.DocumentChange.Type.ADDED) {
                        val petName = change.document.getString("petName") ?: "your pet"
                        showLostFoundNotification("Pet Spotted!", "Someone might have found $petName! Check notifications.")
                    }
                }
            }
    }

    private fun trackAppointmentStatus(appointmentId: String) {
        appointmentListener?.remove()
        appointmentListener = firestore.collection("vet_appointments")
            .document(appointmentId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    val status = snapshot.getString("status") ?: "pending"
                    val petName = snapshot.getString("petName") ?: "your pet"
                    val title = "Appointment Update: $petName"
                    val content = "Status: ${status.uppercase()}"
                    
                    updateNotification(APPOINTMENT_NOTIFICATION_ID, title, content)
                }
            }
    }

    private fun showLostFoundNotification(title: String, content: String) {
        val notification = createNotification(title, content)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(LOST_FOUND_NOTIFICATION_ID, notification)
    }

    private fun updateNotification(id: Int, title: String, content: String) {
        val notification = createNotification(title, content)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(id, notification)
    }

    private fun createNotification(title: String, content: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "PetCare Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        appointmentListener?.remove()
        contactRequestListener?.remove()
    }
}
