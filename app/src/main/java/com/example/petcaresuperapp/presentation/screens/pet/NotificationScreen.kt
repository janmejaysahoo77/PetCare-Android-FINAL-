package com.example.petcaresuperapp.presentation.screens.pet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.petcaresuperapp.ui.theme.*

data class AppNotification(
    val title: String,
    val time: String,
    val date: String,
    val type: String,
    val icon: ImageVector,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(navController: NavController) {
    val notifications = listOf(
        AppNotification("Heartworm Pill Reminder", "08:00 AM", "Today", "Health", Icons.Default.Medication, AccentColor),
        AppNotification("Evening Walk", "06:30 PM", "Daily", "Activity", Icons.Default.DirectionsWalk, SuccessGradStart),
        AppNotification("Rabies Vaccine Appointment", "10:00 AM", "15 Oct 2024", "Health", Icons.Default.Vaccines, PrimaryColor),
        AppNotification("Deworming Reminder", "09:00 AM", "20 Oct 2024", "Health", Icons.Default.Medication, SecondaryColor)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundColor)
            )
        },
        containerColor = BackgroundColor,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO */ },
                containerColor = PrimaryColor,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Notification")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
        ) {
            item {
                NotificationHeader()
            }
            item {
                Text(
                    "Recent Updates",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            items(notifications) { notification ->
                NotificationTile(notification)
            }
        }
    }
}

@Composable
fun NotificationHeader() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(PrimaryColor.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = PrimaryColor)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("All Caught Up!", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                Text("You have 4 active notifications.", fontSize = 12.sp, color = TextSecondary)
            }
        }
    }
}

@Composable
fun NotificationTile(notification: AppNotification) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(notification.color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(notification.icon, contentDescription = null, tint = notification.color)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(notification.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                Text("${notification.date} • ${notification.time}", fontSize = 12.sp, color = TextSecondary)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextGrey)
        }
    }
}
