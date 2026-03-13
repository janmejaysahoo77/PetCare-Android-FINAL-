package com.example.petcaresuperapp.presentation.screens.discovery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoveryMapScreen(navController: NavController) {
    var selectedCategory by remember { mutableStateOf("Parks") }
    val categories = listOf("Parks", "Stores", "Shelters", "Vets", "Hotels")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pet Discovery Map", fontWeight = FontWeight.Bold) },
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
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Map Placeholder
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFCFD8DC)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(100.dp), tint = Color.White)
                
                // Mock Markers
                MarkerIcon(Modifier.offset(x = (-50).dp, y = (-100).dp), Icons.Default.Park, SuccessGradStart)
                MarkerIcon(Modifier.offset(x = 80.dp, y = (-40).dp), Icons.Default.ShoppingBag, PrimaryColor)
                MarkerIcon(Modifier.offset(x = (-100).dp, y = 60.dp), Icons.Default.LocalHospital, AccentColor)
            }

            // Category Selector
            Column(modifier = Modifier.align(Alignment.TopCenter).padding(top = 16.dp)) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp)
                ) {
                    items(categories) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category) },
                            shape = RoundedCornerShape(16.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryColor,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Bottom Location Card
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(20.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(SuccessGradStart.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Park, contentDescription = null, tint = SuccessGradStart)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Central Bark Park", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("2.4 km away • Open until 9:00 PM", fontSize = 12.sp, color = TextSecondary)
                    }
                    IconButton(onClick = { /* TODO: Navigate */ }) {
                        Icon(Icons.Default.Directions, contentDescription = null, tint = PrimaryColor)
                    }
                }
            }
        }
    }
}

@Composable
fun MarkerIcon(modifier: Modifier, icon: ImageVector, color: Color) {
    Surface(
        modifier = modifier.size(45.dp),
        shape = CircleShape,
        color = color,
        shadowElevation = 4.dp,
        border = androidx.compose.foundation.BorderStroke(2.dp, Color.White)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
        }
    }
}
