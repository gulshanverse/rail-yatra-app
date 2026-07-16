package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FavoriteRoute
import com.example.data.model.Passenger
import com.example.data.model.Ticket
import com.example.data.repository.ChatMessage
import com.example.data.repository.LiveStatus
import com.example.data.repository.PnrPredictionResult
import com.example.data.repository.TrainInfo
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.InteractiveTrackMap
import com.example.ui.components.ShimmerCard
import com.example.ui.components.premiumBackgroundBrush
import com.example.ui.theme.*
import com.example.ui.viewmodel.AuthState
import com.example.ui.viewmodel.RailViewModel
import com.example.ui.viewmodel.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ==========================================
// 1. SPLASH SCREEN
// ==========================================
@Composable
fun SplashScreen(viewModel: RailViewModel) {
    val scale = remember { Animatable(0f) }
    val rotation = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        rotation.animateTo(
            targetValue = 360f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
        delay(1200)
        viewModel.completeSplash()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(RailBlueDark, DarkBackground)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(RailGold.copy(alpha = 0.15f))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Train,
                    contentDescription = "RailYatra Logo",
                    tint = RailGold,
                    modifier = Modifier
                        .size(80.dp)
                        .testTag("splash_logo")
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "RailYatra",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = RailGold,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "AI-Powered Smart Indian Railways Assistant",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}

// ==========================================
// 2. ONBOARDING SCREEN
// ==========================================
@Composable
fun OnboardingScreen(viewModel: RailViewModel) {
    var step by remember { mutableStateOf(0) }

    val onboardTitles = listOf(
        "AI confirmation prediction",
        "Live Track & Platform locator",
        "Speech Assisted Yatri AI"
    )
    val onboardDescs = listOf(
        "Predict confirmation probability for your waitlist tickets with high accuracy using Gemini AI.",
        "Track exact real-time delay, platform numbers, speeds and live station routes.",
        "Query refund rules, Tatkal, lost luggage, and local e-catering menus naturally by voice."
    )
    val onboardIcons = listOf(
        Icons.Filled.AutoAwesome,
        Icons.Filled.MyLocation,
        Icons.Filled.Mic
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(premiumBackgroundBrush())
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Skip button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = { viewModel.completeOnboarding() },
                    modifier = Modifier.testTag("skip_button")
                ) {
                    Text("Skip", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                }
            }

            // Visual Center Illustration
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = onboardIcons[step],
                        contentDescription = onboardTitles[step],
                        tint = RailOrangeAccent,
                        modifier = Modifier.size(80.dp)
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = onboardTitles[step],
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = onboardDescs[step],
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Indicators and buttons
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Steps indicator dot
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    repeat(3) { index ->
                        val color = if (index == step) RailOrangeAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                    }
                }

                Button(
                    onClick = {
                        if (step < 2) {
                            step++
                        } else {
                            viewModel.completeOnboarding()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("next_onboarding"),
                    colors = ButtonDefaults.buttonColors(containerColor = RailBluePrimary, contentColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(if (step == 2) "Get Started" else "Next", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ==========================================
// 3. AUTHENTICATION (LOGIN, REGISTER, ETC)
// ==========================================
@Composable
fun LoginScreen(viewModel: RailViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(premiumBackgroundBrush())
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome to RailYatra",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Sign in to book smarter and travel easier",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_email"),
                shape = RoundedCornerShape(16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Lock") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_password"),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { viewModel.setAuthState(AuthState.ForgotPassword) }) {
                    Text("Forgot Password?", color = RailOrangeAccent)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        viewModel.loginUser(email, email.substringBefore("@"))
                    } else {
                        Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("login_submit"),
                colors = ButtonDefaults.buttonColors(containerColor = RailBluePrimary, contentColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Sign In", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
            // Google Sign In Mock
            OutlinedButton(
                onClick = {
                    scope.launch {
                        Toast.makeText(context, "Signing in with Google...", Toast.LENGTH_SHORT).show()
                        delay(500)
                        viewModel.loginUser("guest@gmail.com", "Yatri Guest")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("google_signin"),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.AccountCircle, contentDescription = "Google Icon", tint = RailOrangeAccent)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign In with Google", color = MaterialTheme.colorScheme.onBackground)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Don't have an account?")
                TextButton(onClick = { viewModel.setAuthState(AuthState.Register) }) {
                    Text("Register Now", color = RailOrangeAccent)
                }
            }
        }
    }
}

@Composable
fun RegisterScreen(viewModel: RailViewModel) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(premiumBackgroundBrush())
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Create Account",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Unlock AI predictions and easy bookings",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Lock") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                        viewModel.loginUser(email, name)
                    } else {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("register_submit"),
                colors = ButtonDefaults.buttonColors(containerColor = RailBluePrimary, contentColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Register Now", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Already registered?")
                TextButton(onClick = { viewModel.setAuthState(AuthState.Login) }) {
                    Text("Sign In", color = RailOrangeAccent)
                }
            }
        }
    }
}

@Composable
fun ForgotPasswordScreen(viewModel: RailViewModel) {
    var email by remember { mutableStateOf("") }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(premiumBackgroundBrush())
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Forgot Password",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Enter your email to receive a password reset code",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    if (email.isNotEmpty()) {
                        Toast.makeText(context, "Verification email sent successfully!", Toast.LENGTH_LONG).show()
                        viewModel.setAuthState(AuthState.Login)
                    } else {
                        Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RailBluePrimary, contentColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Send Verification Link", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
            TextButton(
                onClick = { viewModel.setAuthState(AuthState.Login) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Back to Login", color = RailOrangeAccent)
            }
        }
    }
}

// ==========================================
// 4. HOME DASHBOARD
// ==========================================
@Composable
fun DashboardScreen(
    viewModel: RailViewModel,
    onNavigateToSection: (String) -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val tickets by viewModel.tickets.collectAsState()
    val favorites by viewModel.favoriteRoutes.collectAsState()
    val alerts by viewModel.travelAlerts.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(premiumBackgroundBrush())
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Welcoming & Profile header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Namaste, ${currentUser?.name ?: "Yatri"}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Smarter Railway Journeys Start Here",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }

                // Custom premium Avatar
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(RailOrangeAccent)
                        .border(2.dp, RailGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (currentUser?.name?.take(1) ?: "Y").uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 20.sp
                    )
                }
            }
        }

        // Travel Alerts Ticker
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .border(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Alert",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = alerts.lastOrNull() ?: "All systems working normally.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }

        // Quick AI Feature Shortcuts
        item {
            Text(
                text = "Premium AI Services",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Shortcut 1: PNR
                GlassmorphicCard(
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateToSection("pnr") }
                ) {
                    Icon(Icons.Default.QueryStats, contentDescription = "PNR", tint = RailOrangeAccent, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("PNR Prediction", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Confirmation chance %", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                // Shortcut 2: Live Tracking
                GlassmorphicCard(
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateToSection("live") }
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = "Live", tint = RailGold, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Live Status", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Platform & Delays", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Shortcut 3: Chatbot
                GlassmorphicCard(
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateToSection("chatbot") }
                ) {
                    Icon(Icons.Default.ChatBubble, contentDescription = "AI Guide", tint = Color(0xFF64B5F6), modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Yatri AI Chat", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Natural Railway Q&A", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                // Shortcut 4: Journey Planner
                GlassmorphicCard(
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateToSection("planner") }
                ) {
                    Icon(Icons.Default.EventNote, contentDescription = "Plan", tint = Color(0xFF81C784), modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Journey Planner", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Itineraries & Hotels", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        // Active Trips / Booked Tickets
        item {
            Text(
                text = "Recent Active Trips",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 12.dp)
            )
        }

        if (tickets.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No active tickets booked yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            items(tickets) { ticket ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable {
                            viewModel.queryPnr(ticket.pnr)
                            onNavigateToSection("pnr")
                        }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(ticket.trainName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(
                                "PNR: ${ticket.pnr}",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(ticket.sourceStation, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(ticket.journeyDate, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Icon(Icons.Default.ArrowForward, contentDescription = "Arrow")
                            Column(horizontalAlignment = Alignment.End) {
                                Text(ticket.destinationStation, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(ticket.travelClass, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Divider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (ticket.currentStatus == "CNF") StatusGreen else StatusYellow)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(ticket.currentStatus, fontWeight = FontWeight.Bold, color = if (ticket.currentStatus == "CNF") StatusGreen else StatusYellow)
                            }
                            if (ticket.currentStatus != "CNF") {
                                Text("AI: ${ticket.confirmationProbability}% Confirmation Chance", fontSize = 12.sp, color = RailOrangeAccent, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Favorite Routes
        item {
            Text(
                text = "Favorite Saved Routes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 12.dp)
            )
        }

        if (favorites.isEmpty()) {
            item {
                Text("No favorite routes added yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(favorites) { fav ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier
                                .width(200.dp)
                                .clickable {
                                    viewModel.updateSearchParams(
                                        fav.sourceStation,
                                        fav.destinationStation,
                                        "Tomorrow",
                                        "3A",
                                        "General",
                                        1
                                    )
                                    viewModel.executeTrainSearch()
                                    onNavigateToSection("search")
                                }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Icon(Icons.Default.Star, contentDescription = "Fav", tint = RailGold)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(fav.sourceStation.take(15), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("to", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(fav.destinationStation.take(15), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }

        // Nearby Stations & Services
        item {
            Text(
                text = "Nearby Stations & Desks",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 12.dp)
            )
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("New Delhi Station (NDLS)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Distance: 1.2 km | Platform Count: 16", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Button(
                        onClick = { onNavigateToSection("explorer") },
                        colors = ButtonDefaults.buttonColors(containerColor = RailOrangeAccent)
                    ) {
                        Text("Explore Map", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// ==========================================
// 5. TRAIN SEARCH
// ==========================================
@Composable
fun TrainSearchScreen(viewModel: RailViewModel) {
    val src by viewModel.searchSource.collectAsState()
    val dst by viewModel.searchDestination.collectAsState()
    val date by viewModel.searchDate.collectAsState()
    val travelClass by viewModel.searchClass.collectAsState()
    val quota by viewModel.searchQuota.collectAsState()
    val passengersCount by viewModel.searchPassengers.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val passengersList by viewModel.passengers.collectAsState()

    var showBookingDialog by remember { mutableStateOf<TrainInfo?>(null) }
    var selectedBookingClass by remember { mutableStateOf("3A") }
    var selectedPassengerProfile by remember { mutableStateOf<Passenger?>(null) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(premiumBackgroundBrush())
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "Plan Journey",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Form inputs
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Source / Dest
                    OutlinedTextField(
                        value = src,
                        onValueChange = { viewModel.updateSearchParams(it, dst, date, travelClass, quota, passengersCount) },
                        label = { Text("Source Station") },
                        leadingIcon = { Icon(Icons.Default.Train, contentDescription = "Source") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = dst,
                        onValueChange = { viewModel.updateSearchParams(src, it, date, travelClass, quota, passengersCount) },
                        label = { Text("Destination Station") },
                        leadingIcon = { Icon(Icons.Default.Train, contentDescription = "Destination") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = date,
                            onValueChange = { viewModel.updateSearchParams(src, dst, it, travelClass, quota, passengersCount) },
                            label = { Text("Date") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = travelClass,
                            onValueChange = { viewModel.updateSearchParams(src, dst, date, it, quota, passengersCount) },
                            label = { Text("Class") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = quota,
                            onValueChange = { viewModel.updateSearchParams(src, dst, date, travelClass, it, passengersCount) },
                            label = { Text("Quota") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = passengersCount.toString(),
                            onValueChange = {
                                val num = it.toIntOrNull() ?: 1
                                viewModel.updateSearchParams(src, dst, date, travelClass, quota, num)
                            },
                            label = { Text("Passengers") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { viewModel.executeTrainSearch() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("search_trains_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = RailBluePrimary, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Search Trains", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }

        item {
            Text(
                text = "Available Trains",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
            )
        }

        if (isSearching) {
            item {
                ShimmerCard()
            }
        } else if (searchResults.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Enter routes to search for real-time trains.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            items(searchResults) { train ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("${train.number} - ${train.name}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Runs: ${train.runningDays}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(RailGold.copy(alpha = 0.15f))
                                    .padding(6.dp)
                            ) {
                                Text(train.duration, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RailOrangeAccent)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(train.departure, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(train.source.substringBefore(" "), fontSize = 12.sp, color = Color.Gray)
                            }
                            Icon(Icons.Default.ArrowForward, contentDescription = "Route", tint = Color.LightGray)
                            Column(horizontalAlignment = Alignment.End) {
                                Text(train.arrival, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(train.destination.substringBefore(" "), fontSize = 12.sp, color = Color.Gray)
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                        // Fares & Book Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                train.classes.forEach { cls ->
                                    val seat = train.seats[cls] ?: ""
                                    Text("$cls: $seat", fontSize = 12.sp, color = if (seat.contains("Available")) StatusGreen else StatusRed, fontWeight = FontWeight.SemiBold)
                                }
                            }
                            Button(
                                onClick = { showBookingDialog = train },
                                colors = ButtonDefaults.buttonColors(containerColor = RailOrangeAccent),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Book Ticket", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Interactive simulated instant Booking Dialog
    if (showBookingDialog != null) {
        val train = showBookingDialog!!
        AlertDialog(
            onDismissRequest = { showBookingDialog = null },
            title = { Text("Book Train ticket") },
            text = {
                Column {
                    Text("Train: ${train.name} (${train.number})")
                    Text("Fare: ₹${train.fares[selectedBookingClass] ?: 0.0}")
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Select Booking Class:", fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        train.classes.forEach { cls ->
                            val isSelected = selectedBookingClass == cls
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedBookingClass = cls },
                                label = { Text(cls) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Select Passenger Profile:", fontWeight = FontWeight.Bold)
                    if (passengersList.isEmpty()) {
                        Text("No saved passengers. Click Profile below to add passengers first.", color = Color.Red, fontSize = 12.sp)
                    } else {
                        passengersList.forEach { p ->
                            val isSelected = selectedPassengerProfile?.id == p.id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedPassengerProfile = p }
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent)
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = isSelected, onClick = { selectedPassengerProfile = p })
                                Text("${p.name} (Age: ${p.age}, ${p.gender})")
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val passenger = selectedPassengerProfile
                        if (passenger != null) {
                            viewModel.bookTicketSimulation(train, selectedBookingClass, passenger)
                            showBookingDialog = null
                            Toast.makeText(context, "Ticket booked successfully! PNR generated.", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "Please select a passenger profile", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Confirm Booking")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBookingDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// ==========================================
// 6. PNR STATUS & AI SMART PREDICTIONS
// ==========================================
@Composable
fun PnrScreen(viewModel: RailViewModel) {
    val pnrInput by viewModel.pnrInput.collectAsState()
    val ticketResult by viewModel.pnrTicketResult.collectAsState()
    val prediction by viewModel.pnrPrediction.collectAsState()
    val isLoading by viewModel.isPnrLoading.collectAsState()

    var inputPnr by remember { mutableStateOf("") }
    val ticketsList by viewModel.tickets.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(premiumBackgroundBrush())
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "PNR Prediction",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Enter PNR to run Gemini confirmation probability engine",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Entry Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = inputPnr,
                        onValueChange = { if (it.length <= 10) inputPnr = it },
                        label = { Text("10-Digit PNR Number") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("pnr_input_field"),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.queryPnr(inputPnr) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("pnr_prediction_submit"),
                        colors = ButtonDefaults.buttonColors(containerColor = RailOrangeAccent)
                    ) {
                        Text("Get Smart AI Prediction", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Quick selects
        if (ticketsList.isNotEmpty()) {
            item {
                Text(
                    text = "Track Booked PNRs",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(ticketsList) { t ->
                        FilterChip(
                            selected = pnrInput == t.pnr,
                            onClick = {
                                inputPnr = t.pnr
                                viewModel.queryPnr(t.pnr)
                            },
                            label = { Text("PNR: ${t.pnr}") }
                        )
                    }
                }
            }
        }

        if (isLoading) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                CircularProgressIndicator(modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally))
                Text(
                    "Running Gemini confirmation modeling algorithms...",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        } else if (ticketResult != null) {
            val ticket = ticketResult!!
            item {
                Spacer(modifier = Modifier.height(24.dp))
                // Ticket Detail Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(ticket.trainName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(ticket.trainNumber, color = RailOrangeAccent, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Passenger(s): ${ticket.passengerNames}", fontSize = 13.sp)
                        Text("Coach/Seat: ${ticket.coach} / ${ticket.seatNumber}", fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Booking Status: ${ticket.bookingStatus}", color = Color.Gray, fontSize = 12.sp)
                                Text("Current Status: ${ticket.currentStatus}", fontWeight = FontWeight.Bold, color = if (ticket.currentStatus == "CNF") StatusGreen else StatusYellow)
                            }
                            Text(ticket.chartStatus, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            // AI Confirmation Prediction result card
            if (prediction != null) {
                val pred = prediction!!
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, RailGold.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = "AI", tint = RailGold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Gemini AI Prediction Engine", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = RailGold)
                            }
                            Spacer(modifier = Modifier.height(16.dp))

                            // Probabilities Meter
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("${pred.probability}%", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = if (pred.probability > 75) StatusGreen else StatusYellow)
                                    Text("Confirmation", fontSize = 11.sp)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("${pred.racChance}%", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = RailOrangeAccent)
                                    Text("RAC Chance", fontSize = 11.sp)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("${pred.waitingChance}%", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = StatusRed)
                                    Text("Waitlist Chance", fontSize = 11.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Smart Trend Analysis:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(pred.analysis, fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f))

                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Suggested Route Alternatives:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = RailOrangeAccent)
                            Text(pred.alternatives, fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f))
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 7. LIVE TRAIN STATUS (MAPS INCLUDED)
// ==========================================
@Composable
fun LiveStatusScreen(viewModel: RailViewModel) {
    val trainNumber by viewModel.liveTrainNumber.collectAsState()
    val liveResult by viewModel.liveStatusResult.collectAsState()
    val isLoading by viewModel.isLiveLoading.collectAsState()

    var inputTrainNumber by remember { mutableStateOf("12951") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(premiumBackgroundBrush())
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "Live Train Tracker",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Tracker Input Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputTrainNumber,
                        onValueChange = { inputTrainNumber = it },
                        label = { Text("5-Digit Train Number") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = { viewModel.queryLiveStatus(inputTrainNumber) },
                        colors = ButtonDefaults.buttonColors(containerColor = RailOrangeAccent),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Track")
                    }
                }
            }
        }

        if (isLoading) {
            item {
                Spacer(modifier = Modifier.height(32.dp))
                CircularProgressIndicator(modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally))
            }
        } else if (liveResult != null) {
            val live = liveResult!!
            item {
                Spacer(modifier = Modifier.height(24.dp))
                // Speed & Platform Tracker Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Current Location:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (live.delayMinutes == 0) StatusGreen.copy(alpha = 0.15f) else StatusRed.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (live.delayMinutes == 0) "On Time" else "${live.delayMinutes} Mins Late",
                                    color = if (live.delayMinutes == 0) StatusGreen else StatusRed,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text(live.currentStation, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Speed: ${live.speed} km/h", fontWeight = FontWeight.SemiBold)
                                Text("Platform: PF ${live.platform}", fontWeight = FontWeight.SemiBold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Next Stop: ${live.nextStation.substringBefore(" ")}")
                                Text("Expected: ${live.expectedArrival}")
                            }
                        }
                    }
                }
            }

            // Custom Interactive Track Map
            item {
                Spacer(modifier = Modifier.height(16.dp))
                val stationLabels = live.stationsList.map { it.stationName }
                InteractiveTrackMap(
                    currentStationIndex = 2, // Highlight active index
                    stationsList = stationLabels,
                    delayMinutes = live.delayMinutes
                )
            }

            // Complete Schedule Stops list
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Complete Schedule Stops", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(live.stationsList) { stop ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(if (stop.isVisited) Color.Gray else RailGold)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(stop.stationName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("PF: ${stop.platform} | Dep: ${stop.departureTime}", fontSize = 12.sp, color = Color.Gray)
                    }
                    if (stop.isVisited) {
                        Text("Passed", fontSize = 11.sp, color = Color.Gray)
                    } else {
                        Text("Upcoming", fontSize = 11.sp, color = RailOrangeAccent, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ==========================================
// 8. AI CHATBOT & VOICE ASSISTANT
// ==========================================
@Composable
fun ChatbotScreen(viewModel: RailViewModel) {
    val messages by viewModel.chatMessages.collectAsState()
    val isChatLoading by viewModel.isChatLoading.collectAsState()
    val speechText by viewModel.speechText.collectAsState()

    var inputMsg by remember { mutableStateOf("") }
    var isListeningSpeech by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(premiumBackgroundBrush())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Yatri AI Assistant", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("Ask refund, tatkal, lost baggage, rules", fontSize = 11.sp, color = Color.Gray)
            }
            TextButton(onClick = { viewModel.clearChat() }) {
                Text("Clear Chat", color = StatusRed)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Chats lists
        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                reverseLayout = false
            ) {
                items(messages) { msg ->
                    val isAi = msg.sender == "AI"
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = if (isAi) Arrangement.Start else Arrangement.End
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isAi) MaterialTheme.colorScheme.surfaceVariant else RailBluePrimary
                            ),
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (isAi) 4.dp else 16.dp,
                                bottomEnd = if (isAi) 16.dp else 4.dp
                            ),
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = msg.message,
                                    color = if (isAi) MaterialTheme.colorScheme.onSurfaceVariant else Color.White,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                if (isChatLoading) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Yatri AI is thinking...", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }

        // Quick query suggestion bubbles
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            val suggestions = listOf("Tatkal opening timings", "Refund cancellation charges", "Luggage weight limit")
            items(suggestions) { item ->
                SuggestionChip(
                    onClick = { viewModel.sendChatMessage(item) },
                    label = { Text(item) }
                )
            }
        }

        // Speech Listener Banner
        if (isListeningSpeech) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = RailOrangeAccent)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Listening closely... Speak your travel command.", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Send & Mic Inputs Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Speech Recognition Simulator trigger
            IconButton(
                onClick = {
                    scope.launch {
                        isListeningSpeech = true
                        delay(2500)
                        isListeningSpeech = false
                        val commands = listOf(
                            "Find trains from Delhi to Mumbai tomorrow",
                            "Check my PNR 4321098765",
                            "When will my train 12951 arrive?"
                        )
                        val randomCommand = commands.random()
                        Toast.makeText(context, "Heard: \"$randomCommand\"", Toast.LENGTH_LONG).show()
                        viewModel.processVoiceInput(randomCommand)
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(RailOrangeAccent)
                    .testTag("mic_voice_assistant_button")
            ) {
                Icon(Icons.Default.Mic, contentDescription = "Voice Input", tint = Color.White)
            }

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedTextField(
                value = inputMsg,
                onValueChange = { inputMsg = it },
                placeholder = { Text("Ask anything...") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_text_field"),
                shape = RoundedCornerShape(24.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (inputMsg.trim().isNotEmpty()) {
                        viewModel.sendChatMessage(inputMsg)
                        inputMsg = ""
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(RailBluePrimary)
                    .testTag("chat_send_button")
            ) {
                Icon(Icons.Filled.Send, contentDescription = "Send", tint = Color.White)
            }
        }
    }
}

// ==========================================
// 9. SMART JOURNEY PLANNER
// ==========================================
@Composable
fun JourneyPlannerScreen(viewModel: RailViewModel) {
    val src by viewModel.searchSource.collectAsState()
    val dst by viewModel.searchDestination.collectAsState()
    val date by viewModel.searchDate.collectAsState()
    val itinerary by viewModel.itineraryResult.collectAsState()
    val isLoading by viewModel.isItineraryLoading.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(premiumBackgroundBrush())
            .padding(16.dp)
    ) {
        item {
            Text("Journey Planner", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            Text("AI multi-modal transit & hotel itineraries scheduler", fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Current Selected Trip: $src to $dst", fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.generateJourneyItinerary(src, dst, date) },
                        modifier = Modifier.fillMaxWidth().testTag("generate_itinerary_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = RailBluePrimary)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "AI")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate AI Itinerary")
                        }
                    }
                }
            }
        }

        if (isLoading) {
            item {
                Spacer(modifier = Modifier.height(32.dp))
                CircularProgressIndicator(modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally))
                Text("Analyzing metropolitan routes, maps, weather and hotels...", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            }
        } else if (itinerary != null) {
            val plan = itinerary!!
            item {
                Spacer(modifier = Modifier.height(24.dp))
                // Travel Packing checklist
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Check", tint = StatusGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Pre-Journey Packing Checklist", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(plan.checklist, fontSize = 13.sp)
                    }
                }

                // Source Metropolitan Local Connection
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DirectionsBus, contentDescription = "Transit", tint = RailOrangeAccent)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Source Local Transit Connection", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(plan.sourceTransit, fontSize = 13.sp)
                    }
                }

                // Full Itinerary Day Schedule
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "Plan", tint = Color(0xFF64B5F6))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Full Travel Schedule Planner", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(plan.itinerary, fontSize = 13.sp)
                    }
                }

                // Destination Metropolitan Local Connection
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DirectionsCar, contentDescription = "Transit", tint = RailGold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Destination Local Transit Connection", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(plan.destTransit, fontSize = 13.sp)
                    }
                }

                // Hotels recommendation in Destination
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Hotel, contentDescription = "Hotel", tint = Color(0xFFE57373))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Recommended Accommodations", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(plan.hotels, fontSize = 13.sp)
                    }
                }

                // Nearby Tourist Attractions
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Landscape, contentDescription = "Attraction", tint = Color(0xFF81C784))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Nearby Tourist Spots & Landmarks", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(plan.attractions, fontSize = 13.sp)
                    }
                }

                // Destination Weather Summary
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Cloud, contentDescription = "Weather", tint = Color(0xFF4FC3F7))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Destination Weather Summary", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(plan.weather, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

// ==========================================
// 10. STATION EXPLORER
// ==========================================
@Composable
fun StationExplorerScreen() {
    var selectedFacility by remember { mutableStateOf("Food") }
    val facilities = listOf("Food", "ATM", "Waiting Rooms", "Parking", "Medical", "Restrooms")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(premiumBackgroundBrush())
            .padding(16.dp)
    ) {
        item {
            Text("Station Explorer", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            Text("Find platform facilities and nearby services", fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))

            // Facility select filters
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(facilities) { item ->
                    val isSelected = selectedFacility == item
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFacility = item },
                        label = { Text(item) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Interactive Facility maps & directories simulation
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Active Station: New Delhi (NDLS)", fontWeight = FontWeight.Bold)
                        Text("16 Platforms", color = RailOrangeAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    // Map View simulator banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Map, contentDescription = "Map", modifier = Modifier.size(48.dp), tint = Color.Gray)
                            Text("Interactive Terminal Map Loading...", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Facility Directory Details:", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Directories entries based on selected facility
        val facilitiesDetails = when (selectedFacility) {
            "Food" -> listOf(
                Pair("IRCTC Food Court", "Platform 1, 1st Floor - Open 24/7"),
                Pair("Comesum Restaurant", "Main Entry Gate 1 - Multi-cuisine dining"),
                Pair("Amul Milk Parlour", "Platform 4 & 5 - Cold beverages & snacks")
            )
            "ATM" -> listOf(
                Pair("SBI ATM", "Platform 1 Entrance - Cash withdrawals"),
                Pair("HDFC Bank ATM", "Gate 2 Exit Desk - Cash withdrawals"),
                Pair("ICICI Bank ATM", "Platform 8 concourse - Cash withdrawals")
            )
            "Waiting Rooms" -> listOf(
                Pair("Premium Executive Lounge", "Platform 1, First floor - Sofas, Wi-Fi, Food buffet"),
                Pair("AC General Waiting Hall", "Concourse Hallway - Airconditioned seating"),
                Pair("Ladies Dedicated Waiting Room", "Platform 10 - Security guarded, Restrooms available")
            )
            "Parking" -> listOf(
                Pair("Premium Car Parking", "Entry Gate 1 - Under surveillance (Rs 100/hr)"),
                Pair("Two-wheeler Parking Spot", "Gate 2 Terminal parking - Economical (Rs 30/day)"),
                Pair("Prepaid Taxi & Cab Bay", "Exit concourse - 24/7 Taxi booking")
            )
            "Medical" -> listOf(
                Pair("Emergency First-Aid Desk", "Platform 1 Center - 24/7 Medical support & ambulance"),
                Pair("Railway Pharmacy Shop", "Main Entrance concourse - Life-saving medicines")
            )
            else -> listOf(
                Pair("Pay-and-use clean restrooms", "Platform 1, 6, 12, and 16"),
                Pair("Divyangjan accessible washrooms", "Main concourse entrance lobby")
            )
        }

        items(facilitiesDetails) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when (selectedFacility) {
                            "Food" -> Icons.Default.Restaurant
                            "ATM" -> Icons.Default.CreditCard
                            "Waiting Rooms" -> Icons.Default.Weekend
                            "Parking" -> Icons.Default.LocalParking
                            "Medical" -> Icons.Default.MedicalServices
                            else -> Icons.Default.Wc
                        },
                        contentDescription = "Icon",
                        tint = RailOrangeAccent
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(item.first, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(item.second, fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

// ==========================================
// 11. USER PROFILE & SETTINGS
// ==========================================
@Composable
fun ProfileSettingsScreen(
    viewModel: RailViewModel,
    onNavigateToSection: (String) -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val currentLanguage by viewModel.selectedLanguage.collectAsState()
    val notificationEnabled by viewModel.notificationsEnabled.collectAsState()
    val passengersList by viewModel.passengers.collectAsState()

    var showAddPassengerDialog by remember { mutableStateOf(false) }
    var inputPassengerName by remember { mutableStateOf("") }
    var inputPassengerAge by remember { mutableStateOf("") }
    var inputPassengerGender by remember { mutableStateOf("Male") }
    var inputPassengerBerth by remember { mutableStateOf("No Preference") }

    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(premiumBackgroundBrush())
            .padding(16.dp)
    ) {
        // User Meta header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(RailBluePrimary)
                            .border(3.dp, RailGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (currentUser?.name?.take(1) ?: "Y").uppercase(),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(currentUser?.name ?: "Yatri Guest", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text(currentUser?.email ?: "guest@gmail.com", fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { onNavigateToSection("admin") },
                        colors = ButtonDefaults.buttonColors(containerColor = RailOrangeAccent)
                    ) {
                        Text("Open Admin Panel")
                    }
                }
            }
            Divider(modifier = Modifier.padding(vertical = 16.dp))
        }

        // Saved Passenger Profiles
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Saved Passengers Profiles", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                IconButton(onClick = { showAddPassengerDialog = true }) {
                    Icon(Icons.Default.AddCircle, contentDescription = "Add Passenger", tint = RailOrangeAccent)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (passengersList.isEmpty()) {
            item {
                Text("No saved passenger profiles. Click '+' to add.", color = Color.Gray, fontSize = 12.sp)
            }
        } else {
            items(passengersList) { passenger ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(passenger.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Age: ${passenger.age} | ${passenger.gender} | Preferred: ${passenger.preferredBerth}", fontSize = 12.sp, color = Color.Gray)
                        }
                        IconButton(onClick = { viewModel.removePassengerProfile(passenger) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = StatusRed)
                        }
                    }
                }
            }
        }

        // Settings items
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text("App Customizations Settings", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            // Dark mode setting
            ListItem(
                headlineContent = { Text("Dark Theme Interface") },
                supportingContent = { Text("Toggle premium railway slate layout") },
                leadingContent = { Icon(Icons.Default.DarkMode, contentDescription = "Theme") },
                trailingContent = {
                    Switch(checked = isDarkMode, onCheckedChange = { viewModel.toggleDarkMode() })
                },
                modifier = Modifier.clickable { viewModel.toggleDarkMode() }
            )

            // Notifications
            ListItem(
                headlineContent = { Text("Live Travel Notifications") },
                supportingContent = { Text("Alerts on delays, platforms & coach updates") },
                leadingContent = { Icon(Icons.Default.Notifications, contentDescription = "Notif") },
                trailingContent = {
                    Switch(checked = notificationEnabled, onCheckedChange = { viewModel.toggleNotifications() })
                },
                modifier = Modifier.clickable { viewModel.toggleNotifications() }
            )

            // Select language list
            var showLanguageSelect by remember { mutableStateOf(false) }
            ListItem(
                headlineContent = { Text("App Language Selection") },
                supportingContent = { Text("Current: $currentLanguage") },
                leadingContent = { Icon(Icons.Default.Language, contentDescription = "Lang") },
                modifier = Modifier.clickable { showLanguageSelect = true }
            )

            if (showLanguageSelect) {
                AlertDialog(
                    onDismissRequest = { showLanguageSelect = false },
                    title = { Text("Select Language") },
                    text = {
                        Column {
                            val langs = listOf("English", "Hindi (हिन्दी)", "Telugu (తెలుగు)", "Tamil (தமிழ்)", "Bengali (বাংলা)")
                            langs.forEach { l ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.changeLanguage(l.substringBefore(" "))
                                            showLanguageSelect = false
                                        }
                                        .padding(12.dp)
                                ) {
                                    Text(l, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    },
                    confirmButton = {}
                )
            }

            // About, Privacy, Signout
            ListItem(
                headlineContent = { Text("About RailYatra") },
                supportingContent = { Text("Version 1.0.4 Premium-Pro") },
                leadingContent = { Icon(Icons.Default.Info, contentDescription = "About") }
            )

            ListItem(
                headlineContent = { Text("Log Out Account") },
                supportingContent = { Text("Clear sessions and exit dashboard safely") },
                leadingContent = { Icon(Icons.Default.ExitToApp, contentDescription = "Exit", tint = StatusRed) },
                modifier = Modifier.clickable { viewModel.logoutUser() }
            )
        }
    }

    // Add Passenger Dialog
    if (showAddPassengerDialog) {
        AlertDialog(
            onDismissRequest = { showAddPassengerDialog = false },
            title = { Text("Add Passenger Profile") },
            text = {
                Column {
                    OutlinedTextField(
                        value = inputPassengerName,
                        onValueChange = { inputPassengerName = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = inputPassengerAge,
                        onValueChange = { inputPassengerAge = it },
                        label = { Text("Age") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Gender:", fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Male", "Female", "Other").forEach { g ->
                            val isSel = inputPassengerGender == g
                            FilterChip(
                                selected = isSel,
                                onClick = { inputPassengerGender = g },
                                label = { Text(g) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Preferred Berth:", fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("No Preference", "Lower", "Upper").forEach { b ->
                            val isSel = inputPassengerBerth == b
                            FilterChip(
                                selected = isSel,
                                onClick = { inputPassengerBerth = b },
                                label = { Text(b) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val age = inputPassengerAge.toIntOrNull() ?: 30
                        if (inputPassengerName.isNotEmpty()) {
                            viewModel.savePassengerProfile(inputPassengerName, age, inputPassengerGender, inputPassengerBerth)
                            inputPassengerName = ""
                            inputPassengerAge = ""
                            showAddPassengerDialog = false
                            Toast.makeText(context, "Passenger profile added successfully!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Please enter passenger name", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddPassengerDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// ==========================================
// 12. ADMIN PANEL
// ==========================================
@Composable
fun AdminPanelScreen(viewModel: RailViewModel) {
    var title by remember { mutableStateOf("") }
    var msg by remember { mutableStateOf("") }
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(premiumBackgroundBrush())
            .padding(16.dp)
    ) {
        item {
            Text("Admin Dashboard Control", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            Text("Broadcast announcements & review analytical telemetry", fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))

            // Stats Cards Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Active Bookings", fontSize = 11.sp, color = Color.Gray)
                        Text("14,842", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = RailGold)
                    }
                }
                Card(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("AI Success Rate", fontSize = 11.sp, color = Color.Gray)
                        Text("99.4%", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = StatusGreen)
                    }
                }
                Card(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("FCM Channels", fontSize = 11.sp, color = Color.Gray)
                        Text("4,212", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = RailOrangeAccent)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Broadcast announcements
        item {
            Text("Broadcast Push Notification / Announcement", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Announcement Title") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = msg,
                        onValueChange = { msg = it },
                        label = { Text("Detailed Broadcast Message") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            if (title.isNotEmpty() && msg.isNotEmpty()) {
                                viewModel.sendAdminAnnouncement(title, msg)
                                title = ""
                                msg = ""
                                Toast.makeText(context, "Announcement broadcasted successfully to all devices!", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context, "Please enter title and message", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("admin_broadcast_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = RailOrangeAccent)
                    ) {
                        Text("Dispatch Announcement", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Historical analytical list
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text("Active System Logs Desk", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
        }

        val adminLogs = listOf(
            "⚡ Firebase AI Model sync complete with latency 240ms.",
            "📡 Google Cloud Storage backups executed: 124MB compressed.",
            "📣 Announcements channel dispatched 4,110 Firebase Cloud Messaging frames.",
            "📈 Gemini API prediction queries peaked at 45 requests/min."
        )

        items(adminLogs) { log ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = log,
                    modifier = Modifier.padding(12.dp),
                    fontSize = 12.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            }
        }
    }
}
