package com.example.petcaresuperapp.presentation.screens.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcaresuperapp.core.util.Resource
import com.example.petcaresuperapp.data.datastore.UserPreferences
import com.example.petcaresuperapp.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _loginState = mutableStateOf<Resource<Boolean>>(Resource.Idle())
    val loginState: State<Resource<Boolean>> = _loginState

    private val _signUpState = mutableStateOf<Resource<Boolean>>(Resource.Idle())
    val signUpState: State<Resource<Boolean>> = _signUpState

    fun login(email: String, password: String) {
        repository.login(email, password).onEach { result ->
            _loginState.value = result
            if (result is Resource.Success) {
                userPreferences.saveLoggedIn(true)
            }
        }.launchIn(viewModelScope)
    }

    fun signUp(name: String, email: String, password: String) {
        repository.signUp(name, email, password).onEach { result ->
            _signUpState.value = result
            if (result is Resource.Success) {
                userPreferences.saveLoggedIn(true)
            }
        }.launchIn(viewModelScope)
    }

    fun resetLoginState() {
        _loginState.value = Resource.Idle()
    }

    fun resetSignUpState() {
        _signUpState.value = Resource.Idle()
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            userPreferences.saveOnboardingCompleted(true)
        }
    }
}
