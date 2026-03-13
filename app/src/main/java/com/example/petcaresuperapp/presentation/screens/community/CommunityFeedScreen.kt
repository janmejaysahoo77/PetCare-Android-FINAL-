package com.example.petcaresuperapp.presentation.screens.community

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Intent
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.petcaresuperapp.domain.models.SocialPost
import com.example.petcaresuperapp.presentation.navigation.Screen
import com.example.petcaresuperapp.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityFeedScreen(
    navController: NavController,
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
    val viewModel: SocialViewModel = hiltViewModel()
    val posts by viewModel.posts.collectAsState()
    val likedPosts by viewModel.likedPosts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        modifier = Modifier.padding(paddingValues),
        topBar = {
            TopAppBar(
                title = { Text("Community", style = Typography.headlineLarge, color = TextWhite) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = TextWhite)
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Messages */ }) {
                        Icon(Icons.Outlined.Send, contentDescription = "Direct", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        },
        containerColor = BackgroundDark
    ) { padding ->
        if (isLoading && posts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary2026)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                item {
                    ModernStoriesSection()
                }
                items(posts) { post ->
                    val isLiked = likedPosts.contains(post.postId)
                    InstagramPostCard(
                        post = post,
                        isLiked = isLiked,
                        onLikeClick = { viewModel.toggleLike(post.postId) },
                        onCommentClick = { navController.navigate("comment_screen/${post.postId}") }
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun ModernStoriesSection() {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(6) { index ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(if (index == 0) Color.Gray.copy(alpha = 0.3f) else PrimaryGradient)
                        .padding(3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(SurfaceDark)
                            .padding(2.dp)
                    ) {
                        AsyncImage(
                            model = "https://i.pravatar.cc/150?u=$index",
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        if (index == 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(20.dp)
                                    .background(Primary2026, CircleShape)
                                    .border(2.dp, SurfaceDark, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    if (index == 0) "Your Story" else "User $index",
                    style = Typography.labelMedium,
                    color = TextGray
                )
            }
        }
    }
}

@Composable
fun InstagramPostCard(
    post: SocialPost,
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit
) {
    val context = LocalContext.current
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
            .padding(vertical = 8.dp)
            .background(SurfaceDark)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = post.userProfileImage.ifEmpty { "https://via.placeholder.com/150" },
                contentDescription = null,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                post.userName,
                style = Typography.labelLarge,
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { /* TODO */ }) {
                Icon(Icons.Default.MoreHoriz, contentDescription = null, tint = TextWhite)
            }
        }

        // Image with Double Tap
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            if (!isLiked) onLikeClick()
                            scope.launch {
                                showHeartOverlay = true
                                delay(800)
                                showHeartOverlay = false
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = post.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            AnimatedVisibility(
                visible = showHeartOverlay,
                enter = scaleIn(animationSpec = spring(Spring.DampingRatioMediumBouncy)) + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                Icon(
                    Icons.Filled.Favorite,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.size(100.dp)
                )
            }
        }

        // Actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onLikeClick) {
                Icon(
                    imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = null,
                    tint = if (isLiked) Color.Red else TextWhite,
                    modifier = Modifier.size(28.dp).scale(bounceScale)
                )
            }
            IconButton(onClick = onCommentClick) {
                Icon(Icons.Outlined.ModeComment, contentDescription = null, tint = TextWhite, modifier = Modifier.size(24.dp))
            }
            IconButton(onClick = {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "Check out this pet!")
                    putExtra(Intent.EXTRA_TEXT, "${post.userName}: ${post.caption}\n${post.imageUrl}")
                }
                context.startActivity(Intent.createChooser(shareIntent, "Share via"))
            }) {
                Icon(Icons.Outlined.Share, contentDescription = null, tint = TextWhite, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { /* TODO: Bookmark */ }) {
                Icon(Icons.Outlined.BookmarkBorder, contentDescription = null, tint = TextWhite, modifier = Modifier.size(26.dp))
            }
        }

        // Likes & Caption
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)) {
            Text(
                "${post.likeCount} likes",
                style = Typography.labelLarge,
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                Text(
                    post.userName,
                    style = Typography.labelLarge,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    post.caption,
                    style = Typography.bodyMedium,
                    color = TextWhite
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            if (post.commentCount > 0) {
                Text(
                    "View all ${post.commentCount} comments",
                    style = Typography.bodySmall,
                    color = TextGray,
                    modifier = Modifier.clickable { onCommentClick() }
                )
            }
            Text(
                "2 hours ago", // Dummy for now
                style = Typography.labelSmall,
                color = TextGray,
                fontSize = 10.sp
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}
