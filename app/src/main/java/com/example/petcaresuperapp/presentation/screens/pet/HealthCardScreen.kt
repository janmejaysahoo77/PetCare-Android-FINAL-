package com.example.petcaresuperapp.presentation.screens.pet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.petcaresuperapp.ui.theme.*

data class Vaccination(
    val name: String,
    val date: String,
    val vet: String,
    val status: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthCardScreen(navController: NavController) {
    val vaccinations = listOf(
        Vaccination("Rabies", "15 May 2024", "Dr. Smith", "Completed", Icons.Default.Shield),
        Vaccination("Distemper", "20 Jun 2024", "Dr. Sarah", "Completed", Icons.Default.Vaccines),
        Vaccination("Parvovirus", "10 Oct 2024", "Dr. Mike", "Scheduled", Icons.Default.Timer)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Digital Health Card", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                QRSection()
            }
            item {
                Text(
                    "Vaccination History",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            items(vaccinations) { vaccine ->
                VaccineCard(vaccine)
            }
            item {
                HealthSummary()
            }
            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Composable
fun QRSection() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = SurfaceColor,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.QrCode2,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    tint = PrimaryColor
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Buddy's Health ID", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(
                "Scan this QR code for quick vet access",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun VaccineCard(vaccine: Vaccination) {
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
                    .background(
                        if (vaccine.status == "Completed") SuccessGradEnd.copy(alpha = 0.1f) 
                        else SecondaryGradStart.copy(alpha = 0.1f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    vaccine.icon, 
                    contentDescription = null, 
                    tint = if (vaccine.status == "Completed") SuccessGradStart else SecondaryColor
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(vaccine.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("${vaccine.date} • ${vaccine.vet}", fontSize = 12.sp, color = TextSecondary)
            }
            Badge(
                containerColor = if (vaccine.status == "Completed") SuccessGradStart.copy(alpha = 0.1f) 
                                 else AccentGradStart.copy(alpha = 0.1f)
            ) {
                Text(
                    vaccine.status,
                    color = if (vaccine.status == "Completed") SuccessGradStart else AccentGradStart,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun HealthSummary() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = PrimaryColor,
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Info, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                "Health Summary: Buddy is in perfect health. No pending alerts.",
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}
