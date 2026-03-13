package com.example.petcaresuperapp.presentation.screens.store

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.petcaresuperapp.domain.models.CartItem
import com.example.petcaresuperapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    navController: NavController,
    productId: String,
    viewModel: ProductDetailViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel(),
    orderViewModel: OrderViewModel = hiltViewModel()
) {
    val product by viewModel.product.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val orderState by orderViewModel.orderState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(productId) {
        viewModel.fetchProductDetails(productId)
    }

    LaunchedEffect(orderState) {
        if (orderState is OrderPlacementState.Success) {
            Toast.makeText(context, "Order placed successfully!", Toast.LENGTH_SHORT).show()
            orderViewModel.resetOrderState()
            navController.navigate("my_orders") {
                popUpTo("marketplace")
            }
        } else if (orderState is OrderPlacementState.Error) {
            Toast.makeText(context, (orderState as OrderPlacementState.Error).message, Toast.LENGTH_SHORT).show()
            orderViewModel.resetOrderState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("cart") }) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = PrimaryColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundColor)
            )
        },
        containerColor = BackgroundColor
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryColor)
            }
        } else {
            product?.let { currentProduct ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    // Scrollable Content
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Image
                        AsyncImage(
                            model = currentProduct.imageUrl,
                            contentDescription = currentProduct.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .background(Color.LightGray)
                        )

                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = currentProduct.category.uppercase(),
                                color = PrimaryColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = currentProduct.name,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "$${currentProduct.price}",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    color = PrimaryColor
                                )
                                Surface(
                                    color = if (currentProduct.stock > 0) SuccessGradStart.copy(alpha = 0.1f) else Color.Red.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = if (currentProduct.stock > 0) "In Stock: ${currentProduct.stock}" else "Out of Stock",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        color = if (currentProduct.stock > 0) SuccessGradStart else Color.Red,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                            Text("Description", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = currentProduct.description.ifEmpty { "No description available for this product." },
                                color = TextSecondary,
                                lineHeight = 24.sp
                            )
                        }
                    } // end scrollable Column

                    // Fixed Bottom Buttons — always visible
                    Surface(
                        color = Color.White,
                        shadowElevation = 8.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .navigationBarsPadding(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val cartItem = CartItem(
                                        productId = currentProduct.id,
                                        name = currentProduct.name,
                                        price = currentProduct.price,
                                        imageUrl = currentProduct.imageUrl,
                                        quantity = 1
                                    )
                                    cartViewModel.addToCart(cartItem)
                                    Toast.makeText(context, "Item added to cart", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f).height(50.dp),
                                enabled = currentProduct.stock > 0,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Add to Cart")
                            }
                            Button(
                                onClick = {
                                    val singleItemCart = CartItem(
                                        productId = currentProduct.id,
                                        name = currentProduct.name,
                                        price = currentProduct.price,
                                        imageUrl = currentProduct.imageUrl,
                                        quantity = 1
                                    )
                                    orderViewModel.placeOrder(listOf(singleItemCart), currentProduct.price)
                                },
                                modifier = Modifier.weight(1f).height(50.dp),
                                enabled = currentProduct.stock > 0 && orderState !is OrderPlacementState.Loading,
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                if (orderState is OrderPlacementState.Loading) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                                } else {
                                    Text("Buy Now")
                                }
                            }
                        }
                    }
                } // end outer Column
            } ?: run {
                if (!isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Product not found", color = TextSecondary)
                    }
                }
            }
        }
    }
}
