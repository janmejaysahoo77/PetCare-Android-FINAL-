package com.example.petcaresuperapp.presentation.screens.auth

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.petcaresuperapp.R
import com.example.petcaresuperapp.core.util.Resource
import com.example.petcaresuperapp.ui.theme.*

@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val signUpState by viewModel.signUpState
    
    LaunchedEffect(signUpState) {
        if (signUpState is Resource.Success) {
            onSignUpSuccess()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.onboaring2),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.4f),
                            Color.Black.copy(alpha = 0.8f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            
            // Header
            Text(
                text = "Join Us",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            
            Text(
                text = "Create an account for your furry friend",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // SignUp Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                tonalElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    var name by remember { mutableStateOf("") }
                    var email by remember { mutableStateOf("") }
                    var password by remember { mutableStateOf("") }
                    var passwordVisible by remember { mutableStateOf(false) }

                    AdvancedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = "Full Name",
                        icon = Icons.Rounded.Person
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AdvancedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email Address",
                        icon = Icons.Rounded.Email
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AdvancedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password",
                        icon = Icons.Rounded.Lock,
                        isPassword = true,
                        passwordVisible = passwordVisible,
                        onPasswordToggle = { passwordVisible = !passwordVisible }
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = { viewModel.signUp(name, email, password) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        if (signUpState is Resource.Loading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Create Account", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }

                    if (signUpState is Resource.Error) {
                        Text(
                            text = (signUpState as Resource.Error).message ?: "An error occurred",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Already have an account? ", color = Color.White.copy(alpha = 0.8f))
                Text(
                    "Sign In",
                    modifier = Modifier.clickable { onNavigateToLogin() },
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
