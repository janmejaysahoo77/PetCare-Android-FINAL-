package com.example.petcaresuperapp.presentation.screens.community

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

data class LostPetPost(
    val petName: String,
    val breed: String,
    val location: String,
    val type: String, // "LOST" or "FOUND"
    val reward: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LostFoundScreen(navController: NavController) {
    val alerts = listOf(
        LostPetPost("Oliver", "Ginger Cat", "Central Park, NY", "LOST", "$200"),
        LostPetPost("Unknown", "Husky Mix", "Queens, NY", "FOUND"),
        LostPetPost("Daisy", "Beagle", "Brooklyn, NY", "LOST", "$500")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lost & Found", fontWeight = FontWeight.Bold) },
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
            ExtendedFloatingActionButton(
                onClick = { /* TODO */ },
                containerColor = AccentColor,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Report Pet") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Text(
                "Active Alerts Nearby",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(alerts) { alert ->
                    LostPetCard(alert)
                }
            }
        }
    }
}

@Composable
fun LostPetCard(alert: LostPetPost) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = SurfaceColor,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        if (alert.type == "LOST") AccentColor.copy(alpha = 0.1f) else SuccessGradStart.copy(alpha = 0.1f),
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Pets,
                    contentDescription = null,
                    tint = if (alert.type == "LOST") AccentColor else SuccessGradStart,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        alert.type,
                        color = if (alert.type == "LOST") AccentColor else SuccessGradStart,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp
                    )
                    if (alert.reward != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("REWARD: ${alert.reward}", color = Color(0xFFFFB300), fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }
                }
                Text(alert.petName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(alert.breed, fontSize = 12.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = SecondaryColor, modifier = Modifier.size(12.dp))
                    Text(" ${alert.location}", fontSize = 11.sp, color = TextSecondary)
                }
            }
        }
    }
}
