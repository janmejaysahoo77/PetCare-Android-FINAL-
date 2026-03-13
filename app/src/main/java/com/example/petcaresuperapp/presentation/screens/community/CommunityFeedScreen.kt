package com.example.petcaresuperapp.presentation.screens.community

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.petcaresuperapp.presentation.components.PostCard
import com.example.petcaresuperapp.presentation.navigation.Screen
import com.example.petcaresuperapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityFeedScreen(
    navController: NavController,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    viewModel: SocialViewModel = hiltViewModel()
) {
    val posts by viewModel.posts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val currentUserId = "user_123" // Mock current user
    val likedPosts by viewModel.likedPosts.collectAsState()

    Scaffold(
        modifier = Modifier.padding(paddingValues),
        topBar = {
            TopAppBar(
                title = { Text("Community", style = Typography.headlineLarge, color = TextWhite) },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.CreatePost.route) }) {
                        Icon(Icons.Rounded.AddCircle, contentDescription = "Create Post", tint = Primary2026, modifier = Modifier.size(28.dp))
                    }
                    IconButton(onClick = { navController.navigate(Screen.Chat.route) }) {
                        Icon(Icons.Rounded.ChatBubbleOutline, contentDescription = "Messages", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        },
        containerColor = BackgroundDark
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {
            if (isLoading && posts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary2026)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    item { StoriesSection() }

                    items(posts, key = { it.postId }) { post ->
                        PostCard(
                            userName = post.userName,
                            userProfileImage = post.userProfileImage,
                            imageUrl = post.imageUrl,
                            caption = post.caption,
                            likeCount = post.likeCount,
                            commentCount = post.commentCount,
                            isLiked = likedPosts.contains(post.postId),
                            onLikeClick = { viewModel.toggleLike(post.postId) },
                            onCommentClick = { navController.navigate("comment_screen/${post.postId}") },
                            onDoubleTapLike = { viewModel.toggleLike(post.postId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StoriesSection() {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(6) { index ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .background(
                            brush = Brush.linearGradient(listOf(Primary2026, Secondary2026, Color(0xFFFACC15))),
                            shape = CircleShape
                        )
                        .padding(3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = "https://i.pravatar.cc/150?u=$index",
                        contentDescription = null,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(if (index == 0) "Your Story" else "User $index", style = Typography.labelSmall, color = TextGray)
            }
        }
    }
}
