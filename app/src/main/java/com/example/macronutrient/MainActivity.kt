package com.example.macronutrient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.room.Room
import com.example.macronutrient.dao.FoodDao
import com.example.macronutrient.dao.MockFoodDao
import com.example.macronutrient.dao.MockSavingDao
import com.example.macronutrient.dao.MockUserDao
import com.example.macronutrient.dao.SavingDao
import com.example.macronutrient.dao.UserDao
import com.example.macronutrient.database.AppDatabase
import com.example.macronutrient.screens.dairy.DiaryScreen
import com.example.macronutrient.screens.food.FoodSearchScreen
import com.example.macronutrient.screens.profile.ProfileScreen
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@ExperimentalPagerApi
@ExperimentalFoundationApi
class MainActivity : ComponentActivity() {
    init {
        System.loadLibrary("macronutrient-lib")
    }
    private external fun validateCaloriesNative(calories: String): Boolean
    private fun validateCalories(calories: String): Boolean {
        return validateCaloriesNative(calories) // Вызов JNI функции
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "macronutrient-database"
        ).fallbackToDestructiveMigration().build()

        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            val userDao = db.userDao()
            val foodDao = db.foodDao()
            val savingDao = db.savingDao()

            //val newUser = User(nickname = "admin", password = "root", role = "admin")
            //userDao.insertUser(newUser)
            setContent {
                MyApp(
                    userDao = userDao,
                    foodDao = foodDao,
                    savingDao = savingDao,
                    validateCalories = this@MainActivity::validateCalories // Передача функции
                )
            }
        }
    }
}

@Composable
@ExperimentalFoundationApi
@ExperimentalPagerApi
fun MyApp(
    userDao: UserDao,
    foodDao: FoodDao,
    savingDao: SavingDao,
    validateCalories: (String) -> Boolean // Добавьте этот параметр
) {
    val pagerState = rememberPagerState(initialPage = 0)

    val tabs = listOf("Food", "Diary", "Profile")
    val coroutineScope = rememberCoroutineScope()
    var nickname by remember { mutableStateOf("") }
    var userRole by remember { mutableStateOf("") }

    LaunchedEffect(nickname) {
        userRole = userDao.getUserRole(nickname) ?: ""
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(pagerState.currentPage) { newIndex ->
                coroutineScope.launch {
                    pagerState.scrollToPage(newIndex)
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            HorizontalPager(
                state = pagerState,
                count = tabs.size
            ) { page ->
                when (page) {
                    0 -> FoodSearchScreen(
                        userRole = userRole,
                        foodDao = foodDao,
                        onNavigateToLogin = {
                            coroutineScope.launch {
                                pagerState.scrollToPage(2)
                            }
                        },
                        userDao = userDao,
                        savingDao = savingDao,
                        nickname = nickname,
                        validateCalories = validateCalories // Передача функции
                    )
                    1 -> DiaryScreen(savingDao = savingDao, userDao = userDao, foodDao = foodDao, nickname = nickname)
                    2 -> ProfileScreen(userDao = userDao, nickname = nickname, onNicknameChange = { newNickname -> nickname = newNickname }, foodDao = foodDao, onNavigateToFoods = {
                        coroutineScope.launch {
                            pagerState.scrollToPage(0)
                        }
                    })
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Food") },
            label = { Text("Food") },
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.DateRange, contentDescription = "Diary") },
            label = { Text("Diary") },
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Face, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) }
        )
    }
}


@Preview(showBackground = true)
@Composable
@ExperimentalPagerApi
@ExperimentalFoundationApi
fun DefaultPreview() {
    MyApp(userDao = MockUserDao(), foodDao = MockFoodDao(), savingDao = MockSavingDao(), validateCalories = { calories: String -> true })
}