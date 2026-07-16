package com.example

import android.app.Application
import com.example.core.database.RailDatabase
import com.example.core.database.RailRepository
import com.example.core.network.GeminiRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class RailApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { RailDatabase.getDatabase(this) }
    val railRepository by lazy { RailRepository(database.railDao()) }
    val geminiRepository by lazy { GeminiRepository() }

    override fun onCreate() {
        super.onCreate()
        
        // Prepopulate data if database is empty
        applicationScope.launch {
            val tickets = railRepository.allTickets.first()
            if (tickets.isEmpty()) {
                railRepository.prepopulateIfEmpty()
            }
        }
    }
}
