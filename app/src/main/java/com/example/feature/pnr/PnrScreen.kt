package com.example.feature.pnr

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.designsystem.*

@Composable
fun PnrScreen(viewModel: PnrViewModel) {
    val pnrInput by viewModel.pnrInput.collectAsState()
    val ticketResult by viewModel.pnrTicketResult.collectAsState()
    val prediction by viewModel.pnrPrediction.collectAsState()
    val isLoading by viewModel.isPnrLoading.collectAsState()

    var inputPnr by remember { mutableStateOf("") }
    val ticketsList by viewModel.tickets.collectAsState()

    // Keep UI synchronized with viewmodel's active pnrInput
    LaunchedEffect(pnrInput) {
        if (pnrInput.isNotEmpty() && pnrInput != inputPnr) {
            inputPnr = pnrInput
        }
    }

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
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
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
