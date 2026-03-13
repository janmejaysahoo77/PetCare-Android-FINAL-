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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.petcaresuperapp.ui.theme.*

data class AdoptionRequest(
    val petName: String,
    val applicant: String,
    val date: String,
    val status: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelterDashboardScreen(navController: NavController) {
    val requests = listOf(
        AdoptionRequest("Luna", "Alice Green", "12 Oct", "Pending"),
        AdoptionRequest("Max", "Bob Smith", "11 Oct", "Reviewing"),
        AdoptionRequest("Charlie", "David Ross", "10 Oct", "Approved")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shelter Management", fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                ShelterStats()
            }
            item {
                Text("Adoption Requests", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            items(requests) { request ->
                AdoptionRequestCard(request)
            }
            item {
                GradientButton_Custom(
                    text = "Add New Pet for Adoption",
                    onClick = { /* TODO */ },
                    gradient = PremiumGradient
                )
            }
            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Composable
fun ShelterStats() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Active Pets",
            value = "45",
            icon = Icons.Default.Pets,
            color = PrimaryColor
        )
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Adopted",
            value = "128",
            icon = Icons.Default.CheckCircle,
            color = Primary2026
        )
    }
}

@Composable
fun StatCard(modifier: Modifier, title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = SurfaceColor,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
            Text(title, fontSize = 12.sp, color = TextSecondary)
        }
    }
}

@Composable
fun AdoptionRequestCard(request: AdoptionRequest) {
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
                    .background(PrimaryColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryColor)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(request.applicant, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("Wants to adopt ${request.petName}", fontSize = 12.sp, color = TextSecondary)
            }
            Badge(
                containerColor = when(request.status) {
                    "Approved" -> Primary2026.copy(alpha = 0.1f)
                    "Pending" -> Accent2026.copy(alpha = 0.1f)
                    else -> Secondary2026.copy(alpha = 0.1f)
                }
            ) {
                Text(
                    request.status,
                    color = when(request.status) {
                        "Approved" -> Primary2026
                        "Pending" -> Accent2026
                        else -> Secondary2026
                    },
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun GradientButton_Custom(text: String, onClick: () -> Unit, gradient: androidx.compose.ui.graphics.Brush) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp)),
        contentPadding = PaddingValues(),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient),
            contentAlignment = Alignment.Center
        ) {
            Text(text, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}
