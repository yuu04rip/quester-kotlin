package com.example.quester.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.quester.ui.screens.FantasyError
import com.example.quester.ui.screens.FantasyGold
import com.example.quester.ui.screens.FantasyGoldLight
import com.example.quester.ui.screens.FantasySurface
import com.example.quester.ui.screens.FantasyText
import com.example.quester.ui.screens.FantasyTextSecondary
import com.example.quester.ui.theme.AppTheme
import com.example.quester.ui.theme.QuesterFantasy
import com.example.quester.ui.theme.QuesterPixel
import com.example.quester.ui.theme.ThemeManager

@Composable
fun DeleteAccountDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var confirmText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val isArcade = ThemeManager.theme == AppTheme.ARCADE
    // ✅ Font per i testi: QuesterFantasy per Fantasy, QuesterPixel per Arcade
    val textFont = if (isArcade) QuesterPixel else QuesterFantasy

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Lascia il Regno",
                color = FantasyError,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = textFont  // ✅ Font speciale per il titolo
                )
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Questa scelta è PERMANENTE e non può essere annullata.",
                    color = FantasyText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = textFont  // ✅ Font speciale
                    )
                )
                Text(
                    text = "Il tuo eroe verrà dimenticato...",
                    color = FantasyTextSecondary,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = textFont  // ✅ Font speciale
                    )
                )
                Text(
                    text = "Tutto ciò che possiedi andrà perduto:",
                    color = FantasyTextSecondary,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = textFont  // ✅ Font speciale
                    )
                )
                Column(
                    modifier = Modifier.padding(start = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Le tue imprese e missioni",
                        color = FantasyTextSecondary,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = textFont  // ✅ Font speciale
                        )
                    )
                    Text(
                        text = "I tuoi cosmetici e tesori",
                        color = FantasyTextSecondary,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = textFont  // ✅ Font speciale
                        )
                    )
                    Text(
                        text = "Il tuo nome e la tua storia",
                        color = FantasyTextSecondary,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = textFont  // ✅ Font speciale
                        )
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Per confermare, digita il tuo nome:",
                    color = FantasyTextSecondary,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = textFont  // ✅ Font speciale
                    )
                )
                OutlinedTextField(
                    value = confirmText,
                    onValueChange = {
                        confirmText = it
                        error = null
                    },
                    label = { Text("Il tuo nome", color = FantasyTextSecondary) },
                    isError = error != null,
                    supportingText = {
                        if (error != null) {
                            Text(error ?: "", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = FantasySurface.copy(alpha = 0.8f),
                        unfocusedContainerColor = FantasySurface.copy(alpha = 0.8f),
                        focusedBorderColor = FantasyError,
                        unfocusedBorderColor = FantasyError.copy(alpha = 0.3f),
                        focusedLabelColor = FantasyError,
                        unfocusedLabelColor = FantasyTextSecondary,
                        focusedTextColor = FantasyText,
                        unfocusedTextColor = FantasyText,
                        cursorColor = FantasyError,
                        errorBorderColor = MaterialTheme.colorScheme.error
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (confirmText.isBlank()) {
                        error = "✦ Scrivi il tuo nome per confermare"
                        return@Button
                    }
                    isLoading = true
                    onConfirm()
                },
                enabled = confirmText.isNotBlank() && !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = FantasyError,
                    contentColor = FantasyText
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text(
                        text = "Abbandona il Regno",
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        fontFamily = textFont  // ✅ Font speciale
                    )
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Torna indietro",
                    color = FantasyGoldLight,
                    fontFamily = textFont  // ✅ Font speciale
                )
            }
        },
        containerColor = FantasySurface.copy(alpha = 0.92f),
        shape = MaterialTheme.shapes.large
    )
}