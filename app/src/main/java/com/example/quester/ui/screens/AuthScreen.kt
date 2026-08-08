package com.example.quester.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.quester.data.repository.AuthResult
import com.example.quester.domain.service.AuthService
import com.example.quester.ui.components.MagicBurstButton
import com.example.quester.ui.theme.QuesterTheme
import kotlinx.coroutines.launch

// Colori Tema Fantasy
private val FantasyBackground = Color(0xFF0D0B14)
private val FantasySurface = Color(0xFF171321)
private val FantasySurfaceLight = Color(0xFF221B2E)

private val FantasyGold = Color(0xFFD4A84F)
private val FantasyGoldLight = Color(0xFFF0CC78)
private val FantasyPurple = Color(0xFF6B4C9A)
private val FantasyPurpleDark = Color(0xFF2B1D42)

private val FantasyText = Color(0xFFF3EBD8)
private val FantasyTextSecondary = Color(0xFFC8BDA8)
private val FantasyError = Color(0xFFE57373)

private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")

data class AuthUiState(
    val isRegisterMode: Boolean = false,
    val usernameOrEmail: String = "",
    val email: String = "",
    val password: String = "",
    val usernameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val passwordVisible: Boolean = false
)

data class AuthUiActions(
    val onRegisterModeToggle: () -> Unit,
    val onUsernameOrEmailChange: (String) -> Unit,
    val onEmailChange: (String) -> Unit,
    val onPasswordChange: (String) -> Unit,
    val onTogglePasswordVisibility: () -> Unit,
    val onAuthClick: () -> Unit
)

private fun toFantasyError(message: String?): String {
    if (message.isNullOrBlank()) return "✦ Un oscuro incantesimo ha interrotto il rituale."

    val m = message.lowercase()

    return when {
        "username o email obbligatorio" in m -> "✦ L'identità dell'avventuriero è richiesta."
        "username obbligatorio" in m -> "✦ Il nome dell’avventuriero è richiesto."
        "username troppo corto" in m -> "✦ Il nome è troppo breve per entrare nelle cronache del regno."
        "email non valida" in m -> "❖ Il sigillo del corvo (email) non è valido."
        "password obbligatoria" in m -> "✦ Devi forgiare una parola segreta."
        "password troppo corta" in m -> "❖ La runa segreta è troppo debole (minimo 8 simboli)."
        "almeno 1 numero" in m -> "❖ La runa segreta deve contenere almeno un numero arcano."
        "almeno 1 maiuscola" in m -> "❖ La runa segreta deve contenere almeno una lettera nobile (maiuscola)."
        "username già esistente" in m -> "⚔ Questo nome è già preso da un altro eroe."
        "email già registrata" in m -> "❖ Questo sigillo è già legato a un eroe."
        "credenziali non valide" in m -> "ᗢ Le chiavi del portale non coincidono."
        else -> "✦ $message"
    }
}

private fun validateIdentity(identity: String, isRegisterMode: Boolean): String? = when {
    identity.isBlank() -> if (isRegisterMode) "Username obbligatorio" else "Username o email obbligatorio"
    isRegisterMode && identity.length < 3 -> "Username troppo corto (minimo 3 caratteri)"
    else -> null
}

private fun validateEmail(email: String, isRegisterMode: Boolean): String? = when {
    isRegisterMode && email.isNotBlank() && !EMAIL_REGEX.matches(email.trim()) -> "Email non valida"
    else -> null
}

private fun validatePassword(password: String): String? = when {
    password.isBlank() -> "Password obbligatoria"
    password.length < 8 -> "Password troppo corta (minimo 8 caratteri)"
    !password.any { it.isDigit() } -> "La password deve contenere almeno 1 numero"
    !password.any { it.isUpperCase() } -> "La password deve contenere almeno 1 maiuscola"
    else -> null
}

