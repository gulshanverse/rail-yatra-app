package com.example.feature.journey

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.database.LiveStatus
import com.example.core.designsystem.*

@Composable
fun LiveStatusScreen(viewModel: LiveStatusViewModel) {
    val trainNumber by viewModel.liveTrainNumber.collectAsState()
    val liveResult by viewModel.liveStatusResult.collectAsState()
    val isLoading by viewModel.isLiveLoading.collectAsState()

    var inputTrainNumber by remember { mutableStateOf("12951") }

    // Synchronize inputs
    LaunchedEffect(trainNumber) {
        if (trainNumber.isNotEmpty() && trainNumber != inputTrainNumber) {
            inputTrainNumber = trainNumber
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
