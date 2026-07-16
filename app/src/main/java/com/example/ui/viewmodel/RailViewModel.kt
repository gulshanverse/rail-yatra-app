package com.example.ui.viewmodel

import android.app.Application
import android.speech.RecognizerIntent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.RailApplication
import com.example.data.model.FavoriteRoute
import com.example.data.model.Passenger
import com.example.data.model.Ticket
import com.example.data.repository.ChatMessage
import com.example.data.repository.GeminiRepository
import com.example.data.repository.JourneyItinerary
import com.example.data.repository.LiveStatus
import com.example.data.repository.PnrPredictionResult
import com.example.data.repository.RailRepository
import com.example.data.repository.TrainInfo
import com.example.data.repository.VoiceAction
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RailViewModel(
    private val app: RailApplication,
    private val railRepository: RailRepository,
    private val geminiRepository: GeminiRepository
) : AndroidViewModel(app) {

    // --- Authentication & Flow Navigation ---
    private val _authState = MutableStateFlow<AuthState>(AuthState.Splash)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // --- Core Repository Flows ---
    val tickets: StateFlow<List<Ticket>> = railRepository.allTickets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val passengers: StateFlow<List<Passenger>> = railRepository.allPassengers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteRoutes: StateFlow<List<FavoriteRoute>> = railRepository.allFavoriteRoutes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Train Search States ---
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

    // --- PNR Smart Prediction States ---
    private val _pnrInput = MutableStateFlow("")
    val pnrInput = _pnrInput.asStateFlow()

    private val _pnrTicketResult = MutableStateFlow<Ticket?>(null)
    val pnrTicketResult = _pnrTicketResult.asStateFlow()

    private val _pnrPrediction = MutableStateFlow<PnrPredictionResult?>(null)
    val pnrPrediction = _pnrPrediction.asStateFlow()

    private val _isPnrLoading = MutableStateFlow(false)
    val isPnrLoading = _isPnrLoading.asStateFlow()

    // --- Live Train Status States ---
    private val _liveTrainNumber = MutableStateFlow("12951")
    val liveTrainNumber = _liveTrainNumber.asStateFlow()

    private val _liveStatusResult = MutableStateFlow<LiveStatus?>(null)
    val liveStatusResult = _liveStatusResult.asStateFlow()

    private val _isLiveLoading = MutableStateFlow(false)
    val isLiveLoading = _isLiveLoading.asStateFlow()

    // --- AI Chatbot & Voice Assistant ---
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(ChatMessage("AI", "Namaste! I am Yatri AI, your premium RailYatra assistant. How can I help you make your journey smoother today?"))
    )
    val chatMessages = _chatMessages.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading = _isChatLoading.asStateFlow()

    private val _speechText = MutableStateFlow("")
    val speechText = _speechText.asStateFlow()

    // --- Smart Journey Planner ---
    private val _itineraryResult = MutableStateFlow<JourneyItinerary?>(null)
    val itineraryResult = _itineraryResult.asStateFlow()

    private val _isItineraryLoading = MutableStateFlow(false)
    val isItineraryLoading = _isItineraryLoading.asStateFlow()

    // --- Settings & UI Customization ---
    private val _isDarkMode = MutableStateFlow(true) // Premium dark by default
    val isDarkMode = _isDarkMode.asStateFlow()

    private val _selectedLanguage = MutableStateFlow("English")
    val selectedLanguage = _selectedLanguage.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled = _notificationsEnabled.asStateFlow()

    // --- Admin Panel States ---
    private val _adminNotificationTitle = MutableStateFlow("")
    val adminNotificationTitle = _adminNotificationTitle.asStateFlow()

    private val _adminNotificationMessage = MutableStateFlow("")
    val adminNotificationMessage = _adminNotificationMessage.asStateFlow()

    private val _travelAlerts = MutableStateFlow(
        listOf(
            "⚠️ Alert: Due to maintenance, Platform 2 at NDLS is shifted to Platform 4 for Shatabdi trains.",
            "🌧️ Weather update: Heavy rains in Mumbai may cause 10-15 mins delay in Western Railway services."
        )
    )
    val travelAlerts = _travelAlerts.asStateFlow()

    // --- Navigation Flow Helper Actions ---
    fun setAuthState(state: AuthState) {
        _authState.value = state
    }

    fun completeSplash() {
        viewModelScope.launch {
            // Simulated Splash delay
            _authState.value = AuthState.Onboarding
        }
    }

    fun completeOnboarding() {
        _authState.value = AuthState.Login
    }

    fun loginUser(email: String, name: String) {
        _currentUser.value = User(name = name, email = email)
        _authState.value = AuthState.LoggedIn
    }

    fun logoutUser() {
        _currentUser.value = null
        _authState.value = AuthState.Login
    }

    // --- Search Train Operations ---
    fun updateSearchParams(source: String, destination: String, date: String, travelClass: String, quota: String, passengers: Int) {
        _searchSource.value = source
        _searchDestination.value = destination
        _searchDate.value = date
        _searchClass.value = travelClass
        _searchQuota.value = quota
        _searchPassengers.value = passengers
    }

    fun executeTrainSearch() {
        _isSearching.value = true
        viewModelScope.launch {
            // Simulated search delay
            kotlinx.coroutines.delay(1000)
            _searchResults.value = railRepository.searchTrains(_searchSource.value, _searchDestination.value)
            _isSearching.value = false
        }
    }

    // --- PNR SMART PREDICTION API ---
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

    // --- LIVE TRAIN TRACKING STATUS ---
    fun queryLiveStatus(trainNumber: String) {
        _liveTrainNumber.value = trainNumber
        _isLiveLoading.value = true
        viewModelScope.launch {
            kotlinx.coroutines.delay(800)
            _liveStatusResult.value = railRepository.getLiveStatus(trainNumber)
            _isLiveLoading.value = false
        }
    }

    // --- AI CHATBOT INTERACTION ---
    fun sendChatMessage(msg: String) {
        if (msg.trim().isEmpty()) return
        val userMsg = ChatMessage("User", msg)
        _chatMessages.value = _chatMessages.value + userMsg
        _isChatLoading.value = true

        viewModelScope.launch {
            val aiResponseText = geminiRepository.getChatbotResponse(msg, _chatMessages.value)
            _chatMessages.value = _chatMessages.value + ChatMessage("AI", aiResponseText)
            _isChatLoading.value = false
        }
    }

    fun clearChat() {
        _chatMessages.value = listOf(ChatMessage("AI", "Namaste! I am Yatri AI, your premium RailYatra assistant. How can I help you make your journey smoother today?"))
    }

    // --- SMART VOICE RECOGNITION ASSISTANT ---
    fun processVoiceInput(text: String) {
        _speechText.value = text
        viewModelScope.launch {
            val action = geminiRepository.parseVoiceCommand(text)
            handleVoiceAction(action)
        }
    }

    private fun handleVoiceAction(action: VoiceAction) {
        when (action.action) {
            "SEARCH_TRAINS" -> {
                if (action.source.isNotEmpty() && action.destination.isNotEmpty()) {
                    _searchSource.value = action.source
                    _searchDestination.value = action.destination
                    executeTrainSearch()
                }
            }
            "CHECK_PNR" -> {
                if (action.pnr.isNotEmpty()) {
                    queryPnr(action.pnr)
                }
            }
            "LIVE_STATUS" -> {
                if (action.trainNumber.isNotEmpty()) {
                    queryLiveStatus(action.trainNumber)
                }
            }
            else -> {
                sendChatMessage(action.source.ifEmpty { "Provide railway guide" })
            }
        }
    }

    // --- SMART JOURNEY PLANNER ---
    fun generateJourneyItinerary(source: String, destination: String, date: String) {
        _isItineraryLoading.value = true
        _itineraryResult.value = null
        viewModelScope.launch {
            val plan = geminiRepository.generateJourneyPlanner(source, destination, date)
            _itineraryResult.value = plan
            _isItineraryLoading.value = false
        }
    }

    // --- TICKET BOOKING SIMULATION ---
    fun bookTicketSimulation(train: TrainInfo, selectedClass: String, passenger: Passenger) {
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
            // Automatically select this ticket for displaying PNR status
            queryPnr(pnrNum)
        }
    }

    // --- PASSENGER PROFILE MANAGEMENT ---
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

    // --- SETTINGS OPERATIONS ---
    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun changeLanguage(language: String) {
        _selectedLanguage.value = language
    }

    fun toggleNotifications() {
        _notificationsEnabled.value = !_notificationsEnabled.value
    }

    // --- ADMIN PANEL CONTROLS ---
    fun sendAdminAnnouncement(title: String, message: String) {
        if (title.isEmpty() || message.isEmpty()) return
        _adminNotificationTitle.value = title
        _adminNotificationMessage.value = message
        _travelAlerts.value = _travelAlerts.value + "📢 Announcement: $title - $message"
    }
}

// --- Auth States representation ---
sealed interface AuthState {
    object Splash : AuthState
    object Onboarding : AuthState
    object Login : AuthState
    object Register : AuthState
    object ForgotPassword : AuthState
    object LoggedIn : AuthState
}

// --- Simple User profile ---
data class User(
    val name: String,
    val email: String
)

// --- Factory for providing Dependencies ---
class ViewModelFactory(private val application: RailApplication) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RailViewModel(
                app = application,
                railRepository = application.railRepository,
                geminiRepository = application.geminiRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
