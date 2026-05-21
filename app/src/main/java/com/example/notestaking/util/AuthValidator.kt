package com.example.notestaking.util

object AuthValidator {

    private val emailPattern = android.util.Patterns.EMAIL_ADDRESS
    fun validateFullName(name: String): String? = when {
        name.isBlank() -> "Full name is required"
        name.trim().length < 2 -> "Enter at least 2 characters"
        else -> null
    }

    fun validateEmail(email: String): String? = when {
        email.isBlank() -> "Email is required"
        !emailPattern.matcher(email.trim()).matches() -> "Enter a valid email address"
        else -> null
    }

    fun validateLoginPassword(password: String): String? = when {
        password.isBlank() -> "Password is required"
        password.length < 8 -> "Password must be at least 8 characters"
        else -> null
    }

    fun validateRegisterPassword(password: String): String? = when {
        password.isBlank() -> "Password is required"
        password.length < 8 -> "Password must be at least 8 characters"
        !password.any { it.isUpperCase() } -> "Include at least one uppercase letter"
        !password.any { it.isLowerCase() } -> "Include at least one lowercase letter"
        !password.any { it.isDigit() } -> "Include at least one number"
        else -> null
    }

    fun validateConfirmPassword(password: String, confirmPassword: String): String? = when {
        confirmPassword.isBlank() -> "Please confirm your password"
        confirmPassword != password -> "Passwords do not match"
        else -> null
    }

    fun isLoginFormValid(email: String, password: String): Boolean =
        validateEmail(email) == null && validateLoginPassword(password) == null

    fun isRegisterFormValid(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean =
        validateFullName(fullName) == null &&
            validateEmail(email) == null &&
            validateRegisterPassword(password) == null &&
            validateConfirmPassword(password, confirmPassword) == null
}
