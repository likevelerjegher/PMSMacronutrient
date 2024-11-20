package com.example.macronutrient.screens.food

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.macronutrient.dao.FoodDao
import com.example.macronutrient.dao.UserDao
import com.example.macronutrient.entity.Food
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AddFoodScreen(
    userDao: UserDao,
    userRole: String,
    foodDao: FoodDao,
    onFoodAdded: () -> Unit,
    validateCalories: (String) -> Boolean
) {
    var foodName by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fats by remember { mutableStateOf("") }
    var proteins by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isDataConfirmed by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Add food", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = foodName,
            onValueChange = { foodName = it },
            label = { Text("Food name") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = carbs,
            onValueChange = {
                carbs = it
                // Валидация number
                if (!validateCalories(carbs)) {
                    errorMessage = "Carbs should contain only numbers."
                } else {
                    errorMessage = ""
                }
            },
            label = { Text("Carbs") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = fats,
            onValueChange = {
                fats = it
                // Валидация number
                if (!validateCalories(fats)) {
                    errorMessage = "Fats should contain only numbers."
                } else {
                    errorMessage = ""
                }
            },
            label = { Text("Fats") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = proteins,
            onValueChange = {
                proteins = it
                // Валидация number
                if (!validateCalories(proteins)) {
                    errorMessage = "Proteins should contain only numbers."
                } else {
                    errorMessage = ""
                }
            },
            label = { Text("Proteins") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = calories,
            onValueChange = {
                calories = it
                // Валидация number
                if (!validateCalories(calories)) {
                    errorMessage = "Calories should contain only numbers."
                } else {
                    errorMessage = ""
                }
            },
            label = { Text("Calories") },
            isError = errorMessage.isNotEmpty()
        )

        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                isDataConfirmed = foodName.isNotEmpty() && validateCalories(carbs) &&
                        validateCalories(fats) && validateCalories(proteins) &&
                        validateCalories(calories) // Проверка валидности
                if (!isDataConfirmed) {
                    errorMessage = "Please submit data and make sure all the fields are not empty."
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isDataConfirmed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        ) {
            Text("Submit")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (isDataConfirmed) {
                coroutineScope.launch(Dispatchers.IO) {
                    try {
                        val food = Food(
                            productName = foodName,
                            calories = (calories.toDoubleOrNull() ?: 0.0).toString(),
                            carbs = carbs,
                            fats = fats,
                            proteins = proteins
                        )
                        val insertedId = foodDao.insertFood(food)
                        Log.d("MainActivity", "Food inserted with ID: $insertedId")
                        withContext(Dispatchers.Main) {
                            onFoodAdded()
                            isDataConfirmed = false
                        }
                    } catch (e: Exception) {
                        Log.e("MainActivity", "Error inserting food", e)
                        withContext(Dispatchers.Main) {
                            errorMessage = "Error while trying adding food: ${e.message}"
                        }
                    }
                }
            }
        }) {
            Text("Add to Database")
        }

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        }
    }
}