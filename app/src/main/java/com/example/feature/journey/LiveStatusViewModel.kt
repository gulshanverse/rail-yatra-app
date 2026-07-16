package com.example.feature.journey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.database.LiveStatus
import com.example.core.database.RailRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LiveStatusViewModel(
    private val railRepository: RailRepository
) : ViewModel() {

    private val _liveTrainNumber = MutableStateFlow("12951")
    val liveTrainNumber = _liveTrainNumber.asStateFlow()

    private val _liveStatusResult = MutableStateFlow<LiveStatus?>(null)
    val liveStatusResult = _liveStatusResult.asStateFlow()

    private val _isLiveLoading = MutableStateFlow(false)
    val isLiveLoading = _isLiveLoading.asStateFlow()

    fun queryLiveStatus(trainNumber: String) {
        _liveTrainNumber.value = trainNumber
        _isLiveLoading.value = true
        viewModelScope.launch {
            kotlinx.coroutines.delay(800)
            _liveStatusResult.value = railRepository.getLiveStatus(trainNumber)
            _isLiveLoading.value = false
        }
    }
}
