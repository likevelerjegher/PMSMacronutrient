package com.example.macronutrient.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food")
data class Food(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "product_name") val productName: String,
    @ColumnInfo(name = "calories") val calories: String,
    @ColumnInfo(name = "carbs") val carbs: String,
    @ColumnInfo(name = "fats") val fats: String,
    @ColumnInfo(name = "proteins") val proteins: String
)