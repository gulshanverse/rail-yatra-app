package com.example.feature.journey

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.designsystem.*
import com.example.core.network.JourneyItinerary

@Composable
fun JourneyPlannerScreen(
    viewModel: JourneyViewModel,
    source: String,
    destination: String,
    date: String
) {
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

            // Current search params summary card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Current Selected Trip: $source to $destination", fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.generateJourneyItinerary(source, destination, date) },
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
                Text(
                    "Analyzing metropolitan routes, maps, weather and hotels...",
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    textAlign = TextAlign.Center
                )
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
