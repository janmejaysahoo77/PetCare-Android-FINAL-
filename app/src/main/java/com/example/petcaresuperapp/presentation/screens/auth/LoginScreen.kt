package com.example.petcaresuperapp.presentation.screens.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.petcaresuperapp.R
import com.example.petcaresuperapp.core.util.Resource
import com.example.petcaresuperapp.ui.theme.*

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val loginState by viewModel.loginState
    
    LaunchedEffect(loginState) {
        if (loginState is Resource.Success) {
            onLoginSuccess()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.onboaring1),
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
                            Color.Black.copy(alpha = 0.3f),
                            Color.Black.copy(alpha = 0.7f),
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            
            // Logo
            Surface(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                tonalElevation = 8.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Rounded.Pets,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Welcome Back",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            
            Text(
                text = "Sign in to continue your pet's journey",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Login Card
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
                    var email by remember { mutableStateOf("") }
                    var password by remember { mutableStateOf("") }
                    var passwordVisible by remember { mutableStateOf(false) }

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

                    TextButton(
                        onClick = { /* TODO */ },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Forgot Password?", color = MaterialTheme.colorScheme.primary)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.login(email, password) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        if (loginState is Resource.Loading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Sign In", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }

                    if (loginState is Resource.Error) {
                        Text(
                            text = (loginState as Resource.Error).message ?: "An error occurred",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
                        Text(
                            "OR",
                            modifier = Modifier.padding(horizontal = 16.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Don't have an account? ", color = MaterialTheme.colorScheme.outline)
                        Text(
                            "Sign Up",
                            modifier = Modifier.clickable { onNavigateToSignUp() },
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordToggle: () -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        trailingIcon = {
            if (isPassword) {
                IconButton(onClick = onPasswordToggle) {
                    Icon(
                        if (passwordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                        contentDescription = null
                    )
                }
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Email),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            focusedLabelColor = MaterialTheme.colorScheme.primary
        )
    )
}
