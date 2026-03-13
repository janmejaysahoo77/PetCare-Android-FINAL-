package com.example.petcaresuperapp.presentation.screens.vet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.petcaresuperapp.ui.theme.*

data class VetClinic(
    val name: String,
    val address: String,
    val rating: String,
    val distance: String,
    val available: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VetSearchScreen(navController: NavController) {
    val clinics = listOf(
        VetClinic("Happy Paws Clinic", "123 Main St, New York", "4.8", "1.2 km", true),
        VetClinic("City Vet Center", "456 Park Ave, New York", "4.5", "2.5 km", true),
        VetClinic("Pet Care Hospital", "789 Broadway, New York", "4.2", "3.0 km", false),
        VetClinic("Emergency Vet 24/7", "101 5th Ave, New York", "4.9", "0.5 km", true)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Find a Vet", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
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
            SearchBar(
                modifier = Modifier.padding(vertical = 16.dp),
                placeholder = "Search clinics, doctors..."
            )

            Text(
                "Nearby Clinics",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(clinics) { clinic ->
                    VetClinicCard(clinic)
                }
            }
        }
    }
}

@Composable
fun SearchBar(modifier: Modifier = Modifier, placeholder: String) {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, color = TextSecondary) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextGrey) },
        trailingIcon = { Icon(Icons.Default.FilterList, contentDescription = null, tint = PrimaryColor) },
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = SurfaceColor,
            focusedContainerColor = SurfaceColor,
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = PrimaryColor
        )
    )
}

@Composable
fun VetClinicCard(clinic: VetClinic) {
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
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PrimaryGradient),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.LocalHospital, contentDescription = null, tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(clinic.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(clinic.address, fontSize = 12.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(14.dp))
                    Text(" ${clinic.rating} • ${clinic.distance}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                if (clinic.available) {
                    Text("Available", color = SuccessGradStart, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                } else {
                    Text("Closed", color = AccentGradStart, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { /* TODO */ },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Book", fontSize = 12.sp)
                }
            }
        }
    }
}
