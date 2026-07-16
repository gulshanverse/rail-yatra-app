package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "passengers")
data class Passenger(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val age: Int,
    val gender: String, // Male, Female, Other
    val preferredBerth: String, // No Preference, Lower, Middle, Upper, Side Lower, Side Upper
    val idProofType: String = "Aadhaar Card",
    val idProofNumber: String = ""
)
