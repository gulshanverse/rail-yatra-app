package com.example.feature.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.database.FavoriteRoute
import com.example.core.designsystem.*

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToSection: (String) -> Unit,
    onQueryPnr: (String) -> Unit,
    onSelectFavoriteRoute: (FavoriteRoute) -> Unit
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
                            onQueryPnr(ticket.pnr)
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
                                    onSelectFavoriteRoute(fav)
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
