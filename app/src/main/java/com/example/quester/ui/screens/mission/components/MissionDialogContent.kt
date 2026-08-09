package com.example.quester.ui.screens.mission.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quester.ui.screens.*
import com.example.quester.ui.screens.mission.model.MissionType

data class MissionDialogState(
    val title: String,
    val description: String,
    val selectedType: MissionType,
    val xpReward: String,
    val subtasks: List<String>
)

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
    xpError: String? = null,
    titleError: String? = null,
    subtaskError: String? = null,
    callbacks: MissionDialogCallbacks
) {
    AlertDialog(
        onDismissRequest = callbacks.onDismiss,
        title = {
            Text(
                text = dialogTitle,
                color = FantasyGoldLight,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            DialogContent(
                state = state,
                xpError = xpError,
                titleError = titleError,
                subtaskError = subtaskError,
                callbacks = callbacks
            )
        },
        confirmButton = {
            ConfirmButton(
                onClick = callbacks.onConfirm,
                text = confirmButtonText
            )
        },
        dismissButton = {
            DismissButton(onClick = callbacks.onDismiss)
        },
        containerColor = FantasySurface,
        shape = MaterialTheme.shapes.large
    )
}

// ===== DIALOG CONTENT =====

@Composable
private fun DialogContent(
    state: MissionDialogState,
    xpError: String?,
    titleError: String?,
    subtaskError: String?,
    callbacks: MissionDialogCallbacks
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TitleField(
            value = state.title,
            onValueChange = callbacks.onTitleChange,
            error = titleError
        )

        DescriptionField(
            value = state.description,
            onValueChange = callbacks.onDescriptionChange
        )

        MissionTypeSelector(
            selectedType = state.selectedType,
            onTypeChange = callbacks.onTypeChange
        )

        XpField(
            value = state.xpReward,
            onValueChange = callbacks.onXpRewardChange,
            selectedType = state.selectedType,
            error = xpError
        )

        SubtasksSection(
            subtasks = state.subtasks,
            onSubtasksChange = callbacks.onSubtasksChange,
            error = subtaskError
        )
    }
}

// ===== TITLE FIELD =====

@Composable
private fun TitleField(
    value: String,
    onValueChange: (String) -> Unit,
    error: String?
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Titolo") },
        isError = error != null,
        supportingText = {
            if (error != null) {
                ErrorText(error)
            }
        },
        modifier = Modifier.fillMaxWidth(),
        colors = dialogTextFieldColors(),
        singleLine = true
    )
}

// ===== DESCRIPTION FIELD =====

@Composable
private fun DescriptionField(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Descrizione (opzionale)") },
        modifier = Modifier.fillMaxWidth(),
        colors = dialogTextFieldColors(),
        minLines = 2,
        maxLines = 4
    )
}

// ===== MISSION TYPE SELECTOR =====

@Composable
private fun MissionTypeSelector(
    selectedType: MissionType,
    onTypeChange: (MissionType) -> Unit
) {
    Column {
        Text(
            text = "Tipo Missione",
            color = FantasyTextSecondary,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MissionType.entries.forEach { type ->
                FilterChip(
                    selected = selectedType == type,
                    onClick = { onTypeChange(type) },
                    label = { Text(type.label, fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = FantasyGold.copy(alpha = 0.3f),
                        selectedLabelColor = FantasyGoldLight,
                        disabledContainerColor = FantasySurfaceLight,
                        disabledLabelColor = FantasyTextSecondary
                    )
                )
            }
        }
    }
}

// ===== XP FIELD =====

@Composable
private fun XpField(
    value: String,
    onValueChange: (String) -> Unit,
    selectedType: MissionType,
    error: String?
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text("XP") },
            isError = error != null,
            supportingText = {
                if (error != null) {
                    ErrorText(error)
                } else {
                    InfoText("✦ Range: ${selectedType.minXp} - ${selectedType.maxXp} XP")
                }
            },
            placeholder = {
                Text(
                    "Default: ${selectedType.defaultXp}",
                    color = FantasyTextSecondary.copy(alpha = 0.5f)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = dialogTextFieldColors(),
            singleLine = true
        )

        if (error != null && value.isNotBlank()) {
            XpWarningBox(error)
        }
    }
}

// ===== XP WARNING BOX =====

