package com.example.petcaresuperapp.presentation.screens.community

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.petcaresuperapp.ui.theme.*

data class Post(
    val author: String,
    val time: String,
    val content: String,
    val likes: String,
    val comments: String,
    val petType: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityFeedScreen(navController: NavController) {
    val posts = listOf(
        Post("Sarah Wilson", "2h ago", "Buddy enjoying his morning walk in the park! #GoldenRetriever #HappyPet", "124", "12", "Dog"),
        Post("Mike Johnson", "5h ago", "New toy day! He won't let go of it.", "85", "5", "Cat"),
        Post("Elena Rodriguez", "1d ago", "Just finished our first training session. So proud!", "210", "45", "Dog")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Community", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Post */ }) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = PrimaryColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundColor)
            )
        },
        containerColor = BackgroundColor
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            item {
                StoriesSection()
            }
            items(posts) { post ->
                PostCard(post)
            }
        }
    }
}

@Composable
fun StoriesSection() {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(
            "Recent Activity",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(4) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(PrimaryGradient)
                        .padding(3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color.LightGray)
                    )
                }
            }
        }
    }
}

@Composable
fun PostCard(post: Post) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        color = SurfaceColor,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SecondaryGradient)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(post.author, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(post.time, fontSize = 11.sp, color = TextSecondary)
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { /* TODO */ }) {
                    Icon(Icons.Default.MoreVert, contentDescription = null, tint = TextSecondary)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(post.content, fontSize = 14.sp, color = TextPrimary, lineHeight = 20.sp)
            Spacer(modifier = Modifier.height(12.dp))
            
            // Image Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.LightGray.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Pets, contentDescription = null, tint = Color.White, modifier = Modifier.size(60.dp))
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.FavoriteBorder, contentDescription = null, tint = AccentColor, modifier = Modifier.size(20.dp))
                    Text(" ${post.likes}", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(20.dp))
                    Text(" ${post.comments}", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
                IconButton(onClick = { /* TODO */ }) {
                    Icon(Icons.Outlined.Share, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
