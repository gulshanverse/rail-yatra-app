package com.example.feature.station

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.designsystem.*

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
                Pair("First Aid Railway Clinic", "Platform 1 - Free emergency medical consults"),
                Pair("Apollo Pharmacy Outlet", "Gate 1 Concourse hall - Open 6:00 AM - 11:00 PM"),
                Pair("Wheelchair Assistance Desk", "Main Ticket concourse - Free companion assistance")
            )
            else -> listOf(
                Pair("Clean Express Restrooms", "Platform 1, 6, 12, 16 - Paid (Rs 5/-)"),
                Pair("Executive Shower Lounges", "Platform 1, 1st floor - Changing facilities")
            )
        }

        items(facilitiesDetails) { facility ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(facility.first, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(facility.second, fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}
