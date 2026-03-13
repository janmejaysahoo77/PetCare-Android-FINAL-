package com.example.petcaresuperapp.presentation.screens.pet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcaresuperapp.data.model.MedicalRecord
import com.example.petcaresuperapp.domain.repository.MedicalRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.petcaresuperapp.domain.repository.PetRepository

@HiltViewModel
class HealthViewModel @Inject constructor(
    private val repository: MedicalRecordRepository,
    private val petRepository: PetRepository
) : ViewModel() {

    private val _recordsState = MutableStateFlow<RecordsState>(RecordsState.Idle)
    val recordsState: StateFlow<RecordsState> = _recordsState.asStateFlow()

    private val _myPetId = MutableStateFlow<String?>(null)
    val myPetId: StateFlow<String?> = _myPetId.asStateFlow()

    init {
        loadMyPetId()
    }

    private fun loadMyPetId() {
        viewModelScope.launch {
            petRepository.getAvailablePets().collect { pets ->
                if (pets.isNotEmpty()) {
                    _myPetId.value = pets.first().petIdInternal
                }
            }
        }
    }

    fun fetchMedicalRecords(petId: String) {
        viewModelScope.launch {
            _recordsState.value = RecordsState.Loading
            val result = repository.getMedicalRecordsForPet(petId)
            
            if (result.isSuccess) {
                val records = result.getOrNull() ?: emptyList()
                if (records.isEmpty()) {
                    _recordsState.value = RecordsState.Empty
                } else {
                    _recordsState.value = RecordsState.Success(records)
                }
            } else {
                _recordsState.value = RecordsState.Error("Unable to fetch pet health data. Please try again.")
            }
        }
    }

    fun resetState() {
        _recordsState.value = RecordsState.Idle
    }
}

sealed class RecordsState {
    object Idle : RecordsState()
    object Loading : RecordsState()
    object Empty : RecordsState()
    data class Success(val records: List<MedicalRecord>) : RecordsState()
    data class Error(val message: String) : RecordsState()
}