@Composable
fun AuthScreen(
    authService: AuthService,
    onAuthSuccess: () -> Unit
) {
    var isRegisterMode by rememberSaveable { mutableStateOf(false) }
    var usernameOrEmail by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    var usernameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var isLoading by remember { mutableStateOf(false) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    fun validate(): Boolean {
        usernameError = validateIdentity(usernameOrEmail.trim(), isRegisterMode)
        emailError = validateEmail(email, isRegisterMode)
        passwordError = validatePassword(password)

        return usernameError == null && emailError == null && passwordError == null
    }

    val uiState = AuthUiState(
        isRegisterMode = isRegisterMode,
        usernameOrEmail = usernameOrEmail,
        email = email,
        password = password,
        usernameError = usernameError,
        emailError = emailError,
        passwordError = passwordError,
        errorMessage = errorMessage,
        isLoading = isLoading,
        passwordVisible = passwordVisible
    )

    val uiActions = AuthUiActions(
        onRegisterModeToggle = {
            isRegisterMode = !isRegisterMode
            errorMessage = null
            usernameError = null
            emailError = null
            passwordError = null
        },
        onUsernameOrEmailChange = {
            usernameOrEmail = it
            usernameError = null
            errorMessage = null
        },
        onEmailChange = {
            email = it
            emailError = null
            errorMessage = null
        },
        onPasswordChange = {
            password = it
            passwordError = null
            errorMessage = null
        },
        onTogglePasswordVisibility = { passwordVisible = !passwordVisible },
        onAuthClick = {
            if (!validate()) return@AuthUiActions
            isLoading = true
            errorMessage = null
            scope.launch {
                try {
                    val result = if (isRegisterMode) {
                        authService.register(usernameOrEmail, email.ifBlank { null }, password)
                    } else {
                        authService.login(usernameOrEmail, password)
                    }
                    when (result) {
                        is AuthResult.Success -> onAuthSuccess()
                        is AuthResult.Error -> errorMessage = result.message
                    }
                } finally {
                    isLoading = false
                }
            }
        }
    )

    AuthContent(uiState = uiState, actions = uiActions)
}

@Composable
private fun AuthContent(
    uiState: AuthUiState,
    actions: AuthUiActions
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF120C1E), FantasyBackground, Color(0xFF0B0813))
                )
            )
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(24.dp, RoundedCornerShape(28.dp))
                .border(2.dp, FantasyPurple.copy(alpha = 0.65f), RoundedCornerShape(28.dp))
                .padding(2.dp)
                .border(1.dp, FantasyGold.copy(alpha = 0.8f), RoundedCornerShape(26.dp)),
            colors = CardDefaults.cardColors(containerColor = FantasySurface.copy(alpha = 0.98f)),
            shape = RoundedCornerShape(26.dp)
        ) {
            Column(
                Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AuthHeader(isRegisterMode = uiState.isRegisterMode)

                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = FantasyGold.copy(alpha = 0.35f))
                Spacer(Modifier.height(16.dp))

                AuthFormFields(uiState = uiState, actions = actions)

                uiState.errorMessage?.let { raw ->
                    Spacer(Modifier.height(10.dp))
                    ErrorMessageCard(fantasyError = toFantasyError(raw))
                }

                Spacer(Modifier.height(16.dp))

                AuthActionButtons(
                    isRegisterMode = uiState.isRegisterMode,
                    isLoading = uiState.isLoading,
                    onAuthClick = actions.onAuthClick,
                    onRegisterModeToggle = actions.onRegisterModeToggle
                )
            }
        }
    }
}

