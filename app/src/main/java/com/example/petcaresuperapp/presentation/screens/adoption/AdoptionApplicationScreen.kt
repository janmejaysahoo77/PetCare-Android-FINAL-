package com.example.petcaresuperapp.presentation.screens.adoption

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.petcaresuperapp.presentation.components.GradientButton
import com.example.petcaresuperapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdoptionApplicationScreen(navController: NavController, petName: String) {
    var experience by remember { mutableStateOf("") }
    var homeType by remember { mutableStateOf("") }
    var otherPets by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Adoption Application", fontWeight = FontWeight.Bold) },
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
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(
                "Application for $petName",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PrimaryColor
            )
            Text(
                "Please fill out this form to help the shelter understand your environment.",
                fontSize = 14.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            ApplicationField("Previous Pet Experience", experience, { experience = it }, "Have you owned a pet before?")
            ApplicationField("Home Environment", homeType, { homeType = it }, "Apartment, House with Yard, etc.")
            ApplicationField("Other Pets", otherPets, { otherPets = it }, "Do you have other animals at home?")
            ApplicationField("Reason for Adoption", reason, { reason = it }, "Why do you want to adopt $petName?", isLarge = true)

            Spacer(modifier = Modifier.height(32.dp))

            GradientButton(
                text = "Submit Application",
                onClick = { 
                    // Success logic here
                    navController.popBackStack()
                },
                gradient = PrimaryGradient
            )
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun ApplicationField(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String, isLarge: Boolean = false) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)) {
        Text(label, fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.padding(bottom = 8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, fontSize = 14.sp) },
            modifier = Modifier.fillMaxWidth().then(if (isLarge) Modifier.height(120.dp) else Modifier),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = SurfaceVariantDark,
                focusedBorderColor = PrimaryColor,
                unfocusedContainerColor = SurfaceColor,
                focusedContainerColor = SurfaceColor
            )
        )
    }
}
