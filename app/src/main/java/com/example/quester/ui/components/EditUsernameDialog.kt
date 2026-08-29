package com.example.quester.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.quester.ui.screens.FantasyGold
import com.example.quester.ui.screens.FantasyGoldLight
import com.example.quester.ui.screens.FantasySurface
import com.example.quester.ui.screens.FantasyText
import com.example.quester.ui.screens.FantasyTextSecondary
import com.example.quester.ui.theme.AppTheme
import com.example.quester.ui.theme.QuesterFantasy
import com.example.quester.ui.theme.QuesterPixel
import com.example.quester.ui.theme.ThemeManager
import com.example.quester.ui.utils.capitalizeFirstLetter

@Composable
fun EditUsernameDialog(
    currentUsername: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    error: String? = null
) {
    var newUsername by remember { mutableStateOf(currentUsername) }
    var localError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val displayError = error ?: localError
    val isArcade = ThemeManager.theme == AppTheme.ARCADE
    // Font per tutti i testi: QuesterPixel per Arcade, QuesterFantasy per Fantasy
    val textFont = if (isArcade) QuesterPixel else QuesterFantasy

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "✦ Cambia Nome",
                color = FantasyGoldLight,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = textFont  // Font speciale
                )
            )
        },
        text = {
            Column {
                Text(
                    text = "Scegli un nuovo nome per il tuo avventuriero:",
                    color = FantasyTextSecondary,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = textFont  // Font speciale
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = newUsername,
                    onValueChange = {
                        newUsername = it
                        localError = null
                    },
                    label = { Text("Nuovo nome", color = FantasyTextSecondary) },
                    isError = displayError != null,
                    supportingText = {
                        if (displayError != null) {
                            Text(displayError, color = MaterialTheme.colorScheme.error)
                        } else {
                            Text("Minimo 3 caratteri", color = FantasyTextSecondary.copy(alpha = 0.5f))
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = FantasySurface.copy(alpha = 0.8f),
                        unfocusedContainerColor = FantasySurface.copy(alpha = 0.8f),
                        focusedBorderColor = FantasyGold,
                        unfocusedBorderColor = FantasyGold.copy(alpha = 0.3f),
                        focusedLabelColor = FantasyGoldLight,
                        unfocusedLabelColor = FantasyTextSecondary,
                        focusedTextColor = FantasyText,
                        unfocusedTextColor = FantasyText,
                        cursorColor = FantasyGold,
                        errorBorderColor = MaterialTheme.colorScheme.error
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val trimmed = newUsername.trim()
                    when {
                        trimmed.isBlank() -> localError = "✦ Il nome non può essere vuoto"
                        trimmed.length < 3 -> localError = "✦ Minimo 3 caratteri"
                        trimmed == currentUsername -> {
                            onDismiss()
                        }
                        else -> {
                            isLoading = true
                            val capitalized = capitalizeFirstLetter(trimmed)
                            onConfirm(capitalized)
                        }
                    }
                },
                enabled = !isLoading && newUsername.trim() != currentUsername,
                colors = ButtonDefaults.buttonColors(
                    containerColor = FantasyGold,
                    contentColor = Color(0xFF0D0B14)
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text(
                        text = "Salva",
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        fontFamily = textFont  // Font speciale
                    )
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Annulla",
                    color = FantasyTextSecondary,
                    fontFamily = textFont  // Font speciale
                )
            }
        },
        containerColor = FantasySurface.copy(alpha = 0.92f),
        shape = MaterialTheme.shapes.large
    )
}