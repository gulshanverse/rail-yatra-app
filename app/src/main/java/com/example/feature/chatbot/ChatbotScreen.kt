package com.example.feature.chatbot

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.designsystem.*
import com.example.core.network.VoiceAction
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ChatbotScreen(
    viewModel: ChatbotViewModel,
    onVoiceAction: (VoiceAction) -> Unit
) {
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
                        viewModel.processVoiceInput(randomCommand, onVoiceAction)
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
