package com.example.petcaresuperapp.presentation.screens.lostfound

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcaresuperapp.data.model.ContactRequest
import com.example.petcaresuperapp.domain.repository.ContactRequestRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactRequestViewModel @Inject constructor(
    private val repository: ContactRequestRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _incomingRequests = MutableStateFlow<List<ContactRequest>>(emptyList())
    val incomingRequests: StateFlow<List<ContactRequest>> = _incomingRequests.asStateFlow()

    private val _requestSentStatus = MutableSharedFlow<Result<Unit>>()
    val requestSentStatus = _requestSentStatus.asSharedFlow()

    private val _isRequestAccepted = MutableStateFlow(false)
    val isRequestAccepted: StateFlow<Boolean> = _isRequestAccepted.asStateFlow()

    private val _isRequestPending = MutableStateFlow(false)
    val isRequestPending: StateFlow<Boolean> = _isRequestPending.asStateFlow()

    fun sendContactRequest(postId: String, petName: String, receiverId: String) {
        viewModelScope.launch {
            val result = repository.sendContactRequest(postId, petName, receiverId)
            _requestSentStatus.emit(result)
        }
    }

    fun observeIncomingRequests() {
        val userId = auth.currentUser?.uid ?: return
        repository.listenForUserRequests(userId)
            .onEach { requests ->
                _incomingRequests.value = requests
            }
            .launchIn(viewModelScope)
    }

    fun updateRequestStatus(requestId: String, status: String) {
        viewModelScope.launch {
            repository.updateRequestStatus(requestId, status)
        }
    }

    fun checkIfRequestApproved(postId: String) {
        val senderId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _isRequestAccepted.value = repository.checkIfRequestAccepted(postId, senderId)
            _isRequestPending.value = repository.checkIfRequestPending(postId, senderId)
        }
    }
}
