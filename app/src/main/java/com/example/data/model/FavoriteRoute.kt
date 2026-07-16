package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_routes")
data class FavoriteRoute(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sourceStation: String,
    val destinationStation: String,
    val frequencyCount: Int = 1,
    val isFavorite: Boolean = false
)
