package com.example.petcaresuperapp.presentation.screens.store

import androidx.lifecycle.ViewModel
import com.example.petcaresuperapp.domain.models.CartItem
import com.example.petcaresuperapp.domain.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    val cartItems: StateFlow<List<CartItem>> = cartRepository.cartItems

    fun addToCart(product: CartItem) {
        cartRepository.addToCart(product)
    }

    fun removeFromCart(productId: String) {
        cartRepository.removeFromCart(productId)
    }

    fun increaseQuantity(productId: String) {
        cartRepository.increaseQuantity(productId)
    }

    fun decreaseQuantity(productId: String) {
        cartRepository.decreaseQuantity(productId)
    }

    fun calculateTotalPrice(): Double {
        return cartRepository.calculateTotalPrice()
    }
}
