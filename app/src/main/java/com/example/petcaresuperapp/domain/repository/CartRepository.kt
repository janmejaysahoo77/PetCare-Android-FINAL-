package com.example.petcaresuperapp.domain.repository

import com.example.petcaresuperapp.domain.models.CartItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepository @Inject constructor() {
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    fun addToCart(product: CartItem) {
        _cartItems.update { items ->
            val existingItem = items.find { it.productId == product.productId }
            if (existingItem != null) {
                items.map {
                    if (it.productId == product.productId) {
                        it.copy(quantity = it.quantity + product.quantity)
                    } else it
                }
            } else {
                items + product
            }
        }
    }

    fun removeFromCart(productId: String) {
        _cartItems.update { items ->
            items.filter { it.productId != productId }
        }
    }

    fun increaseQuantity(productId: String) {
        _cartItems.update { items ->
            items.map {
                if (it.productId == productId) it.copy(quantity = it.quantity + 1)
                else it
            }
        }
    }

    fun decreaseQuantity(productId: String) {
        _cartItems.update { items ->
            items.map {
                if (it.productId == productId && it.quantity > 1) it.copy(quantity = it.quantity - 1)
                else it
            }
        }
    }

    fun calculateTotalPrice(): Double {
        return _cartItems.value.sumOf { it.price * it.quantity }
    }
}
