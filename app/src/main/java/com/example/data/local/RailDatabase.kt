package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.Ticket
import com.example.data.model.Passenger
import com.example.data.model.FavoriteRoute

@Database(
    entities = [Ticket::class, Passenger::class, FavoriteRoute::class],
    version = 1,
    exportSchema = false
)
abstract class RailDatabase : RoomDatabase() {
    abstract fun railDao(): RailDao

    companion object {
        @Volatile
        private var INSTANCE: RailDatabase? = null

        fun getDatabase(context: Context): RailDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RailDatabase::class.java,
                    "rail_yatra_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
