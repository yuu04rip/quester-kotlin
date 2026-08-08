package com.example.quester.ui.screens.mission.components

import androidx.compose.runtime.*
import com.example.quester.ui.screens.mission.model.MissionType

@Composable
fun AddMissionDialog(
    onDismiss: () -> Unit,
    onMissionCreated: (String, String, MissionType, Int, List<String>) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(MissionType.GIORNALIERO) }
    var xpReward by remember { mutableStateOf(selectedType.defaultXp.toString()) }
    var subtasks by remember { mutableStateOf(listOf("")) }

    MissionDialogContent(
        state = MissionDialogState(
            title = title,
            description = description,
            selectedType = selectedType,
            xpReward = xpReward,
            subtasks = subtasks
        ),
        dialogTitle = "Nuova Missione",
        confirmButtonText = "Crea",
        callbacks = MissionDialogCallbacks(
            onTitleChange = { title = it },
            onDescriptionChange = { description = it },
            onTypeChange = {
                selectedType = it
                xpReward = it.defaultXp.toString()
            },
            onXpRewardChange = {
                if (it.all(Char::isDigit)) {
                    xpReward = it
                }
            },
            onSubtasksChange = { subtasks = it },
            onConfirm = {
                if (title.isNotBlank()) {
                    val parsedXp = xpReward.toIntOrNull() ?: selectedType.defaultXp
                    val finalXp = parsedXp.coerceIn(selectedType.minXp, selectedType.maxXp)
                    // CORRETTO: senza named arguments
                    onMissionCreated(
                        title,
                        description,
                        selectedType,
                        finalXp,
                        subtasks.filter { it.isNotBlank() }
                    )
                    onDismiss()
                }
            },
            onDismiss = onDismiss
        )
    )
}