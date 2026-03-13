package com.example.petcaresuperapp.presentation.screens.dashboards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.petcaresuperapp.ui.theme.*

data class Product(
    val name: String,
    val price: String,
    val stock: String,
    val category: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreDashboardScreen(navController: NavController) {
    val products = listOf(
        Product("Premium Dog Food", "$45.00", "24", "Food"),
        Product("Cat Litter 10kg", "$15.99", "50", "Hygiene"),
        Product("Chew Toy Bone", "$9.50", "12", "Toys"),
        Product("Pet Carrier Large", "$65.00", "5", "Accessories")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pet Store Inventory", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundColor)
            )
        },
        containerColor = BackgroundColor,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO */ },
                containerColor = PrimaryColor,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Product")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            InventoryStats()
            
            Text(
                "Product Catalog",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(products) { product ->
                    ProductCard(product)
                }
            }
        }
    }
}

@Composable
fun InventoryStats() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        shape = RoundedCornerShape(24.dp),
        color = SecondaryColor.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Total Items", fontSize = 12.sp, color = TextSecondary)
                Text("1,452", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            }
            Column {
                Text("Low Stock", fontSize = 12.sp, color = TextSecondary)
                Text("12", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Accent2026)
            }
            Column {
                Text("Orders Today", fontSize = 12.sp, color = TextSecondary)
                Text("28", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Primary2026)
            }
        }
    }
}

@Composable
fun ProductCard(product: Product) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = SurfaceColor,
        shadowElevation = 2.dp
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(PrimaryColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.ShoppingBag,
                    contentDescription = null,
                    tint = PrimaryColor.copy(alpha = 0.5f),
                    modifier = Modifier.size(48.dp)
                )
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(product.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1)
                Text(product.category, fontSize = 11.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(product.price, fontWeight = FontWeight.ExtraBold, color = PrimaryColor)
                    Surface(
                        color = Primary2026.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "Stock: ${product.stock}",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primary2026
                        )
                    }
                }
            }
        }
    }
}