@Composable
private fun AuthHeader(isRegisterMode: Boolean) {
    Box(
        modifier = Modifier
            .size(66.dp)
            .background(FantasyPurpleDark, CircleShape)
            .border(1.dp, FantasyGoldLight, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Default.AutoAwesome, null, tint = FantasyGoldLight, modifier = Modifier.size(34.dp))
    }

    Spacer(Modifier.height(14.dp))
    Text("✦ QUESTER ✦", color = FantasyGoldLight, style = MaterialTheme.typography.headlineMedium)
    Text(
        if (isRegisterMode) "Crea il tuo personaggio" else "Bentornato, avventuriero",
        color = FantasyText,
        style = MaterialTheme.typography.titleLarge,
        textAlign = TextAlign.Center
    )
    Text(
        if (isRegisterMode) "Il viaggio inizia adesso" else "Il regno attende il tuo ritorno",
        color = FantasyTextSecondary,
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
private fun AuthFormFields(
    uiState: AuthUiState,
    actions: AuthUiActions
) {
    FantasyTextField(
        value = uiState.usernameOrEmail,
        onValueChange = actions.onUsernameOrEmailChange,
        label = if (uiState.isRegisterMode) "Nome avventuriero" else "Username o Email",
        error = uiState.usernameError
    )

    if (uiState.isRegisterMode) {
        Spacer(Modifier.height(10.dp))
        FantasyTextField(
            value = uiState.email,
            onValueChange = actions.onEmailChange,
            label = "Email (opzionale)",
            error = uiState.emailError,
            keyboardType = KeyboardType.Email
        )
    }

    Spacer(Modifier.height(10.dp))
    OutlinedTextField(
        value = uiState.password,
        onValueChange = actions.onPasswordChange,
        label = { Text("Password") },
        isError = uiState.passwordError != null,
        supportingText = { uiState.passwordError?.let { Text(toFantasyError(it)) } },
        visualTransformation = if (uiState.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            IconButton(onClick = actions.onTogglePasswordVisibility) {
                Icon(
                    if (uiState.passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                    contentDescription = null
                )
            }
        },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        colors = textFieldColors()
    )
}

@Composable
private fun ErrorMessageCard(fantasyError: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF3A1E24).copy(alpha = 0.9f)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = FantasyError.copy(alpha = 0.75f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = fantasyError,
            color = Color(0xFFFFCDD2),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
        )
    }
}

@Composable
private fun AuthActionButtons(
    isRegisterMode: Boolean,
    isLoading: Boolean,
    onAuthClick: () -> Unit,
    onRegisterModeToggle: () -> Unit
) {
    MagicBurstButton(
        text = if (isRegisterMode) "INIZIA L'AVVENTURA" else "ENTRA NEL REGNO",
        loading = isLoading,
        onClickAfterEffect = onAuthClick,
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(Modifier.height(10.dp))
    TextButton(onClick = onRegisterModeToggle) {
        Text(
            if (isRegisterMode) "Hai già un account? Accedi" else "Non hai un account? Registrati",
            color = FantasyGoldLight
        )
    }
}

@Composable
private fun FantasyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = error != null,
        supportingText = { error?.let { Text(toFantasyError(it)) } },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        colors = textFieldColors()
    )
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = FantasySurfaceLight,
    unfocusedContainerColor = FantasySurfaceLight,
    focusedBorderColor = FantasyGold,
    unfocusedBorderColor = FantasyGold.copy(alpha = 0.35f),
    focusedLabelColor = FantasyGoldLight,
    unfocusedLabelColor = FantasyTextSecondary,
    focusedTextColor = FantasyText,
    unfocusedTextColor = FantasyText,
    cursorColor = FantasyGold,
    focusedTrailingIconColor = FantasyGoldLight,
    unfocusedTrailingIconColor = FantasyTextSecondary,
    errorBorderColor = FantasyError,
    errorLabelColor = FantasyError,
    errorSupportingTextColor = FantasyError
)

@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
fun AuthScreenPreview() {
    QuesterTheme {
        AuthContent(
            uiState = AuthUiState(
                isRegisterMode = false,
                usernameOrEmail = "mario",
                password = "Password1",
                errorMessage = "Credenziali non valide"
            ),
            actions = AuthUiActions(
                onRegisterModeToggle = {},
                onUsernameOrEmailChange = {},
                onEmailChange = {},
                onPasswordChange = {},
                onTogglePasswordVisibility = {},
                onAuthClick = {}
            )
        )
    }
}