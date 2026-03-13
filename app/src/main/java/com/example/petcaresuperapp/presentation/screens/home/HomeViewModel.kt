package com.example.petcaresuperapp.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class HomeState(
    val userName: String = "",
    val petName: String = "",
    val petDescription: String = "",
    val petImageUrl: String = "",
    val isLoading: Boolean = true,
    val hasPet: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    init {
        fetchHomeData()
    }

    private fun fetchHomeData() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _uiState.update { it.copy(isLoading = false) }
            return
        }

        viewModelScope.launch {
            try {
                // Fetch User Name
                val userDoc = firestore.collection("users").document(userId).get().await()
                val fetchedUserName = userDoc.getString("name") ?: "User"

                // Fetch Latest Pet
                val petQuerySnapshot = firestore.collection("petslist")
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()

                if (!petQuerySnapshot.isEmpty) {
                    // Sort locally to avoid needing a composite index in Firestore
                    val latestPet = petQuerySnapshot.documents.maxByOrNull {
                        it.getTimestamp("createdAt")?.seconds ?: 0L
                    }

                    if (latestPet != null) {
                        val pName = latestPet.getString("petName") ?: "Pet"
                        val pDesc = latestPet.getString("breed") ?: ""
                        val pImg = latestPet.getString("imageUrl") ?: ""
                        
                        _uiState.update {
                            it.copy(
                                userName = fetchedUserName,
                                petName = pName,
                                petDescription = pDesc,
                                petImageUrl = pImg,
                                hasPet = true,
                                isLoading = false
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                userName = fetchedUserName,
                                hasPet = false,
                                isLoading = false
                            )
                        }
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            userName = fetchedUserName,
                            hasPet = false,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                // Log the exception to see what's happening
                e.printStackTrace()
                // Handle error or fallback
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}
