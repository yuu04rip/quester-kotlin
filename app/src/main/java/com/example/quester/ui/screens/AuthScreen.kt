package com.example.quester.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.quester.data.repository.AuthResult
import com.example.quester.domain.service.AuthService
import com.example.quester.ui.theme.QuesterTheme
import kotlinx.coroutines.launch


@Composable
fun AuthScreen(
    authService: AuthService,
    onAuthSuccess: () -> Unit
) {
    // State for switching between Login and Register modes
    var isRegisterMode by rememberSaveable { mutableStateOf(false) }

    // Input states for Username and Password
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    // Error states for validation
    var usernameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Loading state for the auth process
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    // Validation logic
    fun validate(): Boolean {
        var isValid = true
        if (username.isBlank()) {
            usernameError = "Inserisci un nome utente valido"
            isValid = false
        } else {
            usernameError = null
        }

        if (password.length < 6) {
            passwordError = "La password deve avere almeno 6 caratteri"
            isValid = false
        } else {
            passwordError = null
        }
        return isValid
    }

    AuthContent(
        isRegisterMode = isRegisterMode,
        onRegisterModeChange = {
            isRegisterMode = !isRegisterMode
            errorMessage = null
            // Reset validation errors when switching modes
            usernameError = null
            passwordError = null
        },
        username = username,
        onUsernameChange = {
            username = it
            usernameError = null
            errorMessage = null
        },
        password = password,
        onPasswordChange = {
            password = it
            passwordError = null
            errorMessage = null
        },
        usernameError = usernameError,
        passwordError = passwordError,
        errorMessage = errorMessage,
        isLoading = isLoading,
        onAuthClick = {
            if (validate()) {
                isLoading = true
                errorMessage = null
                scope.launch {
                    try {
                        // Link buttons to AuthService methods
                        val result = if (isRegisterMode) {
                            authService.register(username, password)
                        } else {
                            authService.login(username, password)
                        }

                        when (result) {
                            is AuthResult.Success -> {
                                onAuthSuccess()
                            }
                            is AuthResult.Error -> {
                                errorMessage = result.message
                            }
                        }
                    } catch (e: Exception) {
                        errorMessage = "Errore di connessione: ${e.message}"
                    } finally {
                        isLoading = false
                    }
                }
            }
        }
    )
}

@Composable
private fun AuthContent(
    isRegisterMode: Boolean,
    onRegisterModeChange: () -> Unit,
    username: String,
    onUsernameChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    usernameError: String?,
    passwordError: String?,
    errorMessage: String?,
    isLoading: Boolean,
    onAuthClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isRegisterMode) "Registrati" else "Accedi",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Campo Username
            OutlinedTextField(
                value = username,
                onValueChange = onUsernameChange,
                label = { Text("Username") },
                isError = usernameError != null,
                supportingText = { usernameError?.let { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo Password
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("Password") },
                isError = passwordError != null,
                supportingText = { passwordError?.let { Text(it) } },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Pulsante di Invio (Login o Registrazione)
            Button(
                onClick = onAuthClick,
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(if (isRegisterMode) "Registrati" else "Accedi")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Switch tra Login e Registrazione
            TextButton(
                onClick = onRegisterModeChange
            ) {
                Text(
                    if (isRegisterMode)
                        "Hai già un account? Accedi"
                    else
                        "Non hai un account? Registrati"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AuthScreenPreview() {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRegisterMode by remember { mutableStateOf(false) }

    QuesterTheme {
        AuthContent(
            isRegisterMode = isRegisterMode,
            onRegisterModeChange = { isRegisterMode = !isRegisterMode },
            username = username,
            onUsernameChange = { username = it },
            password = password,
            onPasswordChange = { password = it },
            usernameError = null,
            passwordError = null,
            errorMessage = null,
            isLoading = false,
            onAuthClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Register Mode")
@Composable
fun AuthScreenRegisterPreview() {
    QuesterTheme {
        AuthContent(
            isRegisterMode = true,
            onRegisterModeChange = {},
            username = "new_user",
            onUsernameChange = {},
            password = "password123",
            onPasswordChange = {},
            usernameError = null,
            passwordError = null,
            errorMessage = null,
            isLoading = false,
            onAuthClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Error State")
@Composable
fun AuthScreenErrorPreview() {
    QuesterTheme {
        AuthContent(
            isRegisterMode = false,
            onRegisterModeChange = {},
            username = "user",
            onUsernameChange = {},
            password = "123",
            onPasswordChange = {},
            usernameError = null,
            passwordError = "La password deve avere almeno 6 caratteri",
            errorMessage = "Credenziali non valide",
            isLoading = false,
            onAuthClick = {}
        )
    }
}
