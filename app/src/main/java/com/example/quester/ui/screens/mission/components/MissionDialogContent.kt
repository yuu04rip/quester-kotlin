package com.example.quester.ui.screens.mission.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quester.R
import com.example.quester.ui.screens.mission.model.MissionType

data class MissionDialogState(
    val title: String,
    val description: String,
    val selectedType: MissionType,
    val subtasks: List<String>
)

data class MissionDialogCallbacks(
    val onTitleChange: (String) -> Unit,
    val onDescriptionChange: (String) -> Unit,
    val onTypeChange: (MissionType) -> Unit,
    val onSubtasksChange: (List<String>) -> Unit,
    val onConfirm: () -> Unit,
    val onDismiss: () -> Unit
)

@Composable
fun MissionDialogContent(
    state: MissionDialogState,
    dialogTitle: String,
    confirmButtonText: String,
    titleError: String? = null,
    subtaskError: String? = null,
    callbacks: MissionDialogCallbacks
) {
    AlertDialog(
        onDismissRequest = callbacks.onDismiss,
        title = {
            Text(
                text = dialogTitle,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            DialogContent(
                state = state,
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
        containerColor = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.large
    )
}

@Composable
private fun DialogContent(
    state: MissionDialogState,
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

        RewardsInfo(selectedType = state.selectedType)

        SubtasksSection(
            subtasks = state.subtasks,
            onSubtasksChange = callbacks.onSubtasksChange,
            error = subtaskError
        )
    }
}

@Composable
private fun RewardsInfo(selectedType: MissionType) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // XP con icona star.png
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painter = painterResource(id = R.drawable.star),
                    contentDescription = "XP",
                    modifier = Modifier.size(20.dp),
                    tint = Color.Unspecified
                )
                Text(
                    text = "+${selectedType.xpReward}",
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            // Monete con icona coin.png
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painter = painterResource(id = R.drawable.coin),
                    contentDescription = "Monete",
                    modifier = Modifier.size(20.dp),
                    tint = Color.Unspecified
                )
                Text(
                    text = "+${selectedType.coinReward}",
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
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

@Composable
private fun MissionTypeSelector(
    selectedType: MissionType,
    onTypeChange: (MissionType) -> Unit
) {
    Column {
        Text(
            text = "Tipo Missione",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            MissionType.entries.forEach { type ->
                val label = when (type) {
                    MissionType.GIORNALIERO -> "Giornaliero"
                    MissionType.SETTIMANALE -> "Settimanale"
                    MissionType.SPECIALE -> "Speciale"
                }
                FilterChip(
                    selected = selectedType == type,
                    onClick = { onTypeChange(type) },
                    label = {
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            fontWeight = if (selectedType == type) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1,
                            letterSpacing = 0.1.sp
                        )
                    },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                        selectedLabelColor = MaterialTheme.colorScheme.secondary,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
            }
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
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 6.dp)
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    fontSize = 12.sp
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
        modifier = Modifier.size(32.dp)
    ) {
        Text(
            text = "✕",
            color = MaterialTheme.colorScheme.error,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun AddSubtaskButton(
    onSubtasksChange: (List<String>) -> Unit,
    currentSubtasks: List<String>
) {
    TextButton(
        onClick = { onSubtasksChange(currentSubtasks + "") },
        modifier = Modifier.height(32.dp)
    ) {
        Text(
            text = "+ Aggiungi subtask",
            color = MaterialTheme.colorScheme.secondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
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
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        ),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.height(40.dp)
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun DismissButton(
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.height(40.dp)
    ) {
        Text(
            text = "Annulla",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 13.sp
        )
    }
}

// ===== TEXT HELPERS =====

@Composable
private fun ErrorText(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.error,
        fontSize = 12.sp,
        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
    )
}

// ===== TEXT FIELD COLORS =====

@Composable
private fun dialogTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
    focusedBorderColor = MaterialTheme.colorScheme.secondary,
    unfocusedBorderColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.35f),
    focusedLabelColor = MaterialTheme.colorScheme.secondary,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    cursorColor = MaterialTheme.colorScheme.secondary,
    errorBorderColor = MaterialTheme.colorScheme.error,
    errorLabelColor = MaterialTheme.colorScheme.error,
    errorSupportingTextColor = MaterialTheme.colorScheme.error
)