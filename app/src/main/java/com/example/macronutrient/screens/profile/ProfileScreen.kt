package com.example.macronutrient.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.macronutrient.authorisation.LoginScreen
import com.example.macronutrient.authorisation.RegistrationScreen
import com.example.macronutrient.dao.FoodDao
import com.example.macronutrient.dao.UserDao

@Composable
fun ProfileScreen(userDao: UserDao, foodDao: FoodDao, nickname: String, onNicknameChange: (String) -> Unit, onNavigateToFoods: () -> Unit) {
    var isLoggedIn by remember { mutableStateOf(nickname.isNotEmpty()) }
    var showLogin by remember { mutableStateOf(true) }
    var userRole by remember { mutableStateOf("") }

    LaunchedEffect(nickname) {
        userRole = userDao.getUserRole(nickname) ?: ""
    }

    if (isLoggedIn) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Profile", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Username: $nickname")
            Text("Rights: $userRole")

            Spacer(modifier = Modifier.height(40.dp))

            Button(onClick = {
                isLoggedIn = false
                onNicknameChange("")
            }) {
                Text("Log Out")
            }
        }
    } else {
        if (showLogin) {
            LoginScreen(userDao, onLoginSuccess = { loginNickname ->
                isLoggedIn = true
                onNicknameChange(loginNickname)
                showLogin = false
            }, onNavigateToTickets = onNavigateToFoods)
        } else {
            RegistrationScreen(userDao, onRegisterSuccess = { registerNickname ->
                isLoggedIn = true
                onNicknameChange(registerNickname)
                showLogin = true
            }, onNavigateToSearch = onNavigateToFoods)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { showLogin = !showLogin }) {
            Text(if (showLogin) "Not Registered? Sign Up" else "Already have account? Sign In")
        }
    }
}