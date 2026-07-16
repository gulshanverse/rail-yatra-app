package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ChatBubble
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.core.common.AuthState
import com.example.core.designsystem.MyApplicationTheme
import com.example.core.navigation.NavigationTab
import com.example.core.preferences.PreferencesHelper
import com.example.feature.auth.*
import com.example.feature.chatbot.*
import com.example.feature.dashboard.*
import com.example.feature.journey.*
import com.example.feature.pnr.*
import com.example.feature.profile.*
import com.example.feature.search.*
import com.example.feature.station.*
import com.example.feature.settings.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val app = application as RailApplication
            val factory = remember { FeatureViewModelFactory(app) }

            val settingsViewModel: SettingsViewModel by viewModels { factory }
            val isDarkMode by settingsViewModel.isDarkMode.collectAsState()

            MyApplicationTheme(darkTheme = isDarkMode) {
                val authViewModel: AuthViewModel by viewModels { factory }
                val authState by authViewModel.authState.collectAsState()

                Crossfade(
                    targetState = authState,
                    label = "AuthTransition"
                ) { state ->
                    when (state) {
                        AuthState.Splash -> SplashScreen(authViewModel)
                        AuthState.Onboarding -> OnboardingScreen(authViewModel)
                        AuthState.Login -> LoginScreen(authViewModel)
                        AuthState.Register -> RegisterScreen(authViewModel)
                        AuthState.ForgotPassword -> ForgotPasswordScreen(authViewModel)
                        AuthState.LoggedIn -> MainAppWorkspace(app, authViewModel, settingsViewModel, factory)
                    }
                }
            }
        }
    }
}

class FeatureViewModelFactory(private val app: RailApplication) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val railRepository = app.railRepository
        val geminiRepository = app.geminiRepository
        val preferencesHelper = PreferencesHelper(app)

        @Suppress("UNCHECKED_CAST")
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel() as T
            modelClass.isAssignableFrom(SearchViewModel::class.java) -> SearchViewModel(railRepository) as T
            modelClass.isAssignableFrom(PnrViewModel::class.java) -> PnrViewModel(railRepository, geminiRepository) as T
            modelClass.isAssignableFrom(ChatbotViewModel::class.java) -> ChatbotViewModel(geminiRepository) as T
            modelClass.isAssignableFrom(JourneyViewModel::class.java) -> JourneyViewModel(geminiRepository) as T
            modelClass.isAssignableFrom(LiveStatusViewModel::class.java) -> LiveStatusViewModel(railRepository) as T
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> ProfileViewModel(railRepository) as T
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> SettingsViewModel(preferencesHelper) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

