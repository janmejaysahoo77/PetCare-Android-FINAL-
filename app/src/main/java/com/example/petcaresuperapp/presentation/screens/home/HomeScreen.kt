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
                    text = "Welcome back to PetCare",
                    style = Typography.bodyMedium,
                    color = TextGray
                )
            }
        }

        // Pet Profile Card
        item {
            AnimatedVisibility(
                visible = !uiState.isLoading,
                enter = fadeIn() + slideInVertically()
            ) {
                if (uiState.hasPet) {
                    PremiumPetCard(
                        petName = uiState.petName,
                        petDescription = uiState.petDescription,
                        petImageUrl = uiState.petImageUrl
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
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Rounded.Favorite,
                        label = "Heart Rate",
                        value = "85 bpm",
                        color = Color(0xFFEF4444),
                        delay = 0
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Rounded.Timer,
                        label = "Activity",
                        value = "45 min",
                        color = Primary2026,
                        delay = 100
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Rounded.Scale,
                        label = "Weight",
                        value = "12.5 kg",
                        color = Color(0xFF3B82F6),
                        delay = 200
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
fun PremiumPetCard(
    petName: String,
    petDescription: String,
    petImageUrl: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .animateContentSize(),
        shape = RoundedCornerShape(28.dp),
        color = SurfaceDark,
        tonalElevation = 8.dp
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = petImageUrl.ifEmpty { "https://images.unsplash.com/photo-1543466835-00a7907e9de1" },
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                            startY = 300f
                        )
                    )
            )
            
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(24.dp)
            ) {
                Text(
                    text = petName,
                    style = Typography.headlineLarge,
                    color = Color.White
                )
                Text(
                    text = petDescription.ifEmpty { "A very happy pet!" },
                    style = Typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                color = Primary2026,
                shape = CircleShape
            ) {
                Icon(
                    Icons.Rounded.Pets,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(8.dp).size(20.dp)
                )
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String,
    color: Color,
    delay: Int
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delay.toLong())
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + scaleIn(initialScale = 0.8f),
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier
                .height(130.dp)
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.White.copy(alpha = 0.1f), Color.Transparent)
                    ),
                    shape = RoundedCornerShape(24.dp)
                ),
            color = SurfaceDark.copy(alpha = 0.6f),
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(color.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                }
                Column {
                    Text(value, style = Typography.titleLarge, color = TextWhite)
                    Text(label, style = Typography.labelMedium, color = TextGray)
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
            .size(100.dp, 120.dp),
        shape = RoundedCornerShape(24.dp),
        color = SurfaceDark,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = title, tint = color, modifier = Modifier.size(32.dp))
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
            .height(140.dp)
            .clickable { onClickAddPet() },
        shape = RoundedCornerShape(28.dp),
        color = SurfaceDark.copy(alpha = 0.3f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Primary2026.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = Primary2026, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Add your first pet",
                style = Typography.titleMedium,
                color = TextWhite
            )
        }
    }
}
