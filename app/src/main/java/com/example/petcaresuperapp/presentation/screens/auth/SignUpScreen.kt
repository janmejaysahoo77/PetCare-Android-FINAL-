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
fun SignUpScreen(
    onSignUpSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var showErrorDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val signUpState = viewModel.signUpState.value

    LaunchedEffect(key1 = signUpState) {
        when (signUpState) {
            is Resource.Success -> {
                showSuccessDialog = true
            }
            is Resource.Error -> {
                errorMessage = signUpState.message ?: "Registration failed"
                showErrorDialog = true
            }
            else -> {}
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { 
                showSuccessDialog = false
                viewModel.resetSignUpState()
                onSignUpSuccess()
            },
            confirmButton = {
                TextButton(onClick = { 
                    showSuccessDialog = false
                    viewModel.resetSignUpState()
                    onSignUpSuccess()
                }) {
                    Text("Welcome!")
                }
            },
            title = { Text("Account Created") },
            text = { Text("Your account has been created successfully. Let's get started!") },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF43A047)) },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { 
                showErrorDialog = false
                viewModel.resetSignUpState()
            },
            confirmButton = {
                Button(
                    onClick = { 
                        showErrorDialog = false
                        viewModel.resetSignUpState()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("Try Again", color = Color.White)
                }
            },
            title = { Text("Sign Up Failed") },
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

    // Colors matching the theme
    val darkGreen = Color(0xFF2C5E1A)
    val lightGreenBg = Color(0xFFAED581)
    val buttonGreen = Color(0xFF66BB6A)
    val cardBg = Color(0xFFF9FBE7)

    val bgGradient = Brush.verticalGradient(
        colors = listOf(lightGreenBg, Color.White)
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
            Spacer(modifier = Modifier.height(40.dp))

            // 1. Animated Header Section
            AnimatedVisibility(
                visible = isAnimationVisible,
                enter = slideInVertically(initialOffsetY = { -50 }, animationSpec = tween(600, easing = FastOutSlowInEasing)) + fadeIn(tween(600))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Pets,
                            contentDescription = null,
                            tint = darkGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Join the PetCare Family",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = darkGreen
                        )
                    }
                    Text(
                        text = "Join the\nPetCare Family",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = darkGreen,
                        textAlign = TextAlign.Center,
                        lineHeight = 34.sp
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
                    text = "Create your account and give\nyour pet the best care.",
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = darkGreen,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(horizontal = 40.dp).alpha(0.8f)
                )
            }

            // 3. Animated Pet Illustration
            AnimatedVisibility(
                visible = isAnimationVisible,
                enter = fadeIn(tween(800, delayMillis = 300)) + scaleIn(initialScale = 0.8f, animationSpec = tween(800, delayMillis = 300))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.pet2),
                    contentDescription = "Pet Illustration",
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(180.dp)
                        .offset(y = 15.dp),
                    contentScale = ContentScale.Fit
                )
            }

            // 4. Animated SignUp Card
            AnimatedVisibility(
                visible = isAnimationVisible,
                enter = slideInVertically(initialOffsetY = { it / 2 }, animationSpec = tween(800, easing = FastOutSlowInEasing)) + fadeIn(tween(800))
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp, bottomStart = 24.dp, bottomEnd = 24.dp),
                    color = cardBg,
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Sign Up",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = darkGreen,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        AuthTextField(
                            value = name,
                            onValueChange = { name = it },
                            placeholder = "Full Name",
                            icon = Icons.Default.Person
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        AuthTextField(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = "Email",
                            icon = Icons.Default.Email
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        AuthTextField(
                            value = password,
                            onValueChange = { password = it },
                            placeholder = "Create Password",
                            icon = Icons.Default.Lock,
                            isPassword = true,
                            passwordVisible = passwordVisible,
                            onVisibilityToggle = { passwordVisible = !passwordVisible }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        AuthTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            placeholder = "Confirm Password",
                            icon = Icons.Default.Lock,
                            isPassword = true,
                            passwordVisible = confirmPasswordVisible,
                            onVisibilityToggle = { confirmPasswordVisible = !confirmPasswordVisible }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        val isLoading = signUpState is Resource.Loading && signUpState.isLoading

                        Button(
                            onClick = { 
                                if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                                    if (password == confirmPassword) {
                                        viewModel.signUp(name, email, password)
                                    } else {
                                        errorMessage = "Passwords do not match"
                                        showErrorDialog = true
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            enabled = !isLoading,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = buttonGreen),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    "Sign Up",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "All Pet Services in One App.",
                            color = darkGreen,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 5. Animated Footer
            AnimatedVisibility(
                visible = isAnimationVisible,
                enter = fadeIn(tween(1000, delayMillis = 400))
            ) {
                Row(modifier = Modifier.padding(bottom = 24.dp)) {
                    Text("Already have an account? ", color = darkGreen, fontSize = 14.sp)
                    Text(
                        "Log In",
                        color = Color(0xFF1B5E20),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { onNavigateToLogin() }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onVisibilityToggle: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color.Gray, fontSize = 14.sp) },
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(10.dp),
        leadingIcon = {
            Icon(icon, contentDescription = null, tint = Color(0xFF37474F), modifier = Modifier.size(20.dp))
        },
        trailingIcon = if (isPassword && onVisibilityToggle != null) {
            {
                IconButton(onClick = onVisibilityToggle) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = Color(0xFF37474F),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        } else null,
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color.LightGray,
            focusedBorderColor = Color(0xFF66BB6A),
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White
        ),
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
    )
}
