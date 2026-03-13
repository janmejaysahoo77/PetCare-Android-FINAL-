package com.example.petcaresuperapp.presentation.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.petcaresuperapp.presentation.navigation.Screen
import com.example.petcaresuperapp.presentation.components.*
import com.example.petcaresuperapp.ui.theme.*

@Composable
fun HomeScreen(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(paddingValues),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Top Section: Greeting & Profile
        item {
            Column {
                Text(
                    text = if (uiState.isLoading) "Hello!" else "Hello, ${uiState.userName}!",
                    style = Typography.displayLarge,
                    color = TextWhite
                )
                Text(
                    text = "Your Pet's Wellness Dashboard",
                    style = Typography.bodyMedium,
                    color = TextGray
                )
            }
        }

        // Pet Profile Card
        item {
            AnimatedVisibility(
                visible = !uiState.isLoading,
                enter = fadeIn() + slideInVertically(initialOffsetY = { 40 })
            ) {
                if (uiState.hasPet) {
                    PetCard(
                        petName = uiState.petName,
                        petDescription = uiState.petDescription,
                        petImageUrl = uiState.petImageUrl,
                        onClick = { navController.navigate(Screen.PetProfile.route) }
                    )
                } else {
                    EmptyPetCard(
                        onClickAddPet = { navController.navigate(Screen.AddPet.route) }
                    )
                }
            }
        }

        // Health Stats Section
        item {
            Column {
                Text(
                    "Health Statistics",
                    style = Typography.titleLarge,
                    color = TextWhite,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Rounded.Favorite,
                        label = "Heart Rate",
                        value = "85 bpm",
                        color = Color(0xFFEF4444),
                        delay = 100
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Rounded.Timer,
                        label = "Activity",
                        value = "45 min",
                        color = Primary2026,
                        delay = 200
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Rounded.Scale,
                        label = "Weight",
                        value = "12.5 kg",
                        color = Color(0xFF3B82F6),
                        delay = 300
                    )
                }
            }
        }

        // Quick Actions
        item {
            Text(
                "Quick Actions",
                style = Typography.titleLarge,
                color = TextWhite,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    ActionCard("Vets", Icons.Rounded.LocalHospital, Primary2026) {
                        navController.navigate(Screen.VetSearch.route)
                    }
                }
                item {
                    ActionCard("Store", Icons.Rounded.Store, Info2026) {
                        navController.navigate(Screen.Marketplace.route)
                    }
                }
                item {
                    ActionCard("Forum", Icons.Rounded.Forum, Secondary2026) {
                        navController.navigate(Screen.RescueForum.route)
                    }
                }
            }
        }
    }
}

@Composable
fun ActionCard(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .size(110.dp, 130.dp),
        shape = RoundedCornerShape(24.dp),
        color = SurfaceDark,
        tonalElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = color, modifier = Modifier.size(26.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, style = Typography.labelLarge, color = TextWhite)
        }
    }
}

@Composable
fun EmptyPetCard(onClickAddPet: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable { onClickAddPet() },
        shape = RoundedCornerShape(RoundedCornersLarge),
        color = SurfaceDark.copy(alpha = 0.3f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Primary2026.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = Primary2026, modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Add your first pet",
                style = Typography.titleMedium,
                color = TextWhite
            )
        }
    }
}
