package com.example.notestaking.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notestaking.data.repository.AuthRepository
import com.example.notestaking.util.AuthValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val rememberMe: Boolean = true,
    val passwordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val authError: String? = null,
    val showValidationErrors: Boolean = false,
    val loginSuccess: Boolean = false
) {
    val isFormValid: Boolean
        get() = AuthValidator.isLoginFormValid(email, password)
}

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.update {
            val errors = if (it.showValidationErrors) validateFields(value, it.password) else null
            it.copy(
                email = value,
                emailError = errors?.emailError,
                passwordError = errors?.passwordError,
                authError = null
            )
        }
    }

    fun onPasswordChange(value: String) {
        _uiState.update {
            val errors = if (it.showValidationErrors) validateFields(it.email, value) else null
            it.copy(
                password = value,
                emailError = errors?.emailError,
                passwordError = errors?.passwordError,
                authError = null
            )
        }
    }

    fun onRememberChange(value: Boolean) = _uiState.update { it.copy(rememberMe = value) }
    fun togglePasswordVisibility() = _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }

    fun login() {
        val validated = validateFields(_uiState.value.email, _uiState.value.password)
        _uiState.update {
            it.copy(
                showValidationErrors = true,
                emailError = validated.emailError,
                passwordError = validated.passwordError,
                authError = null
            )
        }
        if (validated.emailError != null || validated.passwordError != null) return

        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, authError = null) }
            val result = authRepository.login(state.email, state.password, state.rememberMe)
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, loginSuccess = true) }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            authError = e.message ?: "Invalid email or password"
                        )
                    }
                }
            )
        }
    }

    fun consumeSuccess() = _uiState.update { it.copy(loginSuccess = false) }

    private fun validateFields(email: String, password: String): LoginUiState {
        return LoginUiState(
            emailError = AuthValidator.validateEmail(email),
            passwordError = AuthValidator.validateLoginPassword(password)
        )
    }
}
