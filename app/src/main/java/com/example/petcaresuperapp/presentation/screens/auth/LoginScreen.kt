package com.example.petcaresuperapp.presentation.screens.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.petcaresuperapp.R
import com.example.petcaresuperapp.core.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val loginState = viewModel.loginState.value

    LaunchedEffect(key1 = loginState) {
        when (loginState) {
            is Resource.Success -> {
                showSuccessDialog = true
            }
            is Resource.Error -> {
                errorMessage = loginState.message ?: "Login failed"
                showErrorDialog = true
            }
            else -> {}
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { 
                showSuccessDialog = false
                viewModel.resetLoginState()
                onLoginSuccess()
            },
            confirmButton = {
                TextButton(onClick = { 
                    showSuccessDialog = false
                    viewModel.resetLoginState()
                    onLoginSuccess()
                }) {
                    Text("OK")
                }
            },
            title = { Text("Success") },
            text = { Text("Login successful! Welcome back.") },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF43A047)) },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { 
                showErrorDialog = false
                viewModel.resetLoginState()
            },
            confirmButton = {
                Button(
                    onClick = { 
                        showErrorDialog = false
                        viewModel.resetLoginState()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("Try Again", color = Color.White)
                }
            },
            title = { Text("Login Failed") },
            text = { Text(errorMessage) },
            icon = { Icon(Icons.Default.Error, contentDescription = null, tint = Color(0xFFD32F2F)) },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }

    val scrollState = rememberScrollState()
    var isAnimationVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isAnimationVisible = true
    }

    val bgGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF81D4FA), Color(0xFFE1F5FE), Color.White)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // 1. Animated Header Section
            AnimatedVisibility(
                visible = isAnimationVisible,
                enter = slideInVertically(initialOffsetY = { -50 }, animationSpec = tween(600, easing = FastOutSlowInEasing)) + fadeIn(tween(600))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Pets,
                            contentDescription = null,
                            tint = Color(0xFF0277BD),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Welcome Back to",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF37474F)
                        )
                    }
                    Text(
                        text = "PetCare",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF01579B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 2. Animated Subtext
            AnimatedVisibility(
                visible = isAnimationVisible,
                enter = fadeIn(tween(800, delayMillis = 200))
            ) {
                Text(
                    text = "Your Pet's Health, Safety\n& Happiness in One Place.",
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    color = Color(0xFF455A64),
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(horizontal = 40.dp).alpha(0.8f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 3. Animated Login Card
            AnimatedVisibility(
                visible = isAnimationVisible,
                enter = slideInVertically(initialOffsetY = { it / 2 }, animationSpec = tween(800, easing = FastOutSlowInEasing)) + fadeIn(tween(800))
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = { Text("Email", fontSize = 14.sp) },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(10.dp),
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF1976D2), modifier = Modifier.size(20.dp))
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color(0xFFBDBDBD),
                                focusedBorderColor = Color(0xFF1976D2),
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            placeholder = { Text("Password", fontSize = 14.sp) },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(10.dp),
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF263238), modifier = Modifier.size(20.dp))
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                        tint = Color(0xFF263238),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color(0xFFBDBDBD),
                                focusedBorderColor = Color(0xFF1976D2),
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        val isLoading = loginState is Resource.Loading && loginState.isLoading
                        
                        Button(
                            onClick = { 
                                if (email.isNotEmpty() && password.isNotEmpty()) {
                                    viewModel.login(email, password)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            enabled = !isLoading,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Login", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Forgot Password?",
                            color = Color(0xFF1976D2),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable { }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Animated Footer Section
            AnimatedVisibility(
                visible = isAnimationVisible,
                enter = fadeIn(tween(1000, delayMillis = 400))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 40.dp)
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
                        Text(
                            text = " Or login with ",
                            color = Color.Gray,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Image(
                        painter = painterResource(id = R.drawable.pet1),
                        contentDescription = null,
                        modifier = Modifier.size(160.dp),
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "All Pet Services in One App.",
                        color = Color(0xFF01579B),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.padding(bottom = 30.dp)) {
                        Text("Don't have an account? ", color = Color.Gray, fontSize = 14.sp)
                        Text(
                            "Sign Up",
                            color = Color(0xFF1976D2),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable { onNavigateToSignUp() }
                        )
                    }
                }
            }
        }
    }
}
