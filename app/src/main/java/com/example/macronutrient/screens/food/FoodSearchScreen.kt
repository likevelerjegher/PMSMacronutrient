package com.example.macronutrient.screens.food

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.example.macronutrient.dao.SavingDao
import com.example.macronutrient.dao.UserDao
import com.example.macronutrient.entity.Food
import com.example.macronutrient.entity.Saving
import com.example.macronutrient.screens.saving.ConfirmationDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun FoodSearchScreen(
    userRole: String,
    foodDao: FoodDao,
    onNavigateToLogin: () -> Unit,
    userDao: UserDao,
    savingDao: SavingDao,
    nickname: String,
    validateCalories: (String) -> Boolean
) {
    var showAddFoodDialog by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("") }
    var availableFood by remember { mutableStateOf<List<Food>>(emptyList()) }
    var isAllFoodShown by remember { mutableStateOf(false) }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var selectedFood by remember { mutableStateOf<Food?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Dishes and foods", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        // Показ всех рейсов или фильтр по дате
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = {
                coroutineScope.launch(Dispatchers.IO) {
                    val foods = foodDao.getAllFood()
                    withContext(Dispatchers.Main) {
                        availableFood = foods
                        isAllFoodShown = true
                    }
                }
            }) {
                Text("Show all the food")
            }

        }

        Spacer(modifier = Modifier.height(16.dp))

        // food display
        if (availableFood.isNotEmpty()) {
            Text(
                text = "All available food" ,
                style = MaterialTheme.typography.titleMedium
            )
            availableFood.forEach { food ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            if (userRole.isEmpty()) {
                                onNavigateToLogin()
                            } else {
                                selectedFood = food
                                showConfirmationDialog = true
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(horizontalAlignment = Alignment.Start) {
                            //Text(text = "${food.date}", style = MaterialTheme.typography.bodySmall)
                            Text(text = "${food.productName}", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Carbs: ${food.carbs} g.", style = MaterialTheme.typography.bodySmall)
                            Text(text = "Fats: ${food.fats} g.", style = MaterialTheme.typography.bodySmall)
                            Text(text = "Proteins: ${food.proteins} g.", style = MaterialTheme.typography.bodySmall)
                            Text(text = "Calories: ${food.calories} g.", style = MaterialTheme.typography.bodySmall)

                        }
                    }
                    if (userRole == "admin") {
                        IconButton(onClick = {
                            coroutineScope.launch(Dispatchers.IO) {
                                foodDao.deleteFood(food)
                                withContext(Dispatchers.Main) {
                                    availableFood = availableFood.filter { it.id != food.id }
                                }
                            }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete food")
                        }
                    }
                }
            }
        } else if (selectedDate.isNotEmpty()) {
            Text("No food for chosen date", style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (userRole == "admin") {
            Button(onClick = { showAddFoodDialog = true }) {
                Text("Add food")
            }
        }

        if (showConfirmationDialog) {
            ConfirmationDialog(
                onConfirm = {
                    coroutineScope.launch(Dispatchers.IO) {
                        selectedFood?.let { food ->
                            // Создаем бронирование и сохраняем его в базу данных
                            val userId = userDao.getUserIdByNickname(nickname) ?: return@launch
                            val saving = Saving(
                                userId = userId,
                                foodId = food.id,
                                addDate = selectedDate
                            )
                            savingDao.insertSaving(saving)
                        }
                        showConfirmationDialog = false
                    }
                },
                onDismiss = { showConfirmationDialog = false }
            )
        }

        if (showAddFoodDialog) {
            AddFoodDialog(onDismiss = { showAddFoodDialog = false }, userDao = userDao, foodDao = foodDao, validateCalories = validateCalories)
        }
    }
}