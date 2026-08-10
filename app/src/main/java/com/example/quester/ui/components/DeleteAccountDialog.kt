package com.example.quester.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.quester.ui.screens.FantasyError
import com.example.quester.ui.screens.FantasyGold
import com.example.quester.ui.screens.FantasyGoldLight
import com.example.quester.ui.screens.FantasySurface
import com.example.quester.ui.screens.FantasyText
import com.example.quester.ui.screens.FantasyTextSecondary

@Composable
fun DeleteAccountDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var confirmText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "🗡️ Lascia il Regno",
                color = FantasyError,
                style = MaterialTheme.typography.titleLarge,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Serif
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Questa scelta è PERMANENTE e non può essere annullata.",
                    color = FantasyText,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Il tuo eroe verrà dimenticato...",
                    color = FantasyTextSecondary,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Serif
                )
                Text(
                    text = "Tutto ciò che possiedi andrà perduto:",
                    color = FantasyTextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
                Column(
                    modifier = Modifier.padding(start = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("⚔️ Le tue imprese e missioni", color = FantasyTextSecondary, style = MaterialTheme.typography.bodySmall)
                    Text("👑 I tuoi cosmetici e tesori", color = FantasyTextSecondary, style = MaterialTheme.typography.bodySmall)
                    Text("📜 Il tuo nome e la tua storia", color = FantasyTextSecondary, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Per confermare, digita il tuo nome:",
                    color = FantasyTextSecondary,
                    style = MaterialTheme.typography.bodySmall
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
                        focusedContainerColor = FantasySurface,
                        unfocusedContainerColor = FantasySurface,
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
                        "🗡️ Abbandona il Regno",
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Serif
                    )
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "Torna indietro",
                    color = FantasyGoldLight,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Serif
                )
            }
        },
        containerColor = FantasySurface,
        shape = MaterialTheme.shapes.large
    )
}