package com.example.petcaresuperapp.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.petcaresuperapp.ui.theme.*

@Composable
fun PetCard(
    petName: String,
    petDescription: String,
    petImageUrl: String,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clickable { onClick() }
            .animateContentSize(),
        shape = RoundedCornerShape(RoundedCornersLarge),
        color = SurfaceDark,
        tonalElevation = CardElevationPremium
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = petImageUrl.ifEmpty { "https://images.unsplash.com/photo-1543466835-00a7907e9de1" },
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                            startY = 350f
                        )
                    )
            )
            
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(24.dp)
            ) {
                Text(
                    text = petName,
                    style = Typography.headlineLarge,
                    color = Color.White
                )
                Text(
                    text = petDescription.ifEmpty { "A very happy pet!" },
                    style = Typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                color = Primary2026,
                shape = CircleShape
            ) {
                Icon(
                    Icons.Rounded.Pets,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(10.dp).size(24.dp)
                )
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String,
    color: Color,
    delay: Int = 0
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delay.toLong())
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + scaleIn(initialScale = 0.8f),
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier
                .height(140.dp)
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.White.copy(alpha = 0.1f), Color.Transparent)
                    ),
                    shape = RoundedCornerShape(RoundedCornersLarge)
                ),
            color = SurfaceDark.copy(alpha = 0.4f), // Glassmorphism effect
            shape = RoundedCornerShape(RoundedCornersLarge),
            tonalElevation = CardElevationPremium
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(color.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                }
                Column {
                    Text(value, style = Typography.titleLarge, color = TextWhite)
                    Text(label, style = Typography.labelMedium, color = TextGray)
                }
            }
        }
    }
}

@Composable
fun PostCard(
    userName: String,
    userProfileImage: String,
    imageUrl: String,
    caption: String,
    likeCount: Int,
    commentCount: Int,
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onDoubleTapLike: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var showHeartOverlay by remember { mutableStateOf(false) }
    
    val bounceScale by animateFloatAsState(
        targetValue = if (isLiked) 1.2f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "likeBounce"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .background(SurfaceDark)
            .clip(RoundedCornerShape(RoundedCornersLarge))
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = userProfileImage.ifEmpty { "https://i.pravatar.cc/150" },
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                userName,
                style = Typography.labelLarge,
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { /* More options */ }) {
                Icon(Icons.Default.MoreHoriz, contentDescription = null, tint = TextWhite)
            }
        }

        // Image with Interaction (Simulated double tap should be handled by caller or here)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            Column {
                androidx.compose.animation.AnimatedVisibility(
                    visible = showHeartOverlay,
                    enter = scaleIn(animationSpec = spring(Spring.DampingRatioMediumBouncy)) + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    Icon(
                        Icons.Filled.Favorite,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(120.dp)
                    )
                }
            }
        }

        // Actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onLikeClick) {
                Icon(
                    imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = null,
                    tint = if (isLiked) Color.Red else TextWhite,
                    modifier = Modifier.size(30.dp).scale(bounceScale)
                )
            }
            IconButton(onClick = onCommentClick) {
                Icon(Icons.Outlined.ModeComment, contentDescription = null, tint = TextWhite, modifier = Modifier.size(26.dp))
            }
            IconButton(onClick = { /* Share */ }) {
                Icon(Icons.Outlined.Share, contentDescription = null, tint = TextWhite, modifier = Modifier.size(26.dp))
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { /* Bookmark */ }) {
                Icon(Icons.Outlined.BookmarkBorder, contentDescription = null, tint = TextWhite, modifier = Modifier.size(28.dp))
            }
        }

        // Stats & Caption
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
            Text(
                "$likeCount likes",
                style = Typography.labelLarge,
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                Text(
                    userName,
                    style = Typography.labelLarge,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    caption,
                    style = Typography.bodyMedium,
                    color = TextWhite
                )
            }
            if (commentCount > 0) {
                Text(
                    "View all $commentCount comments",
                    style = Typography.bodySmall,
                    color = TextGray,
                    modifier = Modifier.padding(vertical = 4.dp).clickable { onCommentClick() }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ProductCard(
    name: String,
    price: Double,
    imageUrl: String,
    category: String,
    stock: Int,
    onClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "pressScale"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(RoundedCornersLarge),
        color = SurfaceDark,
        tonalElevation = CardElevationPremium,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                AsyncImage(
                    model = imageUrl.ifEmpty { "https://images.unsplash.com/photo-1583511655857-d19b40a7a54e" },
                    contentDescription = name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                
                if (stock <= 0) {
                    Surface(
                        modifier = Modifier.padding(12.dp).align(Alignment.TopStart),
                        color = Error2026.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "SOLD OUT",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                
                Surface(
                    onClick = onAddToCart,
                    modifier = Modifier.padding(12.dp).align(Alignment.BottomEnd),
                    color = Primary2026,
                    shape = CircleShape,
                    shadowElevation = 4.dp
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(10.dp).size(20.dp)
                    )
                }
            }
            
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    category.uppercase(),
                    style = Typography.labelSmall,
                    color = Primary2026,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    name,
                    style = Typography.titleMedium,
                    color = TextWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "$${price}",
                    style = Typography.headlineSmall,
                    color = TextWhite,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
fun VaccinationCard(
    name: String,
    date: String,
    vet: String,
    status: String,
    icon: ImageVector
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(RoundedCornersLarge),
        color = SurfaceDark,
        tonalElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        if (status == "Completed") Primary2026.copy(alpha = 0.1f) 
                        else Secondary2026.copy(alpha = 0.1f),
                        RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon, 
                    contentDescription = null, 
                    tint = if (status == "Completed") Primary2026 else Secondary2026,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TextWhite)
                Text("$date • $vet", fontSize = 13.sp, color = TextGray)
            }
            Surface(
                color = if (status == "Completed") Primary2026.copy(alpha = 0.15f) 
                        else Info2026.copy(alpha = 0.15f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    status,
                    style = Typography.labelMedium,
                    color = if (status == "Completed") Primary2026 else Info2026,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}
