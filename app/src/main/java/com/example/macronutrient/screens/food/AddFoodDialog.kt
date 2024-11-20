package com.example.macronutrient.screens.food

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import com.example.macronutrient.dao.FoodDao
import com.example.macronutrient.dao.UserDao

@Composable
fun AddFoodDialog(onDismiss: () -> Unit, userDao: UserDao, foodDao: FoodDao, validateCalories: (String) -> Boolean) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background
        ) {
            AddFoodScreen(
                userDao = userDao,
                userRole = "admin",
                foodDao = foodDao,
                onFoodAdded = onDismiss,
                validateCalories = validateCalories
            )
        }
    }
}