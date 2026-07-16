package com.example.core.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RailDao {

    // --- Tickets ---
    @Query("SELECT * FROM tickets ORDER BY journeyDate DESC")
    fun getAllTickets(): Flow<List<Ticket>>

    @Query("SELECT * FROM tickets WHERE pnr = :pnr LIMIT 1")
    suspend fun getTicketByPnr(pnr: String): Ticket?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: Ticket)

    @Update
    suspend fun updateTicket(ticket: Ticket)

    @Delete
    suspend fun deleteTicket(ticket: Ticket)

    @Query("DELETE FROM tickets WHERE pnr = :pnr")
    suspend fun deleteTicketByPnr(pnr: String)

    // --- Passengers ---
    @Query("SELECT * FROM passengers ORDER BY name ASC")
    fun getAllPassengers(): Flow<List<Passenger>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPassenger(passenger: Passenger)

    @Update
    suspend fun updatePassenger(passenger: Passenger)

    @Delete
    suspend fun deletePassenger(passenger: Passenger)

    // --- Favorite Routes ---
    @Query("SELECT * FROM favorite_routes ORDER BY frequencyCount DESC")
    fun getAllFavoriteRoutes(): Flow<List<FavoriteRoute>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteRoute(route: FavoriteRoute)

    @Query("UPDATE favorite_routes SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean)

    @Delete
    suspend fun deleteFavoriteRoute(route: FavoriteRoute)
}
