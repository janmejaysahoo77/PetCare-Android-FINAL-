package com.example.petcaresuperapp.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.petcaresuperapp.presentation.viewmodel.AdoptionViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdoptionRequestScreen(
    petId: String,
    petName: String,
    onBackClick: () -> Unit,
    onSubmitSuccess: () -> Unit,
    viewModel: AdoptionViewModel = hiltViewModel()
) {
    var contactInfo by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    
    val isLoading by viewModel.isLoading.collectAsState()
    val submissionStatus by viewModel.submissionStatus.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(submissionStatus) {
        if (submissionStatus?.isSuccess == true) {
            scope.launch {
                snackbarHostState.showSnackbar("Adoption request sent successfully!")
            }
            viewModel.resetSubmissionStatus()
            onSubmitSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Request Adoption") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Adopt $petName",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = contactInfo,
                onValueChange = { contactInfo = it },
                label = { Text("Contact Number") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Message to Shelter") },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                maxLines = 5
            )

            if (submissionStatus?.isFailure == true) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Failed to submit request: ${submissionStatus?.exceptionOrNull()?.message}",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.submitAdoptionRequest(petId, petName, contactInfo, message) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isLoading && contactInfo.isNotBlank() && message.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Send Request")
                }
            }
        }
    }
}
