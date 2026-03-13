package com.example.petcaresuperapp.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.petcaresuperapp.data.model.AdoptionRequest
import com.example.petcaresuperapp.presentation.viewmodel.AdoptionViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAdoptionRequestsScreen(
    onBackClick: () -> Unit,
    viewModel: AdoptionViewModel = hiltViewModel()
) {
    val userRequests by viewModel.userRequests.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUserRequests()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Requests") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (userRequests.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("You have no adoption requests.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(userRequests) { request ->
                    RequestItemCard(request = request)
                }
            }
        }
    }
}

@Composable
fun RequestItemCard(request: AdoptionRequest) {
    val statusColor = when (request.status) {
        "APPROVED" -> Color(0xFF10B981) // Green
        "REJECTED" -> Color(0xFFEF4444) // Red
        else -> Color(0xFFF59E0B) // Orange
    }
    
    val statusBgColor = when (request.status) {
        "APPROVED" -> Color(0xFFD1FAE5)
        "REJECTED" -> Color(0xFFFEE2E2)
        else -> Color(0xFFFEF3C7)
    }
    
    val statusDescription = when (request.status) {
        "APPROVED" -> "Adoption approved \uD83C\uDF89"
        "REJECTED" -> "Request rejected"
        else -> "Waiting for shelter approval"
    }

    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val dateString = request.createdAt?.toDate()?.let { dateFormat.format(it) } ?: "Unknown"

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = request.petName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Surface(
                    color = statusBgColor,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = request.status,
                        color = statusColor,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = statusDescription, style = MaterialTheme.typography.bodySmall, color = statusColor)
            Spacer(modifier = Modifier.height(4.dp))
            if (request.message.isNotBlank()) {
                Text(text = "\"${request.message}\"", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
            }
            Text(text = "Requested on: $dateString", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}
