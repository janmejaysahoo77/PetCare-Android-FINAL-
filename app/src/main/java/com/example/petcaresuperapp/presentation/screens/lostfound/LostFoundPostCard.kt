package com.example.petcaresuperapp.presentation.screens.lostfound

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.petcaresuperapp.data.model.LostFoundPost
import com.example.petcaresuperapp.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LostFoundPostCard(
    post: LostFoundPost,
    onViewDetailsClick: (String) -> Unit,
    viewModel: ContactRequestViewModel = hiltViewModel()
) {
    var isPending by remember { mutableStateOf(false) }
    var isRequestSent by remember { mutableStateOf(false) }
    val statusColor = when (post.status.lowercase()) {
        "lost" -> Color(0xFFE53935) // Reddish
        "found" -> Color(0xFF43A047) // Greenish
        "injured" -> Color(0xFFFF9800) // Orange
        else -> TextGray
    }

    val timeString = try {
        val sdf = SimpleDateFormat("dd MMM • hh:mm a", Locale.getDefault())
        sdf.format(Date(post.timestamp))
    } catch (e: Exception) {
        "Recently"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Pet Image
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(post.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Pet Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(16.dp)) {
                // Header Row: Name & Status Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = post.petName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = TextWhite
                    )
                    
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = statusColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = post.status.uppercase(),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Breed/Type
                Text(
                    text = "${post.breed} • ${post.animalType}",
                    fontSize = 14.sp,
                    color = TextGray
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Location & Time
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = "Location", tint = Primary2026, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = post.location, fontSize = 13.sp, color = TextGray)
                }
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = "Time", tint = TextGray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = timeString, fontSize = 13.sp, color = TextGray)
                }

                // Reward (Optional)
                if (!post.reward.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Secondary2026.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = "Reward: ${post.reward}",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Secondary2026
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { onViewDetailsClick(post.id) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary2026),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("View Details")
                    }
                    
                    Button(
                        onClick = { 
                            isPending = true
                            viewModel.sendContactRequest(post.id, post.petName, post.ownerId)
                            isRequestSent = true
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !isRequestSent,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isRequestSent) TextGray else Primary2026,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (isRequestSent) "Request Sent" else "Contact", color = Color.White)
                    }
                }
            }
        }
    }
}
