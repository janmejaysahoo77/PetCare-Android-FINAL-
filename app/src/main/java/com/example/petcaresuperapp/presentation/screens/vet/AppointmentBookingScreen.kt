package com.example.petcaresuperapp.presentation.screens.vet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.petcaresuperapp.presentation.components.GradientButton
import com.example.petcaresuperapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentBookingScreen(navController: NavController, vetId: String?) {
    var selectedDate by remember { mutableStateOf("Oct 15") }
    var selectedTime by remember { mutableStateOf("10:00 AM") }
    var selectedPet by remember { mutableStateOf("Buddy") }

    val dates = listOf("Oct 14", "Oct 15", "Oct 16", "Oct 17", "Oct 18")
    val times = listOf("09:00 AM", "10:00 AM", "11:00 AM", "02:00 PM", "03:00 PM", "04:00 PM")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book Appointment", fontWeight = FontWeight.Bold) },
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
            Text("Select Pet", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(12.dp))
            PetSelector(selectedPet) { selectedPet = it }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Select Date", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(dates) { date ->
                    DateChip(date, date == selectedDate) { selectedDate = date }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Select Time Slot", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(12.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                times.forEach { time ->
                    TimeSlotChip(time, time == selectedTime) { selectedTime = time }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Reason for Visit", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Describe the issue (optional)") },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            GradientButton(
                text = "Confirm Booking",
                onClick = { 
                    // Show confirmation or navigate back
                    navController.popBackStack()
                },
                gradient = PrimaryGradient
            )
        }
    }
}

@Composable
fun PetSelector(selectedPet: String, onSelect: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        PetChip("Buddy", selectedPet == "Buddy", onSelect)
        PetChip("Luna", selectedPet == "Luna", onSelect)
    }
}

@Composable
fun PetChip(name: String, isSelected: Boolean, onSelect: (String) -> Unit) {
    Surface(
        modifier = Modifier
            .width(100.dp)
            .height(50.dp)
            .clickable { onSelect(name) },
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) PrimaryColor else SurfaceColor,
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, DividerColor)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(name, color = if (isSelected) Color.White else TextSecondary, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun DateChip(date: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .width(80.dp)
            .height(90.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) SecondaryColor else SurfaceColor,
        shadowElevation = if (isSelected) 4.dp else 1.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(date.split(" ")[0], fontSize = 12.sp, color = if (isSelected) Color.White.copy(alpha = 0.8f) else TextSecondary)
            Text(date.split(" ")[1], fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = if (isSelected) Color.White else TextPrimary)
        }
    }
}

@Composable
fun TimeSlotChip(time: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .padding(bottom = 12.dp)
            .width(100.dp)
            .height(45.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) PrimaryColor.copy(alpha = 0.1f) else Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) PrimaryColor else DividerColor)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(time, fontSize = 13.sp, color = if (isSelected) PrimaryColor else TextSecondary, fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        content = { content() }
    )
}
