package com.example.quester.ui.screens.mission.components

import androidx.compose.runtime.*
import com.example.quester.data.model.MissionWithSubTasks
import com.example.quester.ui.screens.mission.model.MissionType
import com.example.quester.ui.utils.capitalizeWords
import com.example.quester.ui.utils.capitalizeFirstLetter

@Composable
fun EditMissionDialog(
    missionWithTasks: MissionWithSubTasks,
    onDismiss: () -> Unit,
    onMissionUpdated: (String, String, MissionType, Int, List<String>) -> Unit
) {
    var title by remember { mutableStateOf(missionWithTasks.mission.title) }
    var description by remember { mutableStateOf(missionWithTasks.mission.description) }
    var selectedType by remember { mutableStateOf(MissionType.fromDbValue(missionWithTasks.mission.type)) }
    var xpReward by remember { mutableStateOf(missionWithTasks.mission.xpReward.toString()) }
    var subtasks by remember { mutableStateOf(missionWithTasks.subTasks.map { it.text }) }

    MissionDialogContent(
        state = MissionDialogState(
            title = title,
            description = description,
            selectedType = selectedType,
            xpReward = xpReward,
            subtasks = subtasks
        ),
        dialogTitle = "Modifica Missione",
        confirmButtonText = "Salva Modifiche",
        callbacks = MissionDialogCallbacks(
            onTitleChange = { title = it },
            onDescriptionChange = { description = it },
            onTypeChange = {
                selectedType = it
                xpReward = it.defaultXp.toString()
            },
            onXpRewardChange = {
                if (it.all { char -> char.isDigit() }) {
                    xpReward = it
                }
            },
            onSubtasksChange = { subtasks = it },
            onConfirm = {
                if (title.isNotBlank()) {
                    val parsedXp = xpReward.toIntOrNull() ?: selectedType.defaultXp
                    val finalXp = parsedXp.coerceIn(selectedType.minXp, selectedType.maxXp)

                    // Capitalizza prima di passare all'update
                    val capitalizedTitle = capitalizeWords(title.trim())
                    val capitalizedDescription = capitalizeFirstLetter(description.trim())
                    val capitalizedTasks = subtasks
                        .filter { it.isNotBlank() }
                        .map { capitalizeWords(it.trim()) }

                    onMissionUpdated(
                        capitalizedTitle,
                        capitalizedDescription,
                        selectedType,
                        finalXp,
                        capitalizedTasks
                    )
                    onDismiss()
                }
            },
            onDismiss = onDismiss
        )
    )
}