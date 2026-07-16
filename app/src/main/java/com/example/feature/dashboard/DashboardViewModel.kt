package com.example.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.common.User
import com.example.core.database.FavoriteRoute
import com.example.core.database.RailRepository
import com.example.core.database.Ticket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn

class DashboardViewModel(
    private val railRepository: RailRepository,
    val currentUser: StateFlow<User?>
) : ViewModel() {

    val tickets: StateFlow<List<Ticket>> = railRepository.allTickets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteRoutes: StateFlow<List<FavoriteRoute>> = railRepository.allFavoriteRoutes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _travelAlerts = MutableStateFlow(listOf(
        "Weather alert: Heavy rain in Mumbai region. Some trains running 15-20 mins late.",
        "Festive season rush: Extra coach attached to Hazrat Nizamuddin Rajdhani (12953).",
        "Technical upgrade: IRCTC portal maintenance scheduled from 11:45 PM to 12:30 AM today."
    ))
    val travelAlerts: StateFlow<List<String>> = _travelAlerts.asStateFlow()
}
