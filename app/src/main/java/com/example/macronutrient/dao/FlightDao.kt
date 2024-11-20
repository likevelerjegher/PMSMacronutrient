package com.example.macronutrient.dao

import androidx.room.*
import com.example.macronutrient.entity.Flight

@Dao
interface FlightDao {
    @Insert
    suspend fun insertFlight(flight: Flight)

    @Query("SELECT * FROM flights")
    suspend fun getAllFlights(): List<Flight>

    @Query("SELECT * FROM flights WHERE id = :flightId")
    suspend fun getFlightById(flightId: Int): Flight?

    @Delete
    suspend fun deleteFlight(flight: Flight)
}
