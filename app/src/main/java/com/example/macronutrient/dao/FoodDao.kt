package com.example.macronutrient.dao

import androidx.room.*
import com.example.macronutrient.entity.Food

@Dao
interface FoodDao {
    @Insert
    suspend fun insertFood(flight: Food)

    @Query("SELECT * FROM food")
    suspend fun getAllFood(): List<Food>

    @Query("SELECT * FROM food WHERE id = :foodId")
    suspend fun getFoodById(foodId: Int): Food?

    @Delete
    suspend fun deleteFood(food: Food)
}

class MockFoodDao : FoodDao {
    private val foods = mutableListOf<Food>()

    override suspend fun insertFood(food: Food) {
        foods.add(food)
    }

    override suspend fun getAllFood(): List<Food> {
        return foods
    }

    override suspend fun getFoodById(foodId: Int): Food? {
        return foods.find { it.id == foodId }
    }

    override suspend fun deleteFood(food: Food) {
        foods.remove(food)
    }
}