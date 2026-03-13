package com.example.petcaresuperapp.presentation.screens.vet

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelemedicineScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Telemedicine", fontWeight = FontWeight.Bold) },
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
                TelemedicineHero()
            }
            item {
                Text("Available Online Vets", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            items(onlineVets) { vet ->
                OnlineVetCard(vet)
            }
            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Composable
fun TelemedicineHero() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = SecondaryColor
    ) {
        Row(
            modifier = Modifier
                .background(SecondaryGradient)
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Video Consultation", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                Text("Connect with expert vets instantly from your home.", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { /* TODO */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Start Now", color = SecondaryColor, fontWeight = FontWeight.Bold)
                }
            }
            Icon(Icons.Default.VideoCall, contentDescription = null, tint = Color.White, modifier = Modifier.size(60.dp))
        }
    }
}

@Composable
fun OnlineVetCard(vet: OnlineVet) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = SurfaceColor,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(PrimaryGradient),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(14.dp)
                        .background(SuccessGradStart, CircleShape)
                        .padding(2.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(vet.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(vet.specialty, fontSize = 12.sp, color = TextSecondary)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(14.dp))
                    Text(" ${vet.rating} • ${vet.reviews} reviews", fontSize = 12.sp, color = TextSecondary)
                }
            }
            IconButton(onClick = { /* TODO */ }) {
                Icon(Icons.Default.Chat, contentDescription = null, tint = PrimaryColor)
            }
            IconButton(onClick = { /* TODO */ }) {
                Icon(Icons.Default.Videocam, contentDescription = null, tint = SecondaryColor)
            }
        }
    }
}

data class OnlineVet(
    val name: String,
    val specialty: String,
    val rating: String,
    val reviews: String
)

val onlineVets = listOf(
    OnlineVet("Dr. Emily Chen", "General Practitioner", "4.9", "128"),
    OnlineVet("Dr. Mark Wilson", "Pet Nutritionist", "4.8", "95"),
    OnlineVet("Dr. Sarah Adams", "Behaviorist", "4.7", "210")
)
