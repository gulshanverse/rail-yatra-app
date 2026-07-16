package com.example.feature.pnr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.database.RailRepository
import com.example.core.database.Ticket
import com.example.core.network.GeminiRepository
import com.example.core.network.PnrPredictionResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PnrViewModel(
    private val railRepository: RailRepository,
    private val geminiRepository: GeminiRepository
) : ViewModel() {

    private val _pnrInput = MutableStateFlow("")
    val pnrInput = _pnrInput.asStateFlow()

    private val _pnrTicketResult = MutableStateFlow<Ticket?>(null)
    val pnrTicketResult = _pnrTicketResult.asStateFlow()

    private val _pnrPrediction = MutableStateFlow<PnrPredictionResult?>(null)
    val pnrPrediction = _pnrPrediction.asStateFlow()

    private val _isPnrLoading = MutableStateFlow(false)
    val isPnrLoading = _isPnrLoading.asStateFlow()

    val tickets: StateFlow<List<Ticket>> = railRepository.allTickets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun queryPnr(pnr: String) {
        if (pnr.length != 10) return
        _pnrInput.value = pnr
        _isPnrLoading.value = true
        _pnrPrediction.value = null
        _pnrTicketResult.value = null

        viewModelScope.launch {
            val localTicket = railRepository.getTicketByPnr(pnr)
            if (localTicket != null) {
                _pnrTicketResult.value = localTicket
                val prediction = geminiRepository.predictPnrConfirmation(
                    pnr = localTicket.pnr,
                    trainNumber = localTicket.trainNumber,
                    trainName = localTicket.trainName,
                    currentStatus = localTicket.currentStatus,
                    journeyDate = localTicket.journeyDate
                )
                _pnrPrediction.value = prediction
            } else {
                // If not in database, create a simulated dynamic ticket
                val ticket = Ticket(
                    pnr = pnr,
                    trainNumber = "12951",
                    trainName = "Mumbai Rajdhani",
                    sourceStation = "NDLS (New Delhi)",
                    destinationStation = "MMCT (Mumbai Central)",
                    journeyDate = "22 Jul 2026",
                    travelClass = "3A",
                    quota = "General",
                    passengerNames = "Spoken Guest",
                    coach = "B2",
                    seatNumber = "WL/12",
                    currentStatus = "WL/8",
                    bookingStatus = "WL/12",
                    chartStatus = "Chart Not Prepared",
                    fare = 2100.0
                )
                _pnrTicketResult.value = ticket
                val prediction = geminiRepository.predictPnrConfirmation(
                    pnr = ticket.pnr,
                    trainNumber = ticket.trainNumber,
                    trainName = ticket.trainName,
                    currentStatus = ticket.currentStatus,
                    journeyDate = ticket.journeyDate
                )
                _pnrPrediction.value = prediction
            }
            _isPnrLoading.value = false
        }
    }
}
