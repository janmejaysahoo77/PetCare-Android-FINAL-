package com.example.petcaresuperapp.presentation.screens.community

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.petcaresuperapp.ui.theme.*

data class ForumTopic(
    val title: String,
    val author: String,
    val category: String,
    val replies: Int,
    val views: Int,
    val time: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RescueForumScreen(navController: NavController) {
    val topics = listOf(
        ForumTopic("Advice for first-time adopters?", "Jane Doe", "Rescue", 24, 150, "2h ago"),
        ForumTopic("Looking for foster families in NY", "Safe Haven", "Foster", 12, 85, "5h ago"),
        ForumTopic("How to introduce a cat to a dog?", "PetLover99", "Training", 45, 300, "1d ago"),
        ForumTopic("Success story: From street to sweet home", "RescueTeam", "Success", 89, 500, "2d ago")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rescue Forum", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Search */ }) {
                        Icon(Icons.Default.Search, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundColor)
            )
        },
        containerColor = BackgroundColor,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: New Topic */ },
                containerColor = PrimaryColor,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.PostAdd, contentDescription = "New Topic")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            ForumCategories()
            
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                item {
                    Text("Latest Discussions", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
                }
                items(topics) { topic ->
                    ForumTopicCard(topic)
                }
            }
        }
    }
}

@Composable
fun ForumCategories() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ForumCategoryChip("Rescue", true)
        ForumCategoryChip("Health", false)
        ForumCategoryChip("Training", false)
    }
}

@Composable
fun ForumCategoryChip(label: String, isSelected: Boolean) {
    Surface(
        color = if (isSelected) PrimaryColor else Color.White,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = if (isSelected) 4.dp else 1.dp
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = if (isSelected) Color.White else TextSecondary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun ForumTopicCard(topic: ForumTopic) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { /* TODO */ },
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Badge(containerColor = SecondaryColor.copy(alpha = 0.1f)) {
                    Text(topic.category, color = SecondaryColor, modifier = Modifier.padding(4.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(topic.time, fontSize = 11.sp, color = TextGray)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(topic.title, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = TextPrimary)
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(24.dp).background(PrimaryColor.copy(alpha = 0.2f), RoundedCornerShape(6.dp)), contentAlignment = Alignment.Center) {
                        Text(topic.author.take(1), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(topic.author, fontSize = 12.sp, color = TextSecondary)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, modifier = Modifier.size(14.dp), tint = TextGray)
                    Text(" ${topic.replies}", fontSize = 12.sp, color = TextGray)
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp), tint = TextGray)
                    Text(" ${topic.views}", fontSize = 12.sp, color = TextGray)
                }
            }
        }
    }
}
