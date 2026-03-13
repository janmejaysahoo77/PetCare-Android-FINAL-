package com.example.petcaresuperapp.presentation.screens.community

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.petcaresuperapp.domain.models.Comment
import com.example.petcaresuperapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentScreen(
    navController: NavController,
    postId: String,
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
    val viewModel: SocialViewModel = hiltViewModel()
    var commentText by remember { mutableStateOf("") }
    val comments by viewModel.comments.collectAsState()

    LaunchedEffect(postId) {
        viewModel.loadComments(postId)
    }

    Scaffold(
        modifier = Modifier.padding(paddingValues),
        topBar = {
            TopAppBar(
                title = { Text("Comments", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundColor)
            )
        },
        containerColor = BackgroundColor,
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .navigationBarsPadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        placeholder = { Text("Add a comment...") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryColor,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    IconButton(
                        onClick = {
                            if (commentText.isNotBlank()) {
                                viewModel.addComment(postId, commentText)
                                commentText = ""
                            }
                        },
                        enabled = commentText.isNotBlank(),
                        colors = IconButtonDefaults.iconButtonColors(contentColor = PrimaryColor)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                    }
                }
            }
        }
    ) { padding ->
        if (comments.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No comments yet. Be the first to comment!", color = TextSecondary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(comments) { comment ->
                    CommentItem(comment)
                }
            }
        }
    }
}

@Composable
fun CommentItem(comment: Comment) {
    val dateFormat = remember { java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()) }
    val timeAgo = comment.createdAt?.toDate()?.let { dateFormat.format(it) } ?: "Now"

    Row(modifier = Modifier.fillMaxWidth()) {
        AsyncImage(
            model = comment.userProfileImage.ifEmpty { "https://via.placeholder.com/150" },
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(0.dp, 16.dp, 16.dp, 16.dp),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(comment.userName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(comment.comment, fontSize = 14.sp, color = TextPrimary, lineHeight = 20.sp)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(timeAgo, fontSize = 11.sp, color = TextSecondary, modifier = Modifier.padding(start = 4.dp))
        }
    }
}
