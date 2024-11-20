package com.example.macronutrient.dao

import androidx.room.*
import com.example.macronutrient.entity.Saving

@Dao
interface SavingDao {
    @Insert
    suspend fun insertSaving(saving: Saving)

    @Query("SELECT * FROM saving WHERE user_id = :userId")
    suspend fun getSavingsByUserId(userId: Int): List<Saving>

    @Query("SELECT * FROM saving WHERE food_id = :foodId")
    suspend fun getBookingsByFlightId(foodId: Int): List<Saving>

    @Delete
    suspend fun deleteSaving(saving: Saving)

    @Query("DELETE FROM saving WHERE id = :id")
    suspend fun deleteSavingById(id: Int)

}

class MockSavingDao : SavingDao {
    private val savings = mutableListOf<Saving>()

    override suspend fun insertSaving(saving: Saving) {
        savings.add(saving.copy(id = (savings.maxOfOrNull { it.id } ?: 0) + 1)) // Генерация ID
    }

    override suspend fun getSavingsByUserId(userId: Int): List<Saving> {
        return savings.filter { it.userId == userId }
    }

    override suspend fun getBookingsByFlightId(foodId: Int): List<Saving> {
        return savings.filter { it.foodId == foodId }
    }

    override suspend fun deleteSaving(saving: Saving) {
        savings.removeIf { it.id == saving.id }
    }

    override suspend fun deleteSavingById(id: Int) {
        savings.removeIf { it.id == id }
    }

}