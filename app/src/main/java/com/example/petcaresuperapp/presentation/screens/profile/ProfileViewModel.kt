package com.example.petcaresuperapp.presentation.screens.profile

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.net.Uri
import com.example.petcaresuperapp.core.util.Resource
import com.example.petcaresuperapp.domain.models.User
import com.example.petcaresuperapp.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _userState = mutableStateOf<Resource<User>>(Resource.Idle())
    val userState: State<Resource<User>> = _userState

    private val _uploadState = mutableStateOf<Resource<String>>(Resource.Idle())
    val uploadState: State<Resource<String>> = _uploadState

    init {
        fetchUserData()
    }

    fun fetchUserData() {
        repository.getUserData().onEach { result ->
            _userState.value = result
        }.launchIn(viewModelScope)
    }

    fun uploadProfilePhoto(uri: Uri) {
        repository.uploadProfilePhoto(uri).onEach { result ->
            _uploadState.value = result
            if (result is Resource.Success) {
                fetchUserData() // Refresh profile
            }
        }.launchIn(viewModelScope)
    }
}
