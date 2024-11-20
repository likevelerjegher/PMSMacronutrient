package com.example.macronutrient.screens.dairy

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.macronutrient.screens.saving.SavingAdapter
import com.example.macronutrient.dao.FoodDao
import com.example.macronutrient.dao.SavingDao
import com.example.macronutrient.dao.UserDao
import com.example.macronutrient.entity.Food
import com.example.macronutrient.entity.Saving
import com.example.macronutrient.screens.saving.ConfirmationDeleteDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun DiaryScreen(userDao: UserDao, savingDao: SavingDao, foodDao: FoodDao, nickname: String) {
    var savings by remember { mutableStateOf<List<Saving>>(emptyList()) }
    var foodDetails by remember { mutableStateOf<Map<Int, Food>>(emptyMap()) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var savingToDelete by remember { mutableStateOf<Saving?>(null) }
    var selectedSaving by remember { mutableStateOf<Saving?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Загружаем savings и соответствующие данные о food
    LaunchedEffect(Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            val userId = userDao.getUserIdByNickname(nickname) ?: return@launch
            val userSavings = savingDao.getSavingsByUserId(userId)

            // Получаем данные о рейсах для каждого бронирования
            val foodMap = userSavings.associateBy(
                { it.foodId },
                { foodDao.getFoodById(it.foodId) }
            ).filterValues { it != null } as Map<Int, Food>

            withContext(Dispatchers.Main) {
                savings = userSavings
                foodDetails = foodMap
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("My Diary", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        if (savings.isNotEmpty()) {
            AndroidView(
                factory = { context ->
                    RecyclerView(context).apply {
                        layoutManager = LinearLayoutManager(context)
                        adapter = SavingAdapter(savings, foodDetails, { saving ->
                            savingToDelete = saving
                            showDeleteConfirmation = true
                        }, { saving ->
                            selectedSaving = saving
                        })
                    }
                }
            )
        } else {
            if (nickname.isEmpty()){
                Text("You have to Sign In first.", style = MaterialTheme.typography.bodySmall)
            } else {
                Text("You Diary is empty.", style = MaterialTheme.typography.bodySmall)
            }
        }
    }

    if (showDeleteConfirmation) {
        ConfirmationDeleteDialog(
            onConfirm = {
                savingToDelete?.let { saving ->
                    coroutineScope.launch(Dispatchers.IO) {
                        savingDao.deleteSavingById(saving.id)
                        withContext(Dispatchers.Main) {
                            savings = savings.filter { it.id != saving.id }
                            showDeleteConfirmation = false
                        }
                    }
                }
            },
            onDismiss = { showDeleteConfirmation = false }
        )
    }
}