package com.example.notestaking.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notestaking.di.ViewModelFactory
import com.example.notestaking.ui.components.AuthTextField
import com.example.notestaking.ui.components.NovaButton

@Composable
fun LoginScreen(
    factory: ViewModelFactory,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val viewModel: LoginViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            viewModel.consumeSuccess()
            onLoginSuccess()
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically { it / 4 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Welcome back", style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Sign in to NoteNova",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                    Spacer(Modifier.height(32.dp))
                    Card(
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            AuthTextField(
                                value = uiState.email,
                                onValueChange = viewModel::onEmailChange,
                                label = "Email",
                                leadingIcon = Icons.Filled.Email,
                                keyboardType = KeyboardType.Email,
                                isError = uiState.emailError != null,
                                supportingText = uiState.emailError
                            )
                            AuthTextField(
                                value = uiState.password,
                                onValueChange = viewModel::onPasswordChange,
                                label = "Password",
                                leadingIcon = Icons.Filled.Lock,
                                isPassword = true,
                                passwordVisible = uiState.passwordVisible,
                                onTogglePassword = viewModel::togglePasswordVisibility,
                                isError = uiState.passwordError != null,
                                supportingText = uiState.passwordError
                            )
                            AnimatedVisibility(visible = uiState.authError != null) {
                                Text(
                                    text = uiState.authError.orEmpty(),
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = uiState.rememberMe,
                                    onCheckedChange = viewModel::onRememberChange
                                )
                                Text("Remember me", style = MaterialTheme.typography.bodyMedium)
                            }
                            NovaButton(
                                text = "Sign In",
                                onClick = viewModel::login,
                                isLoading = uiState.isLoading,
                                enabled = uiState.isFormValid
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    TextButton(onClick = onNavigateToRegister) {
                        Text("Don't have an account? Create one")
                    }
                }
            }
        }
    }
}
