package com.example.petcaresuperapp.presentation.screens.store

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcaresuperapp.domain.models.StoreProduct
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _product = MutableStateFlow<StoreProduct?>(null)
    val product: StateFlow<StoreProduct?> = _product.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun fetchProductDetails(productId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val snapshot = firestore.collection("products").document(productId).get().await()
                val fetchedProduct = snapshot.toObject(StoreProduct::class.java)
                _product.value = fetchedProduct
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
