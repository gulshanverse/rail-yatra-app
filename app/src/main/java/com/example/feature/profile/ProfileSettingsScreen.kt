package com.example.feature.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.common.User
import com.example.core.designsystem.*
import com.example.feature.settings.SettingsViewModel
import kotlinx.coroutines.flow.StateFlow

@Composable
fun ProfileSettingsScreen(
    profileViewModel: ProfileViewModel,
    settingsViewModel: SettingsViewModel,
    currentUserFlow: StateFlow<User?>,
    onNavigateToSection: (String) -> Unit,
    onLogout: () -> Unit
) {
    val currentUser by currentUserFlow.collectAsState()
    val isDarkMode by settingsViewModel.isDarkMode.collectAsState()
    val currentLanguage by settingsViewModel.selectedLanguage.collectAsState()
    val notificationEnabled by settingsViewModel.notificationsEnabled.collectAsState()
    val passengersList by profileViewModel.passengersList.collectAsState()

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
                        IconButton(onClick = { profileViewModel.removePassengerProfile(passenger) }) {
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
                    Switch(checked = isDarkMode, onCheckedChange = { settingsViewModel.toggleDarkMode() })
                },
                modifier = Modifier.clickable { settingsViewModel.toggleDarkMode() }
            )

            // Notifications
            ListItem(
                headlineContent = { Text("Live Travel Notifications") },
                supportingContent = { Text("Alerts on delays, platforms & coach updates") },
                leadingContent = { Icon(Icons.Default.Notifications, contentDescription = "Notif") },
                trailingContent = {
                    Switch(checked = notificationEnabled, onCheckedChange = { settingsViewModel.toggleNotifications() })
                },
                modifier = Modifier.clickable { settingsViewModel.toggleNotifications() }
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
                                            settingsViewModel.changeLanguage(l.substringBefore(" "))
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

            // Logout
            ListItem(
                headlineContent = { Text("Log Out Account") },
                supportingContent = { Text("Clear sessions and exit dashboard safely") },
                leadingContent = { Icon(Icons.Default.ExitToApp, contentDescription = "Exit", tint = StatusRed) },
                modifier = Modifier.clickable { onLogout() }
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
                            profileViewModel.savePassengerProfile(inputPassengerName, age, inputPassengerGender, inputPassengerBerth)
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
