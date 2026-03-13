package com.example.petcaresuperapp.presentation.screens.gamification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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

data class Achievement(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val isUnlocked: Boolean,
    val progress: Float = 1f
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(navController: NavController) {
    val achievements = listOf(
        Achievement("Responsible Parent", Icons.Default.Verified, Primary2026, true),
        Achievement("Early Bird", Icons.Default.WbSunny, Color(0xFFFFB300), true),
        Achievement("Health Expert", Icons.Default.MedicalServices, PrimaryColor, true),
        Achievement("Social Butterfly", Icons.Default.Groups, SecondaryColor, false, 0.6f),
        Achievement("Daily Walker", Icons.Default.DirectionsWalk, Color(0xFF4CAF50), false, 0.4f),
        Achievement("Life Saver", Icons.Default.VolunteerActivism, Accent2026, false, 0.1f)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pet Care Achievements", fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Stats Header
            Surface(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                shape = RoundedCornerShape(28.dp),
                color = PrimaryColor
            ) {
                Row(
                    modifier = Modifier.background(PrimaryGradient).padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Achievement Level 5", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                        Text("Master Caretaker", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(50.dp))
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(achievements) { achievement ->
                    AchievementCard(achievement)
                }
            }
        }
    }
}

@Composable
fun AchievementCard(achievement: Achievement) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = if (achievement.isUnlocked) Color.White else Color.White.copy(alpha = 0.5f),
        shadowElevation = if (achievement.isUnlocked) 2.dp else 0.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(if (achievement.isUnlocked) achievement.color.copy(alpha = 0.1f) else Color.LightGray.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    achievement.icon,
                    contentDescription = null,
                    tint = if (achievement.isUnlocked) achievement.color else Color.Gray,
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                achievement.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = if (achievement.isUnlocked) TextPrimary else Color.Gray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { achievement.progress },
                modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                color = achievement.color,
                trackColor = achievement.color.copy(alpha = 0.1f)
            )
        }
    }
}
