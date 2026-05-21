package com.example.notestaking.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notestaking.data.preferences.SessionManager
import com.example.notestaking.data.repository.AuthRepository
import com.example.notestaking.util.AuthValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegisterUiState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val passwordVisible: Boolean = false,
    val confirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val fullNameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val serverError: String? = null,
    val showValidationErrors: Boolean = false,
    val registerSuccess: Boolean = false
) {
    val isFormValid: Boolean
        get() = AuthValidator.isRegisterFormValid(fullName, email, password, confirmPassword)
}

class RegisterViewModel(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onFullNameChange(value: String) = updateField { validate(it.copy(fullName = value)) }
    fun onEmailChange(value: String) = updateField { validate(it.copy(email = value)) }
    fun onPasswordChange(value: String) = updateField { validate(it.copy(password = value)) }
    fun onConfirmPasswordChange(value: String) = updateField { validate(it.copy(confirmPassword = value)) }

    fun togglePasswordVisibility() = _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    fun toggleConfirmPasswordVisibility() =
        _uiState.update { it.copy(confirmPasswordVisible = !it.confirmPasswordVisible) }

    fun register() {
        val state = _uiState.value
        val validated = validate(state.copy(showValidationErrors = true))
        _uiState.value = validated
        if (!validated.isFormValid) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, serverError = null) }
            val result = authRepository.register(state.fullName, state.email, state.password)
            result.fold(
                onSuccess = { userId ->
                    sessionManager.saveSession(
                        userId = userId,
                        email = state.email.trim().lowercase(),
                        fullName = state.fullName.trim(),
                        remember = true
                    )
                    _uiState.update { it.copy(isLoading = false, registerSuccess = true) }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(isLoading = false, serverError = e.message ?: "Registration failed")
                    }
                }
            )
        }
    }

    fun consumeSuccess() = _uiState.update { it.copy(registerSuccess = false) }

    private fun updateField(transform: (RegisterUiState) -> RegisterUiState) {
        _uiState.update { current ->
            val updated = transform(current.copy(serverError = null))
            if (current.showValidationErrors) validate(updated) else updated
        }
    }

    private fun validate(state: RegisterUiState): RegisterUiState = state.copy(
        fullNameError = AuthValidator.validateFullName(state.fullName),
        emailError = AuthValidator.validateEmail(state.email),
        passwordError = AuthValidator.validateRegisterPassword(state.password),
        confirmPasswordError = AuthValidator.validateConfirmPassword(state.password, state.confirmPassword)
    )
}
