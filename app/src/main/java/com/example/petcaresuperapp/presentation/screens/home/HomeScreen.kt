package com.example.petcaresuperapp.presentation.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.petcaresuperapp.presentation.navigation.Screen
import com.example.petcaresuperapp.ui.theme.*

data class HomeFeature(
    val title: String,
    val icon: ImageVector,
    val colors: List<Color>,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val features = listOf(
        HomeFeature("Pet Profile", Icons.Rounded.Pets, PrimaryGradientColors, Screen.PetProfile.route),
        HomeFeature("Health Card", Icons.Rounded.HealthAndSafety, SecondaryGradientColors, Screen.HealthCard.route),
        HomeFeature("Activity", Icons.Rounded.DirectionsRun, PrimaryGradientColors, Screen.ActivityTracker.route),
        HomeFeature("Reminders", Icons.Rounded.NotificationsActive, AccentGradientColors, Screen.Reminders.route),
        HomeFeature("Find a Vet", Icons.Rounded.LocalHospital, SecondaryGradientColors, Screen.VetSearch.route),
        HomeFeature("AI Assistant", Icons.Rounded.AutoAwesome, PrimaryGradientColors, Screen.AiAssistant.route),
        HomeFeature("Community", Icons.Rounded.Groups, SecondaryGradientColors, Screen.Community.route),
        HomeFeature("Emergency", Icons.Rounded.Warning, AccentGradientColors, Screen.EmergencySos.route)
    )

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(
                            "PetCare",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            "Good morning, Alex!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { navController.navigate(Screen.Chat.route) },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                    ) {
                        Icon(Icons.Rounded.ChatBubbleOutline, contentDescription = null)
                    }
                    IconButton(
                        onClick = { navController.navigate(Screen.UserProfile.route) },
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Icon(Icons.Rounded.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            // Live Gradient Status Card
            LiveGradientCard(
                title = "Daily Goal",
                subtitle = "Buddy has completed 70% of daily activity",
                progress = 0.7f,
                colors = PrimaryGradientColors
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Our Services",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                items(features) { feature ->
                    AdvancedFeatureCard(
                        feature = feature,
                        onClick = { navController.navigate(feature.route) }
                    )
                }
            }
        }
    }
}

@Composable
fun LiveGradientCard(
    title: String,
    subtitle: String,
    progress: Float,
    colors: List<Color>
) {
    val infiniteTransition = rememberInfiniteTransition(label = "gradient")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        shape = RoundedCornerShape(28.dp),
        tonalElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = colors,
                            start = Offset(offset, offset),
                            end = Offset(offset + 500f, offset + 500f)
                        )
                    )
                }
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.align(Alignment.TopStart)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(subtitle, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
            }
            
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.CenterEnd),
                color = Color.White,
                strokeWidth = 6.dp,
                trackColor = Color.White.copy(alpha = 0.2f)
            )
            
            Text(
                "${(progress * 100).toInt()}%",
                modifier = Modifier.align(Alignment.CenterEnd).padding(end = 18.dp),
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun AdvancedFeatureCard(
    feature: HomeFeature,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        Brush.linearGradient(feature.colors),
                        RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(feature.icon, contentDescription = null, tint = Color.White)
            }
            
            Column {
                Text(
                    feature.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Explore",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        Icons.Rounded.ChevronRight,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
