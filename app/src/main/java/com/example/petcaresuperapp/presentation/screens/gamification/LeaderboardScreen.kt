package com.example.petcaresuperapp.presentation.screens.gamification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Pets
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

data class LeaderboardUser(
    val name: String,
    val points: Int,
    val rank: Int,
    val isCurrentUser: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(navController: NavController) {
    val topUsers = listOf(
        LeaderboardUser("Alex Rivera", 2450, 1),
        LeaderboardUser("Sarah Wilson", 2310, 2),
        LeaderboardUser("David Miller", 2150, 3),
        LeaderboardUser("Emma Stone", 1980, 4),
        LeaderboardUser("John Doe", 1850, 5, true),
        LeaderboardUser("Lisa Ray", 1720, 6),
        LeaderboardUser("Mike Ross", 1600, 7)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Leaderboard", fontWeight = FontWeight.Bold) },
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
            // Top 3 Podium Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(PrimaryGradient)
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    PodiumItem(topUsers[1], 100.dp, Color(0xFFC0C0C0)) // Silver
                    PodiumItem(topUsers[0], 130.dp, Color(0xFFFFD700)) // Gold
                    PodiumItem(topUsers[2], 90.dp, Color(0xFFCD7F32))  // Bronze
                }
            }

            // List of other users
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        "Monthly Rankings",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                itemsIndexed(topUsers.drop(3)) { index, user ->
                    LeaderboardRow(user)
                }
            }
        }
    }
}

@Composable
fun PodiumItem(user: LeaderboardUser, height: androidx.compose.ui.unit.Dp, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Pets, contentDescription = null, tint = Color.White)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(user.name.split(" ")[0], color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
            modifier = Modifier
                .width(80.dp)
                .height(height),
            color = color.copy(alpha = 0.9f),
            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("#${user.rank}", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
                Text("${user.points} pts", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun LeaderboardRow(user: LeaderboardUser) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = if (user.isCurrentUser) PrimaryColor.copy(alpha = 0.1f) else Color.White,
        border = if (user.isCurrentUser) androidx.compose.foundation.BorderStroke(1.dp, PrimaryColor) else null,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = user.rank.toString(),
                fontWeight = FontWeight.Bold,
                color = if (user.isCurrentUser) PrimaryColor else TextSecondary,
                modifier = Modifier.width(30.dp)
            )
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(brush = GlassGradient, alpha = 0.1f),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Pets, contentDescription = null, tint = SecondaryColor, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = user.name,
                fontWeight = if (user.isCurrentUser) FontWeight.Bold else FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${user.points} pts",
                fontWeight = FontWeight.Bold,
                color = PrimaryColor
            )
        }
    }
}
