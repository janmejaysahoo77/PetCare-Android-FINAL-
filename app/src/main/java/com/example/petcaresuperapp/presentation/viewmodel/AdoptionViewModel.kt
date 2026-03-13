package com.example.petcaresuperapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcaresuperapp.data.model.AdoptionRequest
import com.example.petcaresuperapp.domain.usecase.GetUserAdoptionRequestsUseCase
import com.example.petcaresuperapp.domain.usecase.SendAdoptionRequestUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdoptionViewModel @Inject constructor(
    private val sendAdoptionRequestUseCase: SendAdoptionRequestUseCase,
    private val getUserAdoptionRequestsUseCase: GetUserAdoptionRequestsUseCase,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _userRequests = MutableStateFlow<List<AdoptionRequest>>(emptyList())
    val userRequests: StateFlow<List<AdoptionRequest>> = _userRequests.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _submissionStatus = MutableStateFlow<Result<Unit>?>(null)
    val submissionStatus: StateFlow<Result<Unit>?> = _submissionStatus.asStateFlow()

    fun loadUserRequests() {
        val currentUser = firebaseAuth.currentUser ?: return
        viewModelScope.launch {
            _isLoading.value = true
            getUserAdoptionRequestsUseCase(currentUser.uid).collect { requests ->
                _userRequests.value = requests
                _isLoading.value = false
            }
        }
    }

    fun submitAdoptionRequest(
        petId: String,
        petName: String,
        contactInfo: String,
        message: String
    ) {
        val currentUser = firebaseAuth.currentUser ?: return
        
        viewModelScope.launch {
            _isLoading.value = true
            _submissionStatus.value = null

            val request = AdoptionRequest(
                petId = petId,
                petName = petName,
                userId = currentUser.uid,
                userName = currentUser.displayName ?: currentUser.email ?: "Unknown User",
                contactInfo = contactInfo,
                message = message
            )

            val result = sendAdoptionRequestUseCase(request)
            _submissionStatus.value = result
            _isLoading.value = false
        }
    }
    
    fun resetSubmissionStatus() {
        _submissionStatus.value = null
    }
}
