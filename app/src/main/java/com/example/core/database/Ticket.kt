package com.example.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tickets")
data class Ticket(
    @PrimaryKey val pnr: String,
    val trainNumber: String,
    val trainName: String,
    val sourceStation: String,
    val destinationStation: String,
    val journeyDate: String,
    val travelClass: String, // e.g. 3A, 2A, SL
    val quota: String,       // e.g. General, Tatkal
    val passengerNames: String, // Comma separated
    val coach: String,       // e.g. B1, S3
    val seatNumber: String,  // e.g. 23, 45
    val currentStatus: String, // e.g. CNF, WL/12, RAC/4
    val bookingStatus: String, // e.g. WL/24
    val chartStatus: String,   // e.g. Chart Not Prepared, Chart Prepared
    val fare: Double,
    val confirmationProbability: Int = 95, // AI Prediction %
    val waitlistChance: Int = 5,
    val racChance: Int = 0,
    val aiAlternativeSuggested: String = "" // AI suggested alternative routes/trains
)
