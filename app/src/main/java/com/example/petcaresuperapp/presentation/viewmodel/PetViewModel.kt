package com.example.petcaresuperapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcaresuperapp.data.model.Pet
import com.example.petcaresuperapp.domain.usecase.GetAvailablePetsUseCase
import com.example.petcaresuperapp.domain.usecase.GetPetDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PetViewModel @Inject constructor(
    private val getAvailablePetsUseCase: GetAvailablePetsUseCase,
    private val getPetDetailsUseCase: GetPetDetailsUseCase
) : ViewModel() {

    private val _petsList = MutableStateFlow<List<Pet>>(emptyList())
    val petsList: StateFlow<List<Pet>> = _petsList.asStateFlow()

    private val _selectedPet = MutableStateFlow<Pet?>(null)
    val selectedPet: StateFlow<Pet?> = _selectedPet.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadAvailablePets()
    }

    private fun loadAvailablePets() {
        viewModelScope.launch {
            _isLoading.value = true
            getAvailablePetsUseCase().collect { pets ->
                _petsList.value = pets
                _isLoading.value = false
            }
        }
    }

    fun loadPetDetails(petId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _selectedPet.value = getPetDetailsUseCase(petId)
            _isLoading.value = false
        }
    }

    fun clearSelectedPet() {
        _selectedPet.value = null
    }
}
