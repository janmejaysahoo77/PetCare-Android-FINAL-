package com.example.petcaresuperapp.presentation.screens.vet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.petcaresuperapp.ui.theme.*

data class UserAppointment(
    val clinicName: String,
    val date: String,
    val time: String,
    val status: String, // "Upcoming", "Completed", "Cancelled"
    val type: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppointmentsScreen(navController: NavController) {
    val appointments = listOf(
        UserAppointment("Happy Paws Clinic", "15 Oct 2024", "10:00 AM", "Upcoming", "Check-up"),
        UserAppointment("City Vet Center", "10 Oct 2024", "02:30 PM", "Completed", "Vaccination"),
        UserAppointment("Pet Care Hospital", "05 Oct 2024", "09:00 AM", "Cancelled", "Surgery")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Appointments", fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
        ) {
            items(appointments) { appt ->
                UserAppointmentCard(appt)
            }
        }
    }
}

@Composable
fun UserAppointmentCard(appt: UserAppointment) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = SurfaceColor,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Badge(
                    containerColor = when(appt.status) {
                        "Upcoming" -> PrimaryColor.copy(alpha = 0.1f)
                        "Completed" -> SuccessGradStart.copy(alpha = 0.1f)
                        else -> AccentColor.copy(alpha = 0.1f)
                    }
                ) {
                    Text(
                        appt.status,
                        color = when(appt.status) {
                            "Upcoming" -> PrimaryColor
                            "Completed" -> SuccessGradStart
                            else -> AccentColor
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(appt.type, fontSize = 12.sp, color = TextSecondary)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(brush = SecondaryGradient, shape = RoundedCornerShape(12.dp), alpha = 0.1f),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.LocalHospital, contentDescription = null, tint = SecondaryColor)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(appt.clinicName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("${appt.date} • ${appt.time}", fontSize = 13.sp, color = TextSecondary)
                }
            }
            
            if (appt.status == "Upcoming") {
                Spacer(modifier = Modifier.height(20.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = { /* TODO: Cancel */ },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentColor)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = { /* TODO: Reschedule */ },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                    ) {
                        Text("Reschedule")
                    }
                }
            }
        }
    }
}
