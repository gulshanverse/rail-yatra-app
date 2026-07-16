package com.example.feature.journey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.network.GeminiRepository
import com.example.core.network.JourneyItinerary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class JourneyViewModel(
    private val geminiRepository: GeminiRepository
) : ViewModel() {

    private val _itineraryResult = MutableStateFlow<JourneyItinerary?>(null)
    val itineraryResult = _itineraryResult.asStateFlow()

    private val _isItineraryLoading = MutableStateFlow(false)
    val isItineraryLoading = _isItineraryLoading.asStateFlow()

    fun generateJourneyItinerary(source: String, destination: String, date: String) {
        _isItineraryLoading.value = true
        _itineraryResult.value = null
        viewModelScope.launch {
            val plan = geminiRepository.generateJourneyPlanner(source, destination, date)
            _itineraryResult.value = plan
            _isItineraryLoading.value = false
        }
    }
}
