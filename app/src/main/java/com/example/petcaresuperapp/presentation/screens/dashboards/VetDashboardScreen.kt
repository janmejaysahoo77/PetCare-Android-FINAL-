package com.example.petcaresuperapp.presentation.screens.dashboards

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
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.petcaresuperapp.ui.theme.*

@Immutable
data class Appointment(
    val petName: String,
    val owner: String,
    val time: String,
    val type: String,
    val status: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VetDashboardScreen(navController: NavController) {
    val appointments = listOf(
        Appointment("Buddy", "John Doe", "09:00 AM", "Vaccination", "Confirmed"),
        Appointment("Luna", "Sarah Wilson", "10:30 AM", "Check-up", "Pending"),
        Appointment("Max", "Mike Johnson", "02:00 PM", "Surgery", "Confirmed")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Veterinary Dashboard", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundColor)
            )
        },
        containerColor = BackgroundColor
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                VetStatsSection()
            }
            item {
                Text(
                    "Today's Appointments",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            items(appointments) { appointment ->
                AppointmentCard(appointment)
            }
            item {
                EmergencyRequestsSection()
            }
        }
    }
}

@Composable
fun VetStatsSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatBox(
            modifier = Modifier.weight(1f),
            title = "Patients",
            value = "1,240",
            icon = Icons.Default.Pets,
            color = PrimaryColor
        )
        StatBox(
            modifier = Modifier.weight(1f),
            title = "Revenue",
            value = "$4.2k",
            icon = Icons.Default.Payments,
            color = Primary2026
        )
    }
}

@Composable
fun StatBox(modifier: Modifier, title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Surface(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(24.dp),
        color = SurfaceColor,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
            Text(title, fontSize = 12.sp, color = TextSecondary)
        }
    }
}

@Composable
fun AppointmentCard(appt: Appointment) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = SurfaceColor,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(SecondaryColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Schedule, contentDescription = null, tint = SecondaryColor)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(appt.petName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("${appt.time} • ${appt.type}", fontSize = 12.sp, color = TextSecondary)
                Text("Owner: ${appt.owner}", fontSize = 11.sp, color = TextSecondary)
            }
            Badge(
                containerColor = if (appt.status == "Confirmed") Primary2026.copy(alpha = 0.1f) 
                                 else Accent2026.copy(alpha = 0.1f)
            ) {
                Text(
                    appt.status,
                    color = if (appt.status == "Confirmed") Primary2026 else Accent2026,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun EmergencyRequestsSection() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Accent2026.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Accent2026.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = Accent2026)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("Emergency SOS", fontWeight = FontWeight.Bold, color = Accent2026)
                Text("No active emergency requests nearby", fontSize = 12.sp, color = TextSecondary)
            }
        }
    }
}
