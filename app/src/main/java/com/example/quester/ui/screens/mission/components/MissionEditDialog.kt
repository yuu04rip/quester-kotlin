package com.example.quester.ui.screens.mission.components

import androidx.compose.runtime.*
import com.example.quester.data.model.MissionWithSubTasks
import com.example.quester.ui.screens.mission.model.MissionType
import com.example.quester.ui.utils.capitalizeWords

@Composable
fun EditMissionDialog(
    missionWithTasks: MissionWithSubTasks,
    onDismiss: () -> Unit,
    onMissionUpdated: (String, String, MissionType, List<String>) -> Unit
) {
    var title by remember { mutableStateOf(missionWithTasks.mission.title) }
    var description by remember { mutableStateOf(missionWithTasks.mission.description) }
    var selectedType by remember { mutableStateOf(MissionType.fromDbValue(missionWithTasks.mission.type)) }
    var subtasks by remember { mutableStateOf(missionWithTasks.subTasks.map { it.text }) }

    MissionDialogContent(
        state = MissionDialogState(
            title = title,
            description = description,
            selectedType = selectedType,
            subtasks = subtasks
        ),
        dialogTitle = "Modifica Missione",
        confirmButtonText = "Salva Modifiche",
        callbacks = MissionDialogCallbacks(
            onTitleChange = { title = it },
            onDescriptionChange = { description = it },
            onTypeChange = { selectedType = it },
            onSubtasksChange = { subtasks = it },
            onConfirm = {
                if (title.isNotBlank()) {
                    onMissionUpdated(
                        capitalizeWords(title.trim()),
                        description.trim(),
                        selectedType,
                        subtasks.filter { it.isNotBlank() }.map { capitalizeWords(it.trim()) }
                    )
                    onDismiss()
                }
            },
            onDismiss = onDismiss
        )
    )
}