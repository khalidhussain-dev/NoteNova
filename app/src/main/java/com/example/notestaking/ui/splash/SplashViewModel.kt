package com.example.notestaking.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notestaking.data.preferences.SessionManager
import com.example.notestaking.data.preferences.SessionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class SplashUiState(
    val isLoading: Boolean = true,
    val destination: SplashDestination? = null
)

enum class SplashDestination {
    HOME,
    LOGIN
}

class SplashViewModel(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            kotlinx.coroutines.delay(1800)
            val session = sessionManager.sessionFlow.first()
            val destination = when (session) {
                is SessionState.LoggedIn -> SplashDestination.HOME
                SessionState.LoggedOut -> SplashDestination.LOGIN
            }
            _uiState.value = SplashUiState(isLoading = false, destination = destination)
        }
    }
}
