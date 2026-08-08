package com.example.quester.ui.screens.mission.components

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import com.example.quester.data.model.Mission
import com.example.quester.data.model.MissionWithSubTasks
import com.example.quester.data.model.SubTask
import com.example.quester.domain.service.MissionService
import com.example.quester.ui.screens.mission.model.MissionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// Data class per i parametri di configurazione
data class MissionDialogsConfig(
    val selectedMissionId: Long?,
    val missionToEdit: MissionWithSubTasks?,
    val showAddDialog: Boolean,
    val rawMissions: List<MissionWithSubTasks>
)

// Data class per i callback
data class MissionDialogsCallbacks(
    val onDismissMissionDetail: () -> Unit,
    val onDismissEdit: () -> Unit,
    val onDismissAdd: () -> Unit,
    val onEditClick: (MissionWithSubTasks) -> Unit,
    val onTaskToggle: (SubTask) -> Unit,
    val onMissionUpdated: (Mission, String, String, MissionType, Int, List<String>) -> Unit,
    val onMissionCreated: (String, String, MissionType, Int, List<String>) -> Unit
)

// Data class per i servizi
data class MissionDialogsServices(
    val missionService: MissionService,
    val snackbarHostState: SnackbarHostState,
    val scope: CoroutineScope
)

@Composable
fun MissionDialogs(
    config: MissionDialogsConfig,
    callbacks: MissionDialogsCallbacks,
    services: MissionDialogsServices
) {
    // Dialog dettagli missione
    config.selectedMissionId?.let { targetId ->
        val currentMission = config.rawMissions.find { it.mission.id == targetId }
        if (currentMission != null) {
            MissionDetailDialog(
                missionWithTasks = currentMission,
                onDismiss = callbacks.onDismissMissionDetail,
                onTaskToggle = callbacks.onTaskToggle,
                onEditClick = { callbacks.onEditClick(currentMission) },
                onDeleteClick = {
                    handleMissionDelete(
                        missionWithTasks = currentMission,
                        missionService = services.missionService,
                        snackbarHostState = services.snackbarHostState,
                        scope = services.scope
                    )
                    callbacks.onDismissMissionDetail()
                }
            )
        }
    }

    // Dialog modifica missione
    config.missionToEdit?.let { targetMissionWithTasks ->
        EditMissionDialog(
            missionWithTasks = targetMissionWithTasks,
            onDismiss = callbacks.onDismissEdit,
            onMissionUpdated = { title, desc, type, xp, tasks ->
                callbacks.onMissionUpdated(targetMissionWithTasks.mission, title, desc, type, xp, tasks)
            }
        )
    }

    // Dialog aggiunta missione
    if (config.showAddDialog) {
        AddMissionDialog(
            onDismiss = callbacks.onDismissAdd,
            onMissionCreated = callbacks.onMissionCreated
        )
    }
}

private fun handleMissionDelete(
    missionWithTasks: MissionWithSubTasks,
    missionService: MissionService,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
) {
    val deletedMission = missionWithTasks.mission
    val deletedSubtasks = missionWithTasks.subTasks

    scope.launch {
        missionService.deleteMission(deletedMission)

        val result = snackbarHostState.showSnackbar(
            message = "\"${deletedMission.title}\" eliminata",
            actionLabel = "ANNULLA",
            duration = SnackbarDuration.Short
        )

        if (result == SnackbarResult.ActionPerformed) {
            missionService.restoreMission(deletedMission, deletedSubtasks)
        }
    }
}