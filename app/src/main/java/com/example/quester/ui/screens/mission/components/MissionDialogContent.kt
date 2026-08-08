package com.example.quester.ui.screens.mission.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.quester.ui.screens.mission.model.MissionType

// Data class per raggruppare i parametri
data class MissionDialogState(
    val title: String,
    val description: String,
    val selectedType: MissionType,
    val xpReward: String,
    val subtasks: List<String>
)

// Data class per i callback
data class MissionDialogCallbacks(
    val onTitleChange: (String) -> Unit,
    val onDescriptionChange: (String) -> Unit,
    val onTypeChange: (MissionType) -> Unit,
    val onXpRewardChange: (String) -> Unit,
    val onSubtasksChange: (List<String>) -> Unit,
    val onConfirm: () -> Unit,
    val onDismiss: () -> Unit
)

@Composable
fun MissionDialogContent(
    state: MissionDialogState,
    dialogTitle: String,
    confirmButtonText: String,
    callbacks: MissionDialogCallbacks
) {
    AlertDialog(
        onDismissRequest = callbacks.onDismiss,
        confirmButton = {
            Button(onClick = callbacks.onConfirm) {
                Text(confirmButtonText)
            }
        },
        dismissButton = {
            TextButton(onClick = callbacks.onDismiss) {
                Text("Annulla")
            }
        },
        title = {
            Text(text = dialogTitle, style = MaterialTheme.typography.headlineSmall)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Titolo
                OutlinedTextField(
                    value = state.title,
                    onValueChange = callbacks.onTitleChange,
                    label = { Text("Titolo Missione") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Descrizione
                OutlinedTextField(
                    value = state.description,
                    onValueChange = callbacks.onDescriptionChange,
                    label = { Text("Descrizione") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                // Tipo missione
                Text(
                    text = "Tipo di Missione:",
                    style = MaterialTheme.typography.labelLarge
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MissionType.entries.forEach { type ->
                        FilterChip(
                            selected = state.selectedType == type,
                            onClick = { callbacks.onTypeChange(type) },
                            label = { Text(type.label) }
                        )
                    }
                }

                // XP Reward
                OutlinedTextField(
                    value = state.xpReward,
                    onValueChange = callbacks.onXpRewardChange,
                    label = { Text("Ricompensa XP") },
                    supportingText = {
                        Text("Range consentito: ${state.selectedType.minXp} - ${state.selectedType.maxXp} XP")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider()

                // Subtasks
                Text(
                    text = "Task della missione:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                state.subtasks.forEachIndexed { index, subtask ->
                    SubtaskRow(
                        index = index,
                        subtask = subtask,
                        subtasks = state.subtasks,
                        onSubtasksChange = callbacks.onSubtasksChange
                    )
                }

                // Pulsante aggiungi task
                TextButton(
                    onClick = { callbacks.onSubtasksChange(state.subtasks + "") },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Aggiungi Task")
                }
            }
        }
    )
}

@Composable
private fun SubtaskRow(
    index: Int,
    subtask: String,
    subtasks: List<String>,
    onSubtasksChange: (List<String>) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = subtask,
            onValueChange = { newValue ->
                val newList = subtasks.toMutableList()
                newList[index] = newValue
                onSubtasksChange(newList)
            },
            label = { Text("Task ${index + 1}") },
            singleLine = true,
            modifier = Modifier.weight(1f)
        )
        IconButton(
            onClick = {
                val newList = subtasks.toMutableList()
                newList.removeAt(index)
                onSubtasksChange(newList)
            }
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Rimuovi task",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}