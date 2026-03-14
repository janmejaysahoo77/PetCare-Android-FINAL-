package com.example.petcaresuperapp.presentation.screens.community

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.*
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
    val users by viewModel.users.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val likedPosts by viewModel.likedPosts.collectAsState()
    val currentUserId by viewModel.currentUserId.collectAsState()

    val usersMap = remember(users) { users.associateBy { it.id } }
    
    var postToDelete by remember { mutableStateOf<String?>(null) }

    if (postToDelete != null) {
        AlertDialog(
            onDismissRequest = { postToDelete = null },
            title = { Text("Delete Post") },
            text = { Text("Are you sure you want to delete this post? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        postToDelete?.let { viewModel.deletePost(it) }
                        postToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Error2026)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { postToDelete = null }) {
                    Text("Cancel")
                }
            },
            containerColor = SurfaceDark,
            titleContentColor = TextWhite,
            textContentColor = TextGray
        )
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Community", 
                        style = Typography.headlineLarge, 
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    ) 
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Chat.route) }) {
                        Icon(
                            Icons.Rounded.ChatBubbleOutline, 
                            contentDescription = "Messages", 
                            tint = TextWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundDark,
                    titleContentColor = TextWhite
                )
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
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    item { 
                        StoriesSection(
                            currentUser = currentUser,
                            users = users.filter { it.id != currentUser?.id }
                        ) 
                    }
                    
                    item { 
                        CreatePostCard(
                            userProfileImage = currentUser?.photoUrl ?: "",
                            onNavigateToCreate = { navController.navigate(Screen.CreatePost.route) }
                        ) 
                    }

                    items(posts, key = { it.postId }) { post ->
                        val postUser = usersMap[post.userId]
                        val isOwnPost = currentUserId != null && post.userId == currentUserId
                        
                        // Robust name resolution
                        val resolvedName = when {
                            isOwnPost && !currentUser?.name.isNullOrBlank() -> currentUser?.name!!
                            postUser != null && !postUser.name.isNullOrBlank() -> postUser.name
                            !post.userName.isNullOrBlank() -> post.userName
                            else -> "User"
                        }
                            
                        // Robust photo resolution
                        val resolvedPhoto = when {
                            isOwnPost && !currentUser?.photoUrl.isNullOrBlank() -> currentUser?.photoUrl!!
                            postUser != null && !postUser.photoUrl.isNullOrBlank() -> postUser.photoUrl
                            !post.userProfileImage.isNullOrBlank() -> post.userProfileImage
                            else -> "https://i.pravatar.cc/150?u=${post.userId}"
                        }

                        PostCard(
                            userName = resolvedName,
                            userProfileImage = resolvedPhoto,
                            imageUrl = post.imageUrl,
                            caption = post.caption,
                            likeCount = post.likeCount,
                            commentCount = post.commentCount,
                            isLiked = likedPosts.contains(post.postId),
                            isOwnPost = isOwnPost,
                            onLikeClick = { viewModel.toggleLike(post.postId) },
                            onCommentClick = { navController.navigate("comment_screen/${post.postId}") },
                            onDeleteClick = { postToDelete = post.postId },
                            onDoubleTapLike = { viewModel.toggleLike(post.postId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StoriesSection(
    currentUser: com.example.petcaresuperapp.domain.models.User?,
    users: List<com.example.petcaresuperapp.domain.models.User>
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Your Story
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .border(2.dp, TextGray.copy(alpha = 0.3f), CircleShape)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = if (currentUser?.photoUrl.isNullOrEmpty()) "https://i.pravatar.cc/150?u=me" else currentUser?.photoUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.BottomEnd)
                                .background(Primary2026, CircleShape)
                                .border(2.dp, BackgroundDark, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text("Your Story", style = Typography.labelSmall, color = TextWhite)
            }
        }

        items(users) { user ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .background(
                            brush = Brush.linearGradient(listOf(Primary2026, Secondary2026, Color(0xFFFACC15))),
                            shape = CircleShape
                        )
                        .padding(3.dp)
                        .background(BackgroundDark, CircleShape)
                        .padding(3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = if (user.photoUrl.isNullOrEmpty()) "https://i.pravatar.cc/150?u=${user.id}" else user.photoUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                                .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(user.name.split(" ").firstOrNull() ?: "User", style = Typography.labelSmall, color = TextGray)
            }
        }
    }
}

@Composable
fun CreatePostCard(
    userProfileImage: String,
    onNavigateToCreate: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        color = SurfaceDark,
        tonalElevation = 2.dp,
        onClick = onNavigateToCreate
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = if (userProfileImage.isEmpty()) "https://i.pravatar.cc/150?u=me" else userProfileImage,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Share something about your pet...",
                    style = Typography.bodyMedium,
                    color = TextGray
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color.White.copy(alpha = 0.05f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TextButton(onClick = onNavigateToCreate) {
                    Icon(Icons.Rounded.PhotoLibrary, contentDescription = null, tint = Info2026, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Photo", color = TextWhite)
                }
                TextButton(onClick = onNavigateToCreate) {
                    Icon(Icons.Rounded.Videocam, contentDescription = null, tint = Secondary2026, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Video", color = TextWhite)
                }
            }
        }
    }
}
