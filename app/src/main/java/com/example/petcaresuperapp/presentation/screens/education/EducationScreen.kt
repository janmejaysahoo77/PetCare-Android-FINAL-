package com.example.petcaresuperapp.presentation.screens.education

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Search
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

data class Article(
    val title: String,
    val category: String,
    val duration: String,
    val type: String // "Video" or "Article"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EducationScreen(navController: NavController) {
    val content = listOf(
        Article("Puppy Training 101: Basic Commands", "Training", "15 min", "Video"),
        Article("Optimal Diet for Senior Dogs", "Nutrition", "8 min read", "Article"),
        Article("Understanding Your Cat's Body Language", "Behavior", "10 min read", "Article"),
        Article("Preventative Care for Indoor Pets", "Health", "5 min read", "Article")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pet Education", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.Search, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundColor)
            )
        },
        containerColor = BackgroundColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            EducationCategories()
            
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                item {
                    Text("Featured Content", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
                }
                items(content) { article ->
                    EducationCard(article)
                }
            }
        }
    }
}

@Composable
fun EducationCategories() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CategoryIconBox("Health", PrimaryColor)
        CategoryIconBox("Diet", Accent2026)
        CategoryIconBox("Training", SecondaryColor)
        CategoryIconBox("Care", Primary2026)
    }
}

@Composable
fun CategoryIconBox(label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier.size(60.dp),
            shape = RoundedCornerShape(16.dp),
            color = color.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(label.take(1), fontWeight = FontWeight.Black, color = color, fontSize = 20.sp)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun EducationCard(article: Article) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = SurfaceColor,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(PrimaryGradient, alpha = 0.1f),
                contentAlignment = Alignment.Center
            ) {
                if (article.type == "Video") {
                    Icon(Icons.Default.PlayCircle, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(32.dp))
                } else {
                    Text("Aa", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(article.category, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SecondaryColor)
                Text(article.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, lineHeight = 20.sp)
                Text(article.duration, fontSize = 12.sp, color = TextSecondary)
            }
        }
    }
}
