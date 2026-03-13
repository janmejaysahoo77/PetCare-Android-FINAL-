package com.example.petcaresuperapp.presentation.screens.adoption

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.FavoriteBorder
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

@Immutable
data class AdoptionPet(
    val name: String,
    val breed: String,
    val age: String,
    val location: String,
    val gender: String,
    val isLiked: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdoptionListScreen(navController: NavController) {
    val pets = listOf(
        AdoptionPet("Luna", "Siberian Husky", "2 years", "New York", "Female"),
        AdoptionPet("Max", "Beagle", "1 year", "Brooklyn", "Male", true),
        AdoptionPet("Bella", "Persian Cat", "6 months", "Queens", "Female"),
        AdoptionPet("Charlie", "Golden Retriever", "3 years", "Manhattan", "Male")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Adopt a Friend", fontWeight = FontWeight.Bold) },
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
                .padding(horizontal = 20.dp)
        ) {
            // Category Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CategoryChip("All", true)
                CategoryChip("Dogs", false)
                CategoryChip("Cats", false)
                CategoryChip("Birds", false)
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(pets) { pet ->
                    AdoptionCard(pet)
                }
            }
        }
    }
}

@Composable
fun CategoryChip(label: String, isSelected: Boolean) {
    Surface(
        color = if (isSelected) PrimaryColor else Color.White,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = if (isSelected) 4.dp else 1.dp,
        modifier = Modifier.height(40.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = if (isSelected) Color.White else TextSecondary,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun AdoptionCard(pet: AdoptionPet) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = SurfaceColor,
        shadowElevation = 2.dp
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(SecondaryColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                // Image Placeholder
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(60.dp)
                )
                
                // Like Button
                IconButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(Color.White, CircleShape)
                        .size(32.dp)
                ) {
                    Icon(
                        imageVector = if (pet.isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        tint = AccentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            
            Column(modifier = Modifier.padding(12.dp)) {
                Text(pet.name, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = TextPrimary)
                Text(pet.breed, fontSize = 12.sp, color = TextSecondary)
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = SecondaryColor, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(pet.location, fontSize = 11.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(pet.age, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                }
            }
        }
    }
}

annotation class Immutable
