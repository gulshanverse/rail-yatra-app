package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Person
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
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.RailGold
import com.example.ui.viewmodel.AuthState
import com.example.ui.viewmodel.RailViewModel
import com.example.ui.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val app = application as RailApplication
            val viewModel: RailViewModel by viewModels { ViewModelFactory(app) }
            val isDarkMode by viewModel.isDarkMode.collectAsState()

            MyApplicationTheme(darkTheme = isDarkMode) {
                val authState by viewModel.authState.collectAsState()

                Crossfade(
                    targetState = authState,
                    label = "AuthTransition"
                ) { state ->
                    when (state) {
                        AuthState.Splash -> SplashScreen(viewModel)
                        AuthState.Onboarding -> OnboardingScreen(viewModel)
                        AuthState.Login -> LoginScreen(viewModel)
                        AuthState.Register -> RegisterScreen(viewModel)
                        AuthState.ForgotPassword -> ForgotPasswordScreen(viewModel)
                        AuthState.LoggedIn -> MainAppWorkspace(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun MainAppWorkspace(viewModel: RailViewModel) {
    var activeTab by remember { mutableStateOf("home") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // Premium Bottom Navigation Bar respecting secure areas
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
            when (activeTab) {
                "home" -> DashboardScreen(viewModel = viewModel, onNavigateToSection = { activeTab = it })
                "search" -> TrainSearchScreen(viewModel = viewModel)
                "pnr" -> PnrScreen(viewModel = viewModel)
                "live" -> LiveStatusScreen(viewModel = viewModel)
                "chatbot" -> ChatbotScreen(viewModel = viewModel)
                "planner" -> JourneyPlannerScreen(viewModel = viewModel)
                "explorer" -> StationExplorerScreen()
                "profile" -> ProfileSettingsScreen(viewModel = viewModel, onNavigateToSection = { activeTab = it })
                "admin" -> AdminPanelScreen(viewModel = viewModel)
                else -> DashboardScreen(viewModel = viewModel, onNavigateToSection = { activeTab = it })
            }
        }
    }
}

@Composable
private fun IconColorDefault() = androidx.compose.material3.LocalContentColor.current.copy(alpha = 0.6f)

data class NavigationTab(
    val id: String,
    val label: String,
    val activeIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val inactiveIcon: androidx.compose.ui.graphics.vector.ImageVector
)

