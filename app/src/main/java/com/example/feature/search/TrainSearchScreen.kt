package com.example.feature.search

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.database.Passenger
import com.example.core.database.TrainInfo
import com.example.core.designsystem.*
import kotlinx.coroutines.launch

@Composable
fun TrainSearchScreen(
    viewModel: SearchViewModel,
    onBookSuccess: (String) -> Unit
) {
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
                            viewModel.bookTicketSimulation(train, selectedBookingClass, passenger) { pnr ->
                                onBookSuccess(pnr)
                            }
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
