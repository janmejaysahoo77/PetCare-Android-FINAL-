package com.example.petcaresuperapp.presentation.screens.splash

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.petcaresuperapp.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNext: (String) -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val scale = remember { Animatable(0f) }
    var textVisible by remember { mutableStateOf(false) }
    
    val startDestination by viewModel.startDestination.collectAsState()

    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 0.9f,
            animationSpec = tween(
                durationMillis = 1000,
                easing = {
                    OvershootInterpolator(3f).getInterpolation(it)
                }
            )
        )
        textVisible = true
        delay(2000L)
        startDestination?.let { onNext(it) }
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1B5E20),
            Color(0xFF4CAF50),
            Color(0xFF81C784)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.splash),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(220.dp)
                    .scale(scale.value)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            AnimatedVisibility(
                visible = textVisible,
                enter = fadeIn(animationSpec = tween(1000)) + expandVertically(animationSpec = tween(1000)),
                exit = fadeOut()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "PetCare",
                        style = TextStyle(
                            fontSize = 48.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = 4.sp,
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.25f),
                                blurRadius = 8f
                            )
                        )
                    )
                    Text(
                        text = "SUPER APP",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Light,
                            color = Color.White.copy(alpha = 0.9f),
                            letterSpacing = 8.sp
                        ),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
        
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
        ) {
            AnimatedVisibility(
                visible = textVisible,
                enter = fadeIn(animationSpec = tween(1500, delayMillis = 500))
            ) {
                Text(
                    text = "Everything your pet needs",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

class OvershootInterpolator(private val tension: Float) {
    fun getInterpolation(t: Float): Float {
        var tValue = t
        tValue -= 1.0f
        return tValue * tValue * ((tension + 1) * tValue + tension) + 1.0f
    }
}