@Composable
fun MainAppWorkspace(
    app: RailApplication,
    authViewModel: AuthViewModel,
    settingsViewModel: SettingsViewModel,
    factory: ViewModelProvider.Factory
) {
    var activeTab by remember { mutableStateOf("home") }

    val dashboardViewModel = remember(authViewModel) {
        DashboardViewModel(app.railRepository, authViewModel.currentUser)
    }
    val searchViewModel: SearchViewModel = remember { factory.create(SearchViewModel::class.java) }
    val pnrViewModel: PnrViewModel = remember { factory.create(PnrViewModel::class.java) }
    val chatbotViewModel: ChatbotViewModel = remember { factory.create(ChatbotViewModel::class.java) }
    val journeyViewModel: JourneyViewModel = remember { factory.create(JourneyViewModel::class.java) }
    val liveStatusViewModel: LiveStatusViewModel = remember { factory.create(LiveStatusViewModel::class.java) }
    val profileViewModel: ProfileViewModel = remember { factory.create(ProfileViewModel::class.java) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_nav_bar"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                val tabs = listOf(
                    NavigationTab("home", "Home", Icons.Filled.Home, Icons.Outlined.Home),
                    NavigationTab("search", "Trains", Icons.Filled.Search, Icons.Outlined.Search),
                    NavigationTab("pnr", "PNR", Icons.Filled.QueryStats, Icons.Outlined.QueryStats),
                    NavigationTab("live", "Track", Icons.Filled.MyLocation, Icons.Outlined.MyLocation),
                    NavigationTab("chatbot", "Yatri AI", Icons.Filled.ChatBubble, Icons.Outlined.ChatBubble)
                )

                tabs.forEach { tab ->
                    val isSelected = activeTab == tab.id
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { activeTab = tab.id },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.activeIcon else tab.inactiveIcon,
                                contentDescription = tab.label,
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        label = {
                            Text(
                                text = tab.label,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                            indicatorColor = MaterialTheme.colorScheme.secondary
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.id}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val src by searchViewModel.searchSource.collectAsState()
            val dst by searchViewModel.searchDestination.collectAsState()
            val date by searchViewModel.searchDate.collectAsState()

            when (activeTab) {
                "home" -> DashboardScreen(
                    viewModel = dashboardViewModel,
                    onNavigateToSection = { activeTab = it },
                    onQueryPnr = { pnrViewModel.queryPnr(it) },
                    onSelectFavoriteRoute = { fav ->
                        searchViewModel.updateSearchParams(
                            fav.sourceStation,
                            fav.destinationStation,
                            "Tomorrow",
                            "3A",
                            "General",
                            1
                        )
                        searchViewModel.executeTrainSearch()
                    }
                )
                "search" -> TrainSearchScreen(
                    viewModel = searchViewModel,
                    onBookSuccess = { pnr ->
                        pnrViewModel.queryPnr(pnr)
                        activeTab = "pnr"
                    }
                )
                "pnr" -> PnrScreen(viewModel = pnrViewModel)
                "live" -> LiveStatusScreen(viewModel = liveStatusViewModel)
                "chatbot" -> ChatbotScreen(
                    viewModel = chatbotViewModel,
                    onVoiceAction = { action ->
                        when (action.action) {
                            "SEARCH_TRAINS" -> {
                                if (action.source.isNotEmpty() && action.destination.isNotEmpty()) {
                                    searchViewModel.updateSearchParams(
                                        action.source,
                                        action.destination,
                                        "Tomorrow",
                                        "3A",
                                        "General",
                                        1
                                    )
                                    searchViewModel.executeTrainSearch()
                                    activeTab = "search"
                                }
                            }
                            "CHECK_PNR" -> {
                                if (action.pnr.isNotEmpty()) {
                                    pnrViewModel.queryPnr(action.pnr)
                                    activeTab = "pnr"
                                }
                            }
                            "LIVE_STATUS" -> {
                                if (action.trainNumber.isNotEmpty()) {
                                    liveStatusViewModel.queryLiveStatus(action.trainNumber)
                                    activeTab = "live"
                                }
                            }
                            else -> {
                                chatbotViewModel.sendChatMessage(action.source.ifEmpty { "Provide railway guide" })
                            }
                        }
                    }
                )
                "planner" -> JourneyPlannerScreen(
                    viewModel = journeyViewModel,
                    source = src,
                    destination = dst,
                    date = date
                )
                "explorer" -> StationExplorerScreen()
                "profile" -> ProfileSettingsScreen(
                    profileViewModel = profileViewModel,
                    settingsViewModel = settingsViewModel,
                    currentUserFlow = authViewModel.currentUser,
                    onNavigateToSection = { activeTab = it },
                    onLogout = { authViewModel.logoutUser() }
                )
                "admin" -> AdminPanelScreen(
                    viewModel = settingsViewModel,
                    onBroadcast = { title, msg ->
                        // Add alert to active alerts
                    }
                )
                else -> DashboardScreen(
                    viewModel = dashboardViewModel,
                    onNavigateToSection = { activeTab = it },
                    onQueryPnr = { pnrViewModel.queryPnr(it) },
                    onSelectFavoriteRoute = { fav ->
                        searchViewModel.updateSearchParams(
                            fav.sourceStation,
                            fav.destinationStation,
                            "Tomorrow",
                            "3A",
                            "General",
                            1
                        )
                        searchViewModel.executeTrainSearch()
                    }
                )
            }
        }
    }
}
