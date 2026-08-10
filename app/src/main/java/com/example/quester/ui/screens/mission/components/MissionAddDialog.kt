package com.example.quester.ui.screens.mission.components

import androidx.compose.runtime.*
import com.example.quester.ui.screens.mission.model.MissionType
import com.example.quester.ui.utils.capitalizeWords

@Composable
fun AddMissionDialog(
    onDismiss: () -> Unit,
    onMissionCreated: (String, String, MissionType, List<String>) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(MissionType.GIORNALIERO) }
    var subtasks by remember { mutableStateOf(listOf("")) }

    var titleError by remember { mutableStateOf<String?>(null) }
    var subtaskError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(title) {
        titleError = when {
            title.isBlank() -> null
            title.length < 3 -> "✦ Il titolo deve avere almeno 3 caratteri"
            else -> null
        }
    }

    MissionDialogContent(
        state = MissionDialogState(
            title = title,
            description = description,
            selectedType = selectedType,
            subtasks = subtasks
        ),
        dialogTitle = "✦ Nuova Missione ✦",
        confirmButtonText = "Crea",
        titleError = titleError,
        subtaskError = subtaskError,
        callbacks = MissionDialogCallbacks(
            onTitleChange = {
                title = it
                titleError = null
            },
            onDescriptionChange = { description = it },
            onTypeChange = {
                selectedType = it
            },
            onSubtasksChange = {
                subtasks = it
                subtaskError = null
            },
            onConfirm = {
                if (title.isBlank()) {
                    titleError = "✦ Inserisci un titolo"
                    return@MissionDialogCallbacks
                }

                val validSubtasks = subtasks.filter { it.isNotBlank() }
                if (validSubtasks.isEmpty()) {
                    subtaskError = "✦ Aggiungi almeno un subtask"
                    return@MissionDialogCallbacks
                }

                onMissionCreated(
                    capitalizeWords(title.trim()),
                    description.trim(),
                    selectedType,
                    validSubtasks.map { capitalizeWords(it.trim()) }
                )
                onDismiss()
            },
            onDismiss = onDismiss
        )
    )
}