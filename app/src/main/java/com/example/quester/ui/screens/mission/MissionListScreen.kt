package com.example.quester.ui.screens.mission

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.quester.data.model.MissionWithSubTasks
import com.example.quester.data.repository.MissionRepository
import com.example.quester.data.repository.UserRepository
import com.example.quester.data.session.SessionManager
import com.example.quester.domain.service.MissionService
import com.example.quester.ui.screens.mission.components.*
import com.example.quester.ui.screens.mission.model.FilterStatus
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionListScreen(
    missionService: MissionService,
    missionRepository: MissionRepository,
    userRepository: UserRepository,
    sessionManager: SessionManager
) {
    // STATO DIRETTO - usando variabili locali invece di data class
    var selectedMissionId by remember { mutableStateOf<Long?>(null) }
    var missionToEdit by remember { mutableStateOf<MissionWithSubTasks?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var missionToReset by remember { mutableStateOf<MissionWithSubTasks?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(FilterStatus.ALL) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val loggedUserId by sessionManager.loggedUserId.collectAsState(initial = null)
    val rawMissions by (loggedUserId?.let {
        missionRepository.getAllMissionsWithSubTasksForUser(it)
    } ?: flowOf(emptyList())).collectAsState(initial = emptyList())
    val user by (loggedUserId?.let {
        userRepository.getUserByIdFlow(it)
    } ?: flowOf(null)).collectAsState(initial = null)

    val filteredMissions = filterMissions(rawMissions, searchQuery, selectedFilter)

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                MissionListHeader(
                    username = user?.username ?: "Eroe",
                    onAddClick = {
                        showAddDialog = true
                    }
                )

                // Filtri
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it }
                )
                FilterChips(
                    selectedFilter = selectedFilter,
                    onFilterSelected = { selectedFilter = it }
                )

                // Lista missioni
                MissionListContent(
                    missions = filteredMissions,
                    callbacks = MissionListCallbacks(
                        onMissionClick = { selectedMissionId = it.mission.id },
                        onEditClick = { missionToEdit = it },
                        onDeleteClick = { missionWithTasks ->
                            handleMissionDelete(
                                missionWithTasks = missionWithTasks,
                                missionService = missionService,
                                snackbarHostState = snackbarHostState,
                                scope = scope
                            )
                        },
                        onResetClick = { missionToReset = it }
                    )
                )
            }

            // DIALOG RESET
            missionToReset?.let { missionWithTasks ->
                ResetMissionDialog(
                    missionWithTasks = missionWithTasks,
                    onDismiss = { missionToReset = null },
                    onConfirm = { missionId ->
                        scope.launch {
                            try {
                                missionService.resetMission(missionId)
                                snackbarHostState.showSnackbar(
                                    message = "Missione resettata con successo!",
                                    duration = SnackbarDuration.Short
                                )
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar(
                                    message = "Errore: ${e.message}",
                                    duration = SnackbarDuration.Short
                                )
                            }
                            missionToReset = null
                        }
                    }
                )
            }

            // DIALOG AGGIUNTA
            if (showAddDialog) {
                AddMissionDialog(
                    onDismiss = {
                        showAddDialog = false
                    },
                    onMissionCreated = { title, description, type, xp, tasks ->
                        scope.launch {
                            missionService.createMissionFromForm(
                                title = title,
                                description = description,
                                type = type.dbValue,
                                dueDate = null,
                                xpReward = xp,
                                subtasks = tasks
                            )
                            showAddDialog = false
                        }
                    }
                )
            }

            // DIALOG MODIFICA
            missionToEdit?.let { missionWithTasks ->
                EditMissionDialog(
                    missionWithTasks = missionWithTasks,
                    onDismiss = { missionToEdit = null },
                    onMissionUpdated = { title, description, type, xp, tasks ->
                        scope.launch {
                            missionService.updateMissionFromForm(
                                mission = missionWithTasks.mission,
                                newTitle = title,
                                newDescription = description,
                                newType = type.dbValue,
                                newXpReward = xp,
                                newSubtasksText = tasks
                            )
                            missionToEdit = null
                        }
                    }
                )
            }

            // DIALOG DETTAGLI
            selectedMissionId?.let { targetId ->
                val currentMission = rawMissions.find { it.mission.id == targetId }
                if (currentMission != null) {
                    MissionDetailDialog(
                        missionWithTasks = currentMission,
                        onDismiss = { selectedMissionId = null },
                        onTaskToggle = { subTask ->
                            scope.launch {
                                missionService.toggleSubTask(subTask, !subTask.done)
                            }
                        },
                        onEditClick = {
                            selectedMissionId = null
                            missionToEdit = currentMission
                        },
                        onDeleteClick = {
                            handleMissionDelete(
                                missionWithTasks = currentMission,
                                missionService = missionService,
                                snackbarHostState = snackbarHostState,
                                scope = scope
                            )
                            selectedMissionId = null
                        }
                    )
                }
            }
        }
    }
}

// ===== CALLBACKS =====

private data class MissionListCallbacks(
    val onMissionClick: (MissionWithSubTasks) -> Unit,
    val onEditClick: (MissionWithSubTasks) -> Unit,
    val onDeleteClick: (MissionWithSubTasks) -> Unit,
    val onResetClick: (MissionWithSubTasks) -> Unit
)

// ===== FILTRO =====

private fun filterMissions(
    missions: List<MissionWithSubTasks>,
    searchQuery: String,
    selectedFilter: FilterStatus
): List<MissionWithSubTasks> {
    return missions.filter { item ->
        val matchesSearch = item.mission.title.contains(searchQuery, ignoreCase = true) ||
                item.mission.description.contains(searchQuery, ignoreCase = true)

        val matchesStatus = when (selectedFilter) {
            FilterStatus.ALL -> true
            FilterStatus.IN_PROGRESS -> !item.mission.completed
            FilterStatus.COMPLETED -> item.mission.completed
        }

        matchesSearch && matchesStatus
    }
}

// ===== LISTA MISSIONI =====

@Composable
private fun MissionListContent(
    missions: List<MissionWithSubTasks>,
    callbacks: MissionListCallbacks
) {
    if (missions.isEmpty()) {
        EmptyState(searchQuery = "")
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(missions, key = { it.mission.id }) { missionWithTasks ->
                MissionCard(
                    missionWithTasks = missionWithTasks,
                    onClick = { callbacks.onMissionClick(missionWithTasks) },
                    onEditClick = { callbacks.onEditClick(missionWithTasks) },
                    onDeleteClick = { callbacks.onDeleteClick(missionWithTasks) },
                    onResetClick = { callbacks.onResetClick(missionWithTasks) }
                )
            }
        }
    }
}

// ===== DIALOG RESET =====

@Composable
private fun ResetMissionDialog(
    missionWithTasks: MissionWithSubTasks,
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Resettare la missione?",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column {
                Text(
                    "I task della missione \"${missionWithTasks.mission.title}\" verranno resettati.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Dovrai completarli di nuovo per ottenere gli XP.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (missionWithTasks.subTasks.any { it.done }) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "⚠️ ${missionWithTasks.subTasks.count { it.done }} task sono già stati completati e verranno azzerati.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(missionWithTasks.mission.id) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                )
            ) {
                Text("Reset", color = MaterialTheme.colorScheme.onTertiary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annulla", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.large
    )
}

// ===== FUNZIONI PURE =====

private fun handleMissionDelete(
    missionWithTasks: MissionWithSubTasks,
    missionService: MissionService,
    snackbarHostState: SnackbarHostState,
    scope: kotlinx.coroutines.CoroutineScope
) {
    val deletedMission = missionWithTasks.mission
    val deletedSubtasks = missionWithTasks.subTasks

    scope.launch {
        try {
            missionService.deleteMission(deletedMission)

            val result = snackbarHostState.showSnackbar(
                message = "\"${deletedMission.title}\" eliminata",
                actionLabel = "ANNULLA",
                duration = SnackbarDuration.Short
            )

            if (result == SnackbarResult.ActionPerformed) {
                missionService.restoreMission(deletedMission, deletedSubtasks)
            }
        } catch (e: Exception) {
            snackbarHostState.showSnackbar(
                message = "Errore: ${e.message}",
                duration = SnackbarDuration.Short
            )
        }
    }
}