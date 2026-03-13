package com.example.petcaresuperapp.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.petcaresuperapp.presentation.viewmodel.PetViewModel
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetDetailsScreen(
    petId: String,
    onBackClick: () -> Unit,
    onAdoptClick: (String, String) -> Unit,
    viewModel: PetViewModel = hiltViewModel()
) {
    val pet by viewModel.selectedPet.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(petId) {
        viewModel.loadPetDetails(petId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pet Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            pet?.let { p ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Box(modifier = Modifier.padding(16.dp).navigationBarsPadding()) {
                        Button(
                            onClick = { onAdoptClick(p.petIdInternal ?: "", p.name) },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            enabled = p.status == "Available",
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (p.status == "Available") "Request Adoption" else "Already Adopted",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (pet != null) {
            val p = pet!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Pet Image from Cloudinary via Coil
                AsyncImage(
                    model = p.imageUrl,
                    contentDescription = p.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text(text = p.name, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                Text(text = p.breed, style = MaterialTheme.typography.titleMedium, color = Color.Gray)

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InfoCard(title = "Age", value = "${p.age} yrs", modifier = Modifier.weight(1f))
                    InfoCard(title = "Gender", value = p.gender, modifier = Modifier.weight(1f))
                    InfoCard(title = "Species", value = p.species, modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "About ${p.name}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = p.description, 
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2
                )
                
                // Extra padding at the bottom so content isn't hidden behind the sticky button
                Spacer(modifier = Modifier.height(100.dp))
            }
        } else {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("Error loading pet details")
            }
        }
    }
}

@Composable
fun InfoCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = title, style = MaterialTheme.typography.labelSmall)
        }
    }
}
