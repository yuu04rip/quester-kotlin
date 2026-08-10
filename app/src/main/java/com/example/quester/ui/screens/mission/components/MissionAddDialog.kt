package com.example.quester.ui.screens.mission.components

import androidx.compose.runtime.*
import com.example.quester.ui.screens.mission.model.MissionType
import com.example.quester.ui.screens.isXpValidForMissionType
import com.example.quester.ui.screens.getXpValidationMessage
import com.example.quester.ui.screens.normalizeXpForMissionType
import com.example.quester.ui.utils.capitalizeWords
import com.example.quester.ui.utils.capitalizeFirstLetter

@Composable
fun AddMissionDialog(
    onDismiss: () -> Unit,
    onMissionCreated: (String, String, MissionType, Int, List<String>) -> Unit
) {
    val dialogState = rememberDialogState()
    val validationState = rememberValidationState()

    // Validazioni in tempo reale
    LaunchedEffect(dialogState.xpReward, dialogState.selectedType) {
        validationState.xpError = validateXp(
            xpReward = dialogState.xpReward,
            selectedType = dialogState.selectedType
        )
    }

    LaunchedEffect(dialogState.title) {
        validationState.titleError = validateTitle(dialogState.title)
    }

    MissionDialogContent(
        state = createDialogState(dialogState),
        dialogTitle = "✦ Nuova Missione ✦",
        confirmButtonText = "Crea",
        xpError = validationState.xpError,
        titleError = validationState.titleError,
        subtaskError = validationState.subtaskError,
        callbacks = createDialogCallbacks(
            dialogState = dialogState,
            validationState = validationState,
            onDismiss = onDismiss,
            onMissionCreated = { title, description, type, xp, tasks ->
                // Capitalizza prima di passare alla creazione
                val capitalizedTitle = capitalizeWords(title.trim())
                val capitalizedDescription = capitalizeFirstLetter(description.trim())
                val capitalizedTasks = tasks.map { capitalizeWords(it.trim()) }
                onMissionCreated(
                    capitalizedTitle,
                    capitalizedDescription,
                    type,
                    xp,
                    capitalizedTasks
                )
            }
        )
    )
}

// ===== STATE CLASSES =====

@Stable
class DialogState {
    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var selectedType by mutableStateOf(MissionType.GIORNALIERO)
    var xpReward by mutableStateOf("")
    var subtasks by mutableStateOf(listOf(""))
}

@Stable
class ValidationState {
    var xpError by mutableStateOf<String?>(null)
    var titleError by mutableStateOf<String?>(null)
    var subtaskError by mutableStateOf<String?>(null)
}

// ===== REMEMBER FUNCTIONS =====

@Composable
private fun rememberDialogState(): DialogState {
    val state = remember { DialogState() }
    LaunchedEffect(Unit) {
        state.xpReward = state.selectedType.defaultXp.toString()
    }
    return state
}

@Composable
private fun rememberValidationState(): ValidationState {
    return remember { ValidationState() }
}

// ===== VALIDATION FUNCTIONS =====

private fun validateXp(xpReward: String, selectedType: MissionType): String? {
    val xpValue = xpReward.toIntOrNull()
    return when {
        xpValue == null && xpReward.isNotBlank() -> "✦ Inserisci un numero valido"
        xpValue != null && !isXpValidForMissionType(selectedType.dbValue, xpValue) ->
            getXpValidationMessage(selectedType.dbValue, xpValue)
        else -> null
    }
}

private fun validateTitle(title: String): String? {
    return when {
        title.isBlank() -> null
        title.length < 3 -> "✦ Il titolo deve avere almeno 3 caratteri"
        else -> null
    }
}

private fun validateSubtasks(subtasks: List<String>): String? {
    return if (subtasks.filter { it.isNotBlank() }.isEmpty()) {
        "✦ Aggiungi almeno un subtask"
    } else null
}

private fun validateAllFields(
    title: String,
    xpReward: String,
    selectedType: MissionType,
    subtasks: List<String>
): ValidationResult {
    val titleError = when {
        title.isBlank() -> "✦ Inserisci un titolo"
        else -> null
    }

    val xpValue = xpReward.toIntOrNull()
    val xpError = when {
        xpValue == null -> "✦ Inserisci un valore XP valido"
        !isXpValidForMissionType(selectedType.dbValue, xpValue) ->
            getXpValidationMessage(selectedType.dbValue, xpValue)
        else -> null
    }

    val subtaskError = validateSubtasks(subtasks)

    return ValidationResult(
        isValid = titleError == null && xpError == null && subtaskError == null,
        titleError = titleError,
        xpError = xpError,
        subtaskError = subtaskError,
        xpValue = xpValue
    )
}

// ===== DATA CLASSES =====

private data class ValidationResult(
    val isValid: Boolean,
    val titleError: String?,
    val xpError: String?,
    val subtaskError: String?,
    val xpValue: Int?
)

// ===== FACTORY FUNCTIONS =====

private fun createDialogState(dialogState: DialogState): MissionDialogState {
    return MissionDialogState(
        title = dialogState.title,
        description = dialogState.description,
        selectedType = dialogState.selectedType,
        xpReward = dialogState.xpReward,
        subtasks = dialogState.subtasks
    )
}

private fun createDialogCallbacks(
    dialogState: DialogState,
    validationState: ValidationState,
    onDismiss: () -> Unit,
    onMissionCreated: (String, String, MissionType, Int, List<String>) -> Unit
): MissionDialogCallbacks {
    return MissionDialogCallbacks(
        onTitleChange = {
            dialogState.title = it
            validationState.titleError = null
        },
        onDescriptionChange = { dialogState.description = it },
        onTypeChange = {
            dialogState.selectedType = it
            dialogState.xpReward = it.defaultXp.toString()
            validationState.xpError = null
        },
        onXpRewardChange = {
            if (it.all(Char::isDigit) || it.isEmpty()) {
                dialogState.xpReward = it
                validationState.xpError = null
            }
        },
        onSubtasksChange = {
            dialogState.subtasks = it
            validationState.subtaskError = null
        },
        onConfirm = {
            handleConfirm(
                dialogState = dialogState,
                validationState = validationState,
                onDismiss = onDismiss,
                onMissionCreated = onMissionCreated
            )
        },
        onDismiss = onDismiss
    )
}

// ===== CONFIRM HANDLER =====

private fun handleConfirm(
    dialogState: DialogState,
    validationState: ValidationState,
    onDismiss: () -> Unit,
    onMissionCreated: (String, String, MissionType, Int, List<String>) -> Unit
) {
    val validationResult = validateAllFields(
        title = dialogState.title,
        xpReward = dialogState.xpReward,
        selectedType = dialogState.selectedType,
        subtasks = dialogState.subtasks
    )

    if (!validationResult.isValid) {
        validationState.titleError = validationResult.titleError
        validationState.xpError = validationResult.xpError
        validationState.subtaskError = validationResult.subtaskError
        return
    }

    val finalXp = normalizeXpForMissionType(
        dialogState.selectedType.dbValue,
        validationResult.xpValue ?: dialogState.selectedType.defaultXp
    )

    val validSubtasks = dialogState.subtasks.filter { it.isNotBlank() }

    onMissionCreated(
        dialogState.title.trim(),
        dialogState.description.trim(),
        dialogState.selectedType,
        finalXp,
        validSubtasks
    )
    onDismiss()
}