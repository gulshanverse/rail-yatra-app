package com.example.data.repository

import com.example.data.local.RailDao
import com.example.data.model.FavoriteRoute
import com.example.data.model.Passenger
import com.example.data.model.Ticket
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RailRepository(private val railDao: RailDao) {

    val allTickets: Flow<List<Ticket>> = railDao.getAllTickets()
    val allPassengers: Flow<List<Passenger>> = railDao.getAllPassengers()
    val allFavoriteRoutes: Flow<List<FavoriteRoute>> = railDao.getAllFavoriteRoutes()

    suspend fun getTicketByPnr(pnr: String): Ticket? = railDao.getTicketByPnr(pnr)

    suspend fun insertTicket(ticket: Ticket) = railDao.insertTicket(ticket)

    suspend fun updateTicket(ticket: Ticket) = railDao.updateTicket(ticket)

    suspend fun deleteTicket(ticket: Ticket) = railDao.deleteTicket(ticket)

    suspend fun deleteTicketByPnr(pnr: String) = railDao.deleteTicketByPnr(pnr)

    suspend fun insertPassenger(passenger: Passenger) = railDao.insertPassenger(passenger)

    suspend fun updatePassenger(passenger: Passenger) = railDao.updatePassenger(passenger)

    suspend fun deletePassenger(passenger: Passenger) = railDao.deletePassenger(passenger)

    suspend fun insertFavoriteRoute(route: FavoriteRoute) = railDao.insertFavoriteRoute(route)

    suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean) = railDao.updateFavoriteStatus(id, isFavorite)

    suspend fun deleteFavoriteRoute(route: FavoriteRoute) = railDao.deleteFavoriteRoute(route)

    // Prepopulate some initial journeys and passengers if they are empty
    suspend fun prepopulateIfEmpty() {
        // Prepopulate passenger profiles
        val passengers = listOf(
            Passenger(name = "Rajesh Kumar", age = 34, gender = "Male", preferredBerth = "Lower"),
            Passenger(name = "Priya Sharma", age = 29, gender = "Female", preferredBerth = "Side Lower")
        )
        passengers.forEach { insertPassenger(it) }

        // Prepopulate tickets
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()

        calendar.add(Calendar.DAY_OF_YEAR, 2)
        val date1 = sdf.format(calendar.time)

        calendar.add(Calendar.DAY_OF_YEAR, 5)
        val date2 = sdf.format(calendar.time)

        val ticket1 = Ticket(
            pnr = "4321098765",
            trainNumber = "12951",
            trainName = "Mumbai Rajdhani",
            sourceStation = "NDLS (New Delhi)",
            destinationStation = "MMCT (Mumbai Central)",
            journeyDate = date1,
            travelClass = "3A",
            quota = "General",
            passengerNames = "Rajesh Kumar",
            coach = "B3",
            seatNumber = "24 (LB)",
            currentStatus = "CNF",
            bookingStatus = "CNF",
            chartStatus = "Chart Prepared",
            fare = 2340.0,
            confirmationProbability = 100,
            waitlistChance = 0,
            racChance = 0,
            aiAlternativeSuggested = "Your ticket is already confirmed. Enjoy your premium travel on Mumbai Rajdhani!"
        )

        val ticket2 = Ticket(
            pnr = "9876543210",
            trainNumber = "12626",
            trainName = "Kerala Express",
            sourceStation = "NDLS (New Delhi)",
            destinationStation = "ERS (Ernakulam Town)",
            journeyDate = date2,
            travelClass = "SL",
            quota = "General",
            passengerNames = "Priya Sharma",
            coach = "S4",
            seatNumber = "WL/14",
            currentStatus = "WL/8",
            bookingStatus = "WL/14",
            chartStatus = "Chart Not Prepared",
            fare = 890.0,
            confirmationProbability = 45,
            waitlistChance = 40,
            racChance = 15,
            aiAlternativeSuggested = "High waitlist on Kerala Express. Recommended alternatives: 1. Split journey at Chennai Central. 2. Book NDLS to KCVL Special train."
        )

        insertTicket(ticket1)
        insertTicket(ticket2)

        // Prepopulate favorite routes
        insertFavoriteRoute(FavoriteRoute(sourceStation = "NDLS (New Delhi)", destinationStation = "MMCT (Mumbai Central)", frequencyCount = 5, isFavorite = true))
        insertFavoriteRoute(FavoriteRoute(sourceStation = "HWH (Howrah)", destinationStation = "NDLS (New Delhi)", frequencyCount = 3, isFavorite = false))
    }

    // --- Search Dummy Train Data ---
    fun searchTrains(source: String, destination: String): List<TrainInfo> {
        val src = source.uppercase()
        val dest = destination.uppercase()

        return listOf(
            TrainInfo(
                number = "12952",
                name = "Mumbai Rajdhani",
                source = source,
                destination = destination,
                departure = "16:55",
                arrival = "08:35",
                duration = "15h 40m",
                runningDays = "Daily",
                classes = listOf("1A", "2A", "3A"),
                fares = mapOf("1A" to 4750.0, "2A" to 2860.0, "3A" to 2050.0),
                seats = mapOf("1A" to "Available - 4", "2A" to "Available - 12", "3A" to "Available - 45")
            ),
            TrainInfo(
                number = "12926",
                name = "Paschim Express",
                source = source,
                destination = destination,
                departure = "16:30",
                arrival = "14:45",
                duration = "22h 15m",
                runningDays = "Daily",
                classes = listOf("2A", "3A", "SL"),
                fares = mapOf("2A" to 2450.0, "3A" to 1750.0, "SL" to 650.0),
                seats = mapOf("2A" to "WL/4", "3A" to "WL/12", "SL" to "RAC/15")
            ),
            TrainInfo(
                number = "22222",
                name = "Hazrat Nizamuddin - CSMT Rajdhani",
                source = source,
                destination = destination,
                departure = "17:15",
                arrival = "11:50",
                duration = "18h 35m",
                runningDays = "Sun, Tue, Wed, Thu, Sat",
                classes = listOf("1A", "2A", "3A"),
                fares = mapOf("1A" to 4300.0, "2A" to 2600.0, "3A" to 1850.0),
                seats = mapOf("1A" to "Available - 2", "2A" to "Available - 8", "3A" to "WL/2")
            ),
            TrainInfo(
                number = "12138",
                name = "Punjab Mail",
                source = source,
                destination = destination,
                departure = "05:15",
                arrival = "07:35",
                duration = "26h 20m",
                runningDays = "Daily",
                classes = listOf("2A", "3A", "SL"),
                fares = mapOf("2A" to 2100.0, "3A" to 1450.0, "SL" to 580.0),
                seats = mapOf("2A" to "Available - 5", "3A" to "WL/3", "SL" to "Available - 110")
            )
        )
    }

    // --- Live Tracking Mock Data ---
    fun getLiveStatus(trainNumber: String): LiveStatus {
        return when (trainNumber) {
            "12951", "12952" -> LiveStatus(
                currentStation = "Kota Junction (KOTA)",
                nextStation = "Ratlam Junction (RTM)",
                expectedArrival = "19:40",
                expectedDeparture = "19:45",
                delayMinutes = 5,
                speed = 110,
                platform = "1",
                stationsList = listOf(
                    StationStop("New Delhi (NDLS)", "16:55", "16:55", true, "16"),
                    StationStop("Mathura Junction (MTJ)", "18:38", "18:40", true, "1"),
                    StationStop("Kota Junction (KOTA)", "19:40", "19:45", false, "1"),
                    StationStop("Ratlam Junction (RTM)", "23:00", "23:05", false, "4"),
                    StationStop("Vadodara Junction (BRC)", "03:10", "03:20", false, "2"),
                    StationStop("Mumbai Central (MMCT)", "08:35", "08:35", false, "3")
                )
            )
            else -> LiveStatus(
                currentStation = "Ambala Cant Junction (UMB)",
                nextStation = "Ludhiana Junction (LDH)",
                expectedArrival = "10:15",
                expectedDeparture = "10:25",
                delayMinutes = 45,
                speed = 85,
                platform = "3",
                stationsList = listOf(
                    StationStop("New Delhi (NDLS)", "07:30", "07:30", true, "5"),
                    StationStop("Panipat Junction (PNP)", "08:45", "08:47", true, "1"),
                    StationStop("Ambala Cant Junction (UMB)", "10:15", "10:25", false, "3"),
                    StationStop("Ludhiana Junction (LDH)", "11:55", "12:05", false, "2"),
                    StationStop("Amritsar Junction (ASR)", "14:30", "14:30", false, "1")
                )
            )
        }
    }
}

data class TrainInfo(
    val number: String,
    val name: String,
    val source: String,
    val destination: String,
    val departure: String,
    val arrival: String,
    val duration: String,
    val runningDays: String,
    val classes: List<String>,
    val fares: Map<String, Double>,
    val seats: Map<String, String>
)

data class LiveStatus(
    val currentStation: String,
    val nextStation: String,
    val expectedArrival: String,
    val expectedDeparture: String,
    val delayMinutes: Int,
    val speed: Int,
    val platform: String,
    val stationsList: List<StationStop>
)

data class StationStop(
    val stationName: String,
    val arrivalTime: String,
    val departureTime: String,
    val isVisited: Boolean,
    val platform: String
)
