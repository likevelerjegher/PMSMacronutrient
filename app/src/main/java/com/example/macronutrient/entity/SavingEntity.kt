package com.example.macronutrient.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Saving")
data class Saving(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "food_Id") val foodId: Int,
    @ColumnInfo(name = "add_date") val addDate: String,
)