@Composable
private fun XpWarningBox(error: String) {
    Spacer(modifier = Modifier.height(4.dp))
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFFF6B6B).copy(alpha = 0.15f),
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                tint = Color(0xFFFF6B6B),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "⚠️ $error",
                color = Color(0xFFFF6B6B),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ===== SUBTASKS SECTION =====

@Composable
private fun SubtasksSection(
    subtasks: List<String>,
    onSubtasksChange: (List<String>) -> Unit,
    error: String?
) {
    Column {
        Text(
            text = "Sub-tasks",
            color = FantasyTextSecondary,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        subtasks.forEachIndexed { index, task ->
            SubtaskRow(
                task = task,
                index = index,
                subtasks = subtasks,
                onSubtasksChange = onSubtasksChange,
                isError = error != null && index == subtasks.lastIndex
            )
        }

        // FIX: Usa un Row con il modificatore invece di Modifier.align
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            AddSubtaskButton(
                onSubtasksChange = onSubtasksChange,
                currentSubtasks = subtasks
            )
        }

        if (error != null) {
            ErrorText(error)
        }
    }
}

// ===== SUBTASK ROW =====

@Composable
private fun SubtaskRow(
    task: String,
    index: Int,
    subtasks: List<String>,
    onSubtasksChange: (List<String>) -> Unit,
    isError: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        OutlinedTextField(
            value = task,
            onValueChange = { newTask ->
                val newList = subtasks.toMutableList()
                newList[index] = newTask
                onSubtasksChange(newList)
            },
            isError = isError,
            modifier = Modifier.weight(1f),
            colors = dialogTextFieldColors(),
            singleLine = true,
            placeholder = {
                Text(
                    if (index == 0) "Sub-task 1" else "Sub-task ${index + 1}",
                    color = FantasyTextSecondary.copy(alpha = 0.5f)
                )
            }
        )
        if (subtasks.size > 1) {
            RemoveSubtaskButton(
                index = index,
                subtasks = subtasks,
                onSubtasksChange = onSubtasksChange
            )
        }
    }
}

// ===== SUBTASK BUTTONS =====

@Composable
private fun RemoveSubtaskButton(
    index: Int,
    subtasks: List<String>,
    onSubtasksChange: (List<String>) -> Unit
) {
    IconButton(
        onClick = {
            val newList = subtasks.toMutableList()
            newList.removeAt(index)
            onSubtasksChange(newList)
        },
        modifier = Modifier.size(36.dp)
    ) {
        Text("✕", color = Color(0xFFFF6B6B))
    }
}

@Composable
private fun AddSubtaskButton(
    onSubtasksChange: (List<String>) -> Unit,
    currentSubtasks: List<String>
) {
    TextButton(
        onClick = { onSubtasksChange(currentSubtasks + "") }
    ) {
        Text("+ Aggiungi subtask", color = FantasyGoldLight, fontSize = 12.sp)
    }
}

// ===== BUTTONS =====

@Composable
private fun ConfirmButton(
    onClick: () -> Unit,
    text: String
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = FantasyGold,
            contentColor = Color(0xFF0D0B14)
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(text, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun DismissButton(
    onClick: () -> Unit
) {
    TextButton(onClick = onClick) {
        Text("Annulla", color = FantasyTextSecondary)
    }
}

// ===== TEXT HELPERS =====

@Composable
private fun ErrorText(text: String) {
    Text(
        text = text,
        color = Color(0xFFFF6B6B),
        fontSize = 12.sp,
        modifier = Modifier.padding(start = 4.dp)
    )
}

@Composable
private fun InfoText(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        color = FantasyTextSecondary.copy(alpha = 0.7f)
    )
}

// ===== TEXT FIELD COLORS =====

@Composable
private fun dialogTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = FantasySurfaceLight,
    unfocusedContainerColor = FantasySurfaceLight,
    focusedBorderColor = FantasyGold,
    unfocusedBorderColor = FantasyGold.copy(alpha = 0.35f),
    focusedLabelColor = FantasyGoldLight,
    unfocusedLabelColor = FantasyTextSecondary,
    focusedTextColor = FantasyText,
    unfocusedTextColor = FantasyText,
    cursorColor = FantasyGold,
    errorBorderColor = Color(0xFFFF6B6B),
    errorLabelColor = Color(0xFFFF6B6B),
    errorSupportingTextColor = Color(0xFFFF6B6B)
)