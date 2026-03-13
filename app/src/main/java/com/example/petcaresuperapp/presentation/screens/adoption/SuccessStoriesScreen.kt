package com.example.petcaresuperapp.presentation.screens.adoption

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.petcaresuperapp.ui.theme.*

data class SuccessStory(
    val petName: String,
    val ownerName: String,
    val story: String,
    val date: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuccessStoriesScreen(navController: NavController) {
    val stories = listOf(
        SuccessStory("Max", "Sarah J.", "Max has been with us for 6 months now. He's the light of our lives and has become best friends with our older dog.", "Oct 2023"),
        SuccessStory("Bella", "David R.", "Adopting Bella was the best decision. She was shy at first, but now she's the most affectionate cat.", "Dec 2023"),
        SuccessStory("Cooper", "Emily W.", "From a lonely shelter pup to a happy mountain hiker. Cooper loves his new life!", "Jan 2024")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Success Stories", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
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
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Text(
                    "Heartwarming Adoption Tales",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimaryColor
                )
            }
            items(stories) { story ->
                StoryCard(story)
            }
        }
    }
}

@Composable
fun StoryCard(story: SuccessStory) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(SuccessGradStart.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Favorite, contentDescription = null, tint = SuccessGradStart, modifier = Modifier.size(60.dp))
            }
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${story.petName} & ${story.ownerName}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(story.date, fontSize = 12.sp, color = TextGrey)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    story.story,
                    fontSize = 14.sp,
                    color = TextSecondary,
                    lineHeight = 22.sp
                )
            }
        }
    }
}
