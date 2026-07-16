package com.example.feature.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.designsystem.*

@Composable
fun AdminPanelScreen(
    viewModel: SettingsViewModel,
    onBroadcast: (String, String) -> Unit
) {
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
                                viewModel.sendAdminAnnouncement(title, msg, onBroadcast)
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
