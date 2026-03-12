package com.example.petcaresuperapp.presentation.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcaresuperapp.data.datastore.UserPreferences
import com.example.petcaresuperapp.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination: StateFlow<String?> = _startDestination

    init {
        viewModelScope.launch {
            combine(
                userPreferences.isOnboardingCompleted,
                userPreferences.isLoggedIn
            ) { onboardingCompleted, isLoggedIn ->
                when {
                    !onboardingCompleted -> Screen.Onboarding.route
                    !isLoggedIn -> Screen.Login.route
                    else -> Screen.Home.route
                }
            }.collect { destination ->
                _startDestination.value = destination
            }
        }
    }
}
