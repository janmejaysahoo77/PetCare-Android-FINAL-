package com.example.petcaresuperapp.presentation.screens.lostfound

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.petcaresuperapp.data.model.LostFoundPost
import com.example.petcaresuperapp.presentation.navigation.Screen
import com.example.petcaresuperapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LostFoundScreen(
    navController: NavController,
    viewModel: LostFoundViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val filters = listOf("All", "Lost", "Found", "Injured", "Dogs", "Cats", "Nearby")
    var selectedFilter by remember { mutableStateOf(filters[0]) }

    val realPosts by viewModel.posts.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lost & Found", fontWeight = FontWeight.Bold, color = TextWhite) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        },
        containerColor = BackgroundDark
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search pet, breed, location...", color = TextGray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = TextGray) },
                shape = RoundedCornerShape(32.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary2026,
                    unfocusedBorderColor = SurfaceDark,
                    focusedContainerColor = SurfaceDark,
                    unfocusedContainerColor = SurfaceDark,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                )
            )

            // Filter Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filters) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter, fontWeight = if (selectedFilter == filter) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = SurfaceDark,
                            labelColor = TextGray,
                            selectedContainerColor = Primary2026.copy(alpha = 0.2f),
                            selectedLabelColor = Primary2026
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedFilter == filter,
                            borderColor = if (selectedFilter == filter) Primary2026 else Color(0xFF1E1E1E),
                            borderWidth = 1.dp
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }

            // Feed List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp), // Extra padding for FAB
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val filteredPosts = if (selectedFilter == "All") {
                    realPosts
                } else if (selectedFilter in listOf("Lost", "Found", "Injured")) {
                    realPosts.filter { it.status.equals(selectedFilter, ignoreCase = true) }
                } else if (selectedFilter == "Dogs") {
                    realPosts.filter { it.animalType.equals("Dog", ignoreCase = true) }
                } else if (selectedFilter == "Cats") {
                    realPosts.filter { it.animalType.equals("Cat", ignoreCase = true) }
                } else {
                    realPosts
                }

                items(filteredPosts) { post ->
                    LostFoundPostCard(
                        post = post,
                        onViewDetailsClick = { postId -> 
                            navController.navigate("lost_found_detail/$postId")
                        }
                    )
                }
            }
        }
    }
}
