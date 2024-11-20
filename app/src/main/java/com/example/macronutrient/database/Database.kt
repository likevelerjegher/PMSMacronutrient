package com.example.macronutrient.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.macronutrient.dao.UserDao
import com.example.macronutrient.dao.FlightDao
import com.example.macronutrient.dao.BookingDao
import com.example.macronutrient.dao.FoodDao
import com.example.macronutrient.dao.SavingDao
import com.example.macronutrient.entity.User
import com.example.macronutrient.entity.Flight
import com.example.macronutrient.entity.Booking
import com.example.macronutrient.entity.Food
import com.example.macronutrient.entity.Saving

@Database(entities = [User::class, Flight::class, Booking::class, Food::class, Saving::class], version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun flightDao(): FlightDao
    abstract fun bookingDao(): BookingDao
    abstract fun savingDao(): SavingDao
    abstract fun foodDao(): FoodDao
}
