package com.example.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.database.Passenger
import com.example.core.database.RailRepository
import com.example.core.database.Ticket
import com.example.core.database.TrainInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SearchViewModel(private val railRepository: RailRepository) : ViewModel() {

    private val _searchSource = MutableStateFlow("NDLS (New Delhi)")
    val searchSource = _searchSource.asStateFlow()

    private val _searchDestination = MutableStateFlow("MMCT (Mumbai Central)")
    val searchDestination = _searchDestination.asStateFlow()

    private val _searchDate = MutableStateFlow("Tomorrow")
    val searchDate = _searchDate.asStateFlow()

    private val _searchClass = MutableStateFlow("3A")
    val searchClass = _searchClass.asStateFlow()

    private val _searchQuota = MutableStateFlow("General")
    val searchQuota = _searchQuota.asStateFlow()

    private val _searchPassengers = MutableStateFlow(1)
    val searchPassengers = _searchPassengers.asStateFlow()

    private val _searchResults = MutableStateFlow<List<TrainInfo>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    val passengers: StateFlow<List<Passenger>> = railRepository.allPassengers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchParams(
        source: String,
        destination: String,
        date: String,
        travelClass: String,
        quota: String,
        passengersCount: Int
    ) {
        _searchSource.value = source
        _searchDestination.value = destination
        _searchDate.value = date
        _searchClass.value = travelClass
        _searchQuota.value = quota
        _searchPassengers.value = passengersCount
    }

    fun executeTrainSearch() {
        _isSearching.value = true
        viewModelScope.launch {
            kotlinx.coroutines.delay(1000)
            _searchResults.value = railRepository.searchTrains(_searchSource.value, _searchDestination.value)
            _isSearching.value = false
        }
    }

    fun bookTicketSimulation(
        train: TrainInfo,
        selectedClass: String,
        passenger: Passenger,
        onPnrGenerated: (String) -> Unit
    ) {
        viewModelScope.launch {
            val pnrNum = (1000000000..9999999999).random().toString()
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, 3)
            val journeyDateStr = sdf.format(calendar.time)

            val newTicket = Ticket(
                pnr = pnrNum,
                trainNumber = train.number,
                trainName = train.name,
                sourceStation = train.source,
                destinationStation = train.destination,
                journeyDate = journeyDateStr,
                travelClass = selectedClass,
                quota = _searchQuota.value,
                passengerNames = passenger.name,
                coach = "A1",
                seatNumber = "12 (LB)",
                currentStatus = "CNF",
                bookingStatus = "CNF",
                chartStatus = "Chart Not Prepared",
                fare = train.fares[selectedClass] ?: 1200.0,
                confirmationProbability = 100,
                waitlistChance = 0,
                racChance = 0,
                aiAlternativeSuggested = "Your ticket is confirmed. Enjoy your travel with RailYatra!"
            )
            railRepository.insertTicket(newTicket)
            onPnrGenerated(pnrNum)
        }
    }
}
