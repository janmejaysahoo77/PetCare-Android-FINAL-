package com.example.petcaresuperapp.presentation.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.petcaresuperapp.R
import com.example.petcaresuperapp.presentation.screens.auth.AuthViewModel
import com.example.petcaresuperapp.ui.theme.*
import kotlinx.coroutines.launch

data class OnboardingData(
    val title: String,
    val description: String,
    val image: Int,
    val colors: List<Color>
)

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val onboardingPages = listOf(
        OnboardingData(
            title = "Manage Your Pet Health",
            description = "Centralize all your pet's medical records and health history in one secure digital card.",
            image = R.drawable.onboaring1,
            colors = listOf(PrimaryLight.copy(alpha = 0.2f), MaterialTheme.colorScheme.background)
        ),
        OnboardingData(
            title = "Vet Appointments",
            description = "Book appointments with top-rated veterinarians in your area with just a few taps.",
            image = R.drawable.onboaring2,
            colors = listOf(SecondaryLight.copy(alpha = 0.2f), MaterialTheme.colorScheme.background)
        ),
        OnboardingData(
            title = "Adoption & Community",
            description = "Connect with a caring community of pet lovers and find your perfect companion.",
            image = R.drawable.onboaring3,
            colors = listOf(AccentLight.copy(alpha = 0.2f), MaterialTheme.colorScheme.background)
        )
    )

    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = onboardingPages[pagerState.currentPage].colors
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                OnboardingPage(data = onboardingPages[page], isSelected = pagerState.currentPage == page)
            }

            // Bottom Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 40.dp)
            ) {
                // Page Indicator
                Row(
                    Modifier
                        .align(Alignment.CenterStart)
                        .height(56.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(onboardingPages.size) { iteration ->
                        val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                        val width by animateDpAsState(
                            targetValue = if (pagerState.currentPage == iteration) 24.dp else 8.dp,
                            label = "width"
                        )
                        Box(
                            modifier = Modifier
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(color)
                                .size(width = width, height = 8.dp)
                        )
                    }
                }

                // Next/Get Started Button
                val isLastPage = pagerState.currentPage == onboardingPages.size - 1
                Button(
                    onClick = {
                        if (isLastPage) {
                            viewModel.completeOnboarding()
                            onFinished()
                        } else {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .height(56.dp)
                        .width(if (isLastPage) 160.dp else 64.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    AnimatedContent(
                        targetState = isLastPage,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                        },
                        label = "buttonText"
                    ) { last ->
                        if (last) {
                            Text("Get Started", fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Rounded.ChevronRight, contentDescription = "Next")
                        }
                    }
                }
            }
        }

        // Skip Button
        if (pagerState.currentPage < onboardingPages.size - 1) {
            TextButton(
                onClick = {
                    viewModel.completeOnboarding()
                    onFinished()
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(16.dp)
            ) {
                Text(
                    "Skip",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun OnboardingPage(data: OnboardingData, isSelected: Boolean) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.8f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "alpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = data.image),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .aspectRatio(1f)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    this.alpha = alpha
                },
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = data.title,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.graphicsLayer { this.alpha = alpha }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = data.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
            modifier = Modifier.graphicsLayer { this.alpha = alpha }
        )
    }
}
