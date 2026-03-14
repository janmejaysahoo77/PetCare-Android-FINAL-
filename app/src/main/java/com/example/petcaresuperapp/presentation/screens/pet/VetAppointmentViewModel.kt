package com.example.petcaresuperapp.presentation.screens.pet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcaresuperapp.data.model.VetAppointment
import com.example.petcaresuperapp.domain.models.Doctor
import com.example.petcaresuperapp.domain.repository.VetAppointmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VetAppointmentViewModel @Inject constructor(
    private val repository: VetAppointmentRepository
) : ViewModel() {

    private val _doctors = MutableStateFlow<List<Doctor>>(emptyList())
    val doctors: StateFlow<List<Doctor>> = _doctors.asStateFlow()

    private val _bookingState = MutableStateFlow<BookingState>(BookingState.Idle)
    val bookingState: StateFlow<BookingState> = _bookingState.asStateFlow()

    init {
        fetchDoctors()
    }

    private fun fetchDoctors() {
        viewModelScope.launch {
            repository.getDoctors().collectLatest {
                _doctors.value = it
            }
        }
    }

    fun bookAppointment(appointment: VetAppointment) {
        viewModelScope.launch {
            _bookingState.value = BookingState.Loading
            val result = repository.createAppointment(appointment)
            if (result.isSuccess) {
                _bookingState.value = BookingState.Success(result.getOrNull() ?: "")
            } else {
                _bookingState.value = BookingState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun resetState() {
        _bookingState.value = BookingState.Idle
    }
}

sealed class BookingState {
    object Idle : BookingState()
    object Loading : BookingState()
    data class Success(val appointmentId: String) : BookingState()
    data class Error(val message: String) : BookingState()
}
