package com.example.notestaking.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    factory: ViewModelFactory,
    onNavigateBack: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val viewModel: RegisterViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.registerSuccess) {
        if (uiState.registerSuccess) {
            viewModel.consumeSuccess()
            onRegisterSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Account") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Join NoteNova",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(Modifier.height(24.dp))
            Card(elevation = CardDefaults.cardElevation(4.dp)) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AuthTextField(
                        value = uiState.fullName,
                        onValueChange = viewModel::onFullNameChange,
                        label = "Full Name",
                        leadingIcon = Icons.Filled.Person,
                        isError = uiState.fullNameError != null,
                        supportingText = uiState.fullNameError
                    )
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
                            ?: if (!uiState.showValidationErrors) {
                                "Min 8 chars with upper, lower, and number"
                            } else null
                    )
                    AuthTextField(
                        value = uiState.confirmPassword,
                        onValueChange = viewModel::onConfirmPasswordChange,
                        label = "Confirm Password",
                        leadingIcon = Icons.Filled.Lock,
                        isPassword = true,
                        passwordVisible = uiState.confirmPasswordVisible,
                        onTogglePassword = viewModel::toggleConfirmPasswordVisibility,
                        isError = uiState.confirmPasswordError != null,
                        supportingText = uiState.confirmPasswordError
                    )
                    uiState.serverError?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    NovaButton(
                        text = "Create Account",
                        onClick = viewModel::register,
                        isLoading = uiState.isLoading,
                        enabled = uiState.isFormValid
                    )
                }
            }
        }
    }
}
