package com.example.petcaresuperapp.presentation.screens.store

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.petcaresuperapp.domain.models.StoreProduct
import com.example.petcaresuperapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceScreen(
    navController: NavController,
    viewModel: MarketplaceViewModel = hiltViewModel()
) {
    val products by viewModel.products.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pet Marketplace", style = Typography.headlineLarge, color = TextWhite) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = TextWhite)
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("cart") }) {
                        Box {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Primary2026)
                            // Badge placeholder
                        }
                    }
                },
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .height(56.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurfaceDark)
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = TextGray)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Search premium pet supplies...", color = TextGray, style = Typography.bodyMedium)
                }
            }

            if (isLoading && products.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary2026)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(products) { product ->
                        PremiumProductCard(
                            product = product,
                            onClick = { navController.navigate("product_detail/${product.id}") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PremiumProductCard(product: StoreProduct, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "pressScale"
    )
    
    val elevation by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 4.dp,
        label = "elevation"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(28.dp),
        color = SurfaceDark,
        tonalElevation = elevation,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                AsyncImage(
                    model = product.imageUrl.ifEmpty { "https://images.unsplash.com/photo-1583511655857-d19b40a7a54e" },
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                )
                
                // Stock Badge
                Surface(
                    modifier = Modifier.padding(12.dp).align(Alignment.TopStart),
                    color = if (product.stock > 0) Primary2026.copy(alpha = 0.9f) else Error2026.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        if (product.stock > 0) "IN STOCK" else "SOLD OUT",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                
                // Add to Cart FAB
                Surface(
                    modifier = Modifier.padding(12.dp).align(Alignment.BottomEnd),
                    color = Primary2026,
                    shape = CircleShape,
                    shadowElevation = 4.dp
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(8.dp).size(18.dp)
                    )
                }
            }
            
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    product.category.uppercase(),
                    style = Typography.labelSmall,
                    color = Primary2026,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    product.name,
                    style = Typography.titleMedium,
                    color = TextWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "$${product.price}",
                    style = Typography.headlineSmall,
                    color = TextWhite,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

private fun String.ifEmpty(defaultValue: String): String = if (this.isEmpty()) defaultValue else this
private enum class TextOverflow { Ellipsis } // Fix for missing import or just use standard
