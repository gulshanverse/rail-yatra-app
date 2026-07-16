package com.example.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.database.Passenger
import com.example.core.database.RailRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val railRepository: RailRepository
) : ViewModel() {

    val passengersList: StateFlow<List<Passenger>> = railRepository.allPassengers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun savePassengerProfile(name: String, age: Int, gender: String, berth: String) {
        viewModelScope.launch {
            val newPassenger = Passenger(name = name, age = age, gender = gender, preferredBerth = berth)
            railRepository.insertPassenger(newPassenger)
        }
    }

    fun removePassengerProfile(passenger: Passenger) {
        viewModelScope.launch {
            railRepository.deletePassenger(passenger)
        }
    }
}
