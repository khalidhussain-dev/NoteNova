package com.example.notestaking.data.repository

import com.example.notestaking.data.local.dao.UserDao
import com.example.notestaking.data.local.entity.UserEntity
import com.example.notestaking.data.preferences.SessionManager
import com.example.notestaking.util.PasswordHasher

class AuthRepository(
    private val userDao: UserDao,
    private val sessionManager: SessionManager
) {

    suspend fun register(
        fullName: String,
        email: String,
        password: String
    ): Result<Long> {
        val normalizedEmail = email.trim().lowercase()
        if (userDao.getByEmail(normalizedEmail) != null) {
            return Result.failure(Exception("An account with this email already exists"))
        }
        val salt = PasswordHasher.generateSalt()
        val hash = PasswordHasher.hash(password, salt)
        val user = UserEntity(
            fullName = fullName.trim(),
            email = normalizedEmail,
            passwordHash = hash,
            salt = salt
        )
        return try {
            val id = userDao.insert(user)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(Exception("Registration failed. Please try again."))
        }
    }

    suspend fun login(
        email: String,
        password: String,
        remember: Boolean
    ): Result<UserEntity> {
        val normalizedEmail = email.trim().lowercase()
        val user = userDao.getByEmail(normalizedEmail)
            ?: return Result.failure(Exception("Invalid email or password"))
        if (!PasswordHasher.verify(password, user.salt, user.passwordHash)) {
            return Result.failure(Exception("Invalid email or password"))
        }
        sessionManager.saveSession(
            userId = user.id,
            email = user.email,
            fullName = user.fullName,
            remember = remember
        )
        return Result.success(user)
    }

    suspend fun logout() {
        sessionManager.clearSession()
    }
}
