package com.example.petcaresuperapp.presentation.screens.lostfound

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import androidx.compose.material.icons.filled.VideoCall
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.petcaresuperapp.data.model.LostFoundPost
import com.example.petcaresuperapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetDetailsScreen(
    navController: NavController, 
    postId: String?,
    viewModel: ContactRequestViewModel = hiltViewModel(),
    lostFoundViewModel: LostFoundViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val isAccepted by viewModel.isRequestAccepted.collectAsState()
    val isPending by viewModel.isRequestPending.collectAsState()

    LaunchedEffect(postId) {
        postId?.let { viewModel.checkIfRequestApproved(it) }
    }

    val posts by lostFoundViewModel.posts.collectAsState()
    val post = posts.find { it.id == postId } ?: LostFoundPost(
        id = postId ?: "1",
        imageUrl = "https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&q=80&w=800",
        petName = "Max",
        animalType = "Dog",
        breed = "Golden Retriever",
        status = "Lost",
        location = "Central Park, NY",
        description = "Max went missing around 6 PM near the duck pond.",
        reward = "$100",
        timestamp = System.currentTimeMillis()
    )

    val statusColor = when (post.status.lowercase()) {
        "lost" -> Color(0xFFE53935)
        "found" -> Color(0xFF43A047)
        "injured" -> Color(0xFFFF9800)
        else -> TextGray
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Details", color = TextWhite) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextWhite)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Share */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        },
        containerColor = BackgroundDark,
        bottomBar = {
            Surface(
                color = SurfaceDark,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                    if (isAccepted) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            IconButton(onClick = { Toast.makeText(context, "Chat feature coming soon", Toast.LENGTH_SHORT).show() }) {
                                Icon(Icons.Default.Message, contentDescription = "Chat", tint = Primary2026)
                            }
                            IconButton(onClick = { Toast.makeText(context, "Call feature coming soon", Toast.LENGTH_SHORT).show() }) {
                                Icon(Icons.Default.Call, contentDescription = "Call", tint = Primary2026)
                            }
                            IconButton(onClick = { Toast.makeText(context, "Video call feature coming soon", Toast.LENGTH_SHORT).show() }) {
                                Icon(Icons.Default.VideoCall, contentDescription = "Video", tint = Primary2026)
                            }
                        }
                    } else if (isPending) {
                        Text(
                            text = "Waiting for owner approval",
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            color = TextGray,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Button(
                            onClick = { 
                                viewModel.sendContactRequest(post.id, post.petName, post.ownerId)
                                Toast.makeText(context, "Contact Request Sent", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary2026),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Contact Owner", color = Color.White)
                        }
                    }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(post.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Pet Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-24).dp)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(BackgroundDark)
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = post.petName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = TextWhite
                    )
                    
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = statusColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = post.status.uppercase(),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "${post.breed} • ${post.animalType}",
                    fontSize = 16.sp,
                    color = TextGray
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = "Location", tint = Primary2026)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = post.location, fontSize = 16.sp, color = TextWhite)
                }

                if (!post.reward.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Secondary2026.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = "Reward: ${post.reward}",
                            modifier = Modifier.padding(16.dp),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Secondary2026,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Description",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextWhite
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = post.description,
                    fontSize = 15.sp,
                    lineHeight = 24.sp,
                    color = TextGray
                )
                
                Spacer(modifier = Modifier.height(80.dp)) // Extra space for bottom bar
            }
        }
    }
}
