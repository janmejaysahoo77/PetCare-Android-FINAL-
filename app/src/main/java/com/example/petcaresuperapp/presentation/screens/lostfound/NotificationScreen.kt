package com.example.petcaresuperapp.presentation.screens.lostfound

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.petcaresuperapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    navController: NavController,
    viewModel: ContactRequestViewModel = hiltViewModel()
) {
    val requests by viewModel.incomingRequests.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.observeIncomingRequests()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contact Requests", fontWeight = FontWeight.Bold, color = TextWhite) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        },
        containerColor = BackgroundDark
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (requests.isEmpty()) {
                item {
                    Text(
                        "No pending contact requests",
                        modifier = Modifier.padding(16.dp),
                        color = TextGray
                    )
                }
            } else {
                items(requests) { request ->
                    ContactRequestCard(
                        request = request,
                        onAccept = { viewModel.updateRequestStatus(request.requestId, "accepted") },
                        onReject = { viewModel.updateRequestStatus(request.requestId, "rejected") }
                    )
                }
            }
        }
    }
}
