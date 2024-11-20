package com.example.macronutrient.dao

import androidx.room.*
import com.example.macronutrient.entity.User

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE nickname = :nickname AND password = :password")
    suspend fun getUser(nickname: String, password: String): User?

    @Query("SELECT * FROM users WHERE role = :role")
    suspend fun getUsersByRole(role: String): List<User> // Получение пользователей по роли

    @Query("SELECT role FROM users WHERE nickname = :nickname")
    suspend fun getUserRole(nickname: String): String? // Получение роли пользователя по никнейму

    @Query("SELECT id FROM users WHERE nickname = :nickname")
    suspend fun getUserIdByNickname(nickname: String): Int?
}

class MockUserDao : UserDao {
    private val users = mutableListOf<User>()

    override suspend fun insertUser(user: User) {
        users.add(user)
    }

    override suspend fun getUser(nickname: String, password: String): User? {
        return users.find { it.nickname == nickname && it.password == password }
    }

    override suspend fun getUsersByRole(role: String): List<User> {
        return users.filter { it.role == role }
    }

    override suspend fun getUserRole(nickname: String): String? {
        return users.find { it.nickname == nickname }?.role
    }

    override suspend fun getUserIdByNickname(nickname: String): Int? {
        return users.find {it.nickname == nickname}?.id
    }
}