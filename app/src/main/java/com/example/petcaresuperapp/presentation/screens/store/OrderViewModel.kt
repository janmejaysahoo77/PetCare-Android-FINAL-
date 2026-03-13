package com.example.petcaresuperapp.presentation.screens.store

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcaresuperapp.domain.models.CartItem
import com.example.petcaresuperapp.domain.models.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.google.firebase.Timestamp
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _orderState = MutableStateFlow<OrderPlacementState>(OrderPlacementState.Idle)
    val orderState: StateFlow<OrderPlacementState> = _orderState.asStateFlow()

    init {
        listenForOrders()
    }

    private fun listenForOrders() {
        val userId = auth.currentUser?.uid ?: return

        _isLoading.value = true
        firestore.collection("orders")
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                _isLoading.value = false
                if (e != null) {
                    e.printStackTrace()
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val orderList = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Order::class.java)
                    }
                    _orders.value = orderList
                }
            }
    }

    fun placeOrder(items: List<CartItem>, totalAmount: Double) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _orderState.value = OrderPlacementState.Error("User not logged in")
            return
        }

        if (items.isEmpty()) {
            _orderState.value = OrderPlacementState.Error("No items to order")
            return
        }

        viewModelScope.launch {
            _orderState.value = OrderPlacementState.Loading
            try {
                val newOrder = Order(
                    userId = userId,
                    items = items,
                    totalAmount = totalAmount,
                    status = "Pending",
                    createdAt = Timestamp.now()
                )
                
                firestore.collection("orders").add(newOrder).await()
                _orderState.value = OrderPlacementState.Success
            } catch (e: Exception) {
                e.printStackTrace()
                _orderState.value = OrderPlacementState.Error(e.message ?: "Failed to place order")
            }
        }
    }

    fun resetOrderState() {
        _orderState.value = OrderPlacementState.Idle
    }
}

sealed class OrderPlacementState {
    object Idle : OrderPlacementState()
    object Loading : OrderPlacementState()
    object Success : OrderPlacementState()
    data class Error(val message: String) : OrderPlacementState()
}
