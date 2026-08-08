package com.example.quester.ui.screens.mission

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.quester.data.model.Mission
import com.example.quester.data.model.MissionWithSubTasks
import com.example.quester.data.model.SubTask
import com.example.quester.data.repository.MissionRepository
import com.example.quester.data.repository.UserRepository
import com.example.quester.data.session.SessionManager
import com.example.quester.domain.service.MissionService
import com.example.quester.ui.screens.mission.components.*
import com.example.quester.ui.screens.mission.model.FilterStatus
import com.example.quester.ui.screens.mission.model.MissionType
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

// Data class per lo stato della schermata
private data class MissionListState(
    var selectedMissionId: Long? = null,
    var missionToEdit: MissionWithSubTasks? = null,
    var showAddDialog: Boolean = false,
    var missionToReset: MissionWithSubTasks? = null,
    var searchQuery: String = "",
    var selectedFilter: FilterStatus = FilterStatus.ALL
)

// Data class per i dati della schermata
private data class MissionListData(
    val rawMissions: List<MissionWithSubTasks>,
    val filteredMissions: List<MissionWithSubTasks>,
    val username: String
)

// Data class per i callback dei dialoghi
private data class MissionListDialogCallbacks(
    val onMissionUpdated: (Mission, String, String, MissionType, Int, List<String>) -> Unit,
    val onMissionCreated: (String, String, MissionType, Int, List<String>) -> Unit,
    val onTaskToggle: (SubTask) -> Unit
)

// Data class per i servizi dei dialoghi
private data class MissionListDialogServices(
    val missionService: MissionService,
    val snackbarHostState: SnackbarHostState,
    val scope: kotlinx.coroutines.CoroutineScope
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionListScreen(
    missionService: MissionService,
    missionRepository: MissionRepository,
    userRepository: UserRepository,
    sessionManager: SessionManager
) {
    // Stato - USANDO VAR PERCHE MUTABILE
    var selectedMissionId by remember { mutableStateOf<Long?>(null) }
    var missionToEdit by remember { mutableStateOf<MissionWithSubTasks?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var missionToReset by remember { mutableStateOf<MissionWithSubTasks?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(FilterStatus.ALL) }

    val state = MissionListState(
        selectedMissionId = selectedMissionId,
        missionToEdit = missionToEdit,
        showAddDialog = showAddDialog,
        missionToReset = missionToReset,
        searchQuery = searchQuery,
        selectedFilter = selectedFilter
    )

    // Snackbar e coroutine scope
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Dati
    val data = rememberMissionListData(
        missionRepository = missionRepository,
        userRepository = userRepository,
        sessionManager = sessionManager,
        state = state
    )

    // Dialog callbacks
    val dialogCallbacks = MissionListDialogCallbacks(
        onMissionUpdated = { updatedMission, title, desc, type, xp, tasks ->
            scope.launch {
                missionService.updateMissionFromForm(
                    mission = updatedMission,
                    newTitle = title,
                    newDescription = desc,
                    newType = type.dbValue,
                    newXpReward = xp,
                    newSubtasksText = tasks
                )
                missionToEdit = null
                state.missionToEdit = null
            }
        },
        onMissionCreated = { title, desc, type, xp, tasks ->
            scope.launch {
                missionService.createMissionFromForm(
                    title = title,
                    description = desc,
                    type = type.dbValue,
                    dueDate = null,
                    xpReward = xp,
                    subtasks = tasks
                )
                showAddDialog = false
                state.showAddDialog = false
            }
        },
        onTaskToggle = { subTask ->
            scope.launch {
                missionService.toggleSubTask(subTask, !subTask.done)
            }
        }
    )

    val dialogServices = MissionListDialogServices(
        missionService = missionService,
        snackbarHostState = snackbarHostState,
        scope = scope
    )

    // UI
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
                    username = data.username,
                    onAddClick = {
                        showAddDialog = true
                        state.showAddDialog = true
                    }
                )

                // Search e filtri
                MissionListFilters(
                    searchQuery = searchQuery,
                    selectedFilter = selectedFilter,
                    onSearchChange = {
                        searchQuery = it
                        state.searchQuery = it
                    },
                    onFilterChange = {
                        selectedFilter = it
                        state.selectedFilter = it
                    }
                )

                // Lista missioni
                MissionListContent(
                    missions = data.filteredMissions,
                    onMissionClick = {
                        selectedMissionId = it.mission.id
                        state.selectedMissionId = it.mission.id
                    },
                    onEditClick = {
                        missionToEdit = it
                        state.missionToEdit = it
                    },
                    onDeleteClick = { missionWithTasks ->
                        handleMissionDelete(
                            missionWithTasks = missionWithTasks,
                            missionService = missionService,
                            snackbarHostState = snackbarHostState,
                            scope = scope
                        )
                    },
                    onResetClick = {
                        missionToReset = it
                        state.missionToReset = it
                    }
                )
            }

            // Dialoghi
            MissionListDialogs(
                state = state,
                data = data,
                callbacks = dialogCallbacks,
                services = dialogServices
            )
        }
    }
}

// ===== COMPOSABLES DI SUPPORTO =====

@Composable
private fun rememberMissionListData(
    missionRepository: MissionRepository,
    userRepository: UserRepository,
    sessionManager: SessionManager,
    state: MissionListState
): MissionListData {
    val loggedUserId by sessionManager.loggedUserId.collectAsState(initial = null)

    val rawMissions by (loggedUserId?.let {
        missionRepository.getAllMissionsWithSubTasksForUser(it)
    } ?: flowOf(emptyList()))
        .collectAsState(initial = emptyList())

    val user by (loggedUserId?.let {
        userRepository.getUserByIdFlow(it)
    } ?: flowOf(null))
        .collectAsState(initial = null)

    val filteredMissions = rawMissions.filter { item ->
        filterMission(item, state.searchQuery, state.selectedFilter)
    }

    return MissionListData(
        rawMissions = rawMissions,
        filteredMissions = filteredMissions,
        username = user?.username ?: "Eroe"
    )
}

@Composable
private fun MissionListFilters(
    searchQuery: String,
    selectedFilter: FilterStatus,
    onSearchChange: (String) -> Unit,
    onFilterChange: (FilterStatus) -> Unit
) {
    SearchBar(
        query = searchQuery,
        onQueryChange = onSearchChange
    )

    FilterChips(
        selectedFilter = selectedFilter,
        onFilterSelected = onFilterChange
    )
}

@Composable
private fun MissionListContent(
    missions: List<MissionWithSubTasks>,
    onMissionClick: (MissionWithSubTasks) -> Unit,
    onEditClick: (MissionWithSubTasks) -> Unit,
    onDeleteClick: (MissionWithSubTasks) -> Unit,
    onResetClick: (MissionWithSubTasks) -> Unit
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
                    onClick = { onMissionClick(missionWithTasks) },
                    onEditClick = { onEditClick(missionWithTasks) },
                    onDeleteClick = { onDeleteClick(missionWithTasks) },
                    onResetClick = { onResetClick(missionWithTasks) }
                )
            }
        }
    }
}

// ===== DIALOGS =====

@Composable
private fun MissionListDialogs(
    state: MissionListState,
    data: MissionListData,
    callbacks: MissionListDialogCallbacks,
    services: MissionListDialogServices
) {
    // Dialog reset
    ResetMissionDialog(
        missionToReset = state.missionToReset,
        onDismiss = {
            state.missionToReset = null
        },
        onConfirm = { missionId ->
            services.scope.launch {
                try {
                    services.missionService.resetMission(missionId)
                    services.snackbarHostState.showSnackbar(
                        message = "Missione resettata con successo!",
                        duration = SnackbarDuration.Short
                    )
                } catch (e: Exception) {
                    services.snackbarHostState.showSnackbar(
                        message = "Errore: ${e.message}",
                        duration = SnackbarDuration.Short
                    )
                }
                state.missionToReset = null
            }
        }
    )

    // Dialoghi missione - usando la nuova sintassi con data class
    MissionDialogs(
        config = MissionDialogsConfig(
            selectedMissionId = state.selectedMissionId,
            missionToEdit = state.missionToEdit,
            showAddDialog = state.showAddDialog,
            rawMissions = data.rawMissions
        ),
        callbacks = MissionDialogsCallbacks(
            onDismissMissionDetail = { state.selectedMissionId = null },
            onDismissEdit = { state.missionToEdit = null },
            onDismissAdd = { state.showAddDialog = false },
            onEditClick = { mission ->
                state.selectedMissionId = null
                state.missionToEdit = mission
            },
            onTaskToggle = callbacks.onTaskToggle,
            onMissionUpdated = callbacks.onMissionUpdated,
            onMissionCreated = callbacks.onMissionCreated
        ),
        services = MissionDialogsServices(
            missionService = services.missionService,
            snackbarHostState = services.snackbarHostState,
            scope = services.scope
        )
    )
}

// ===== DIALOG RESET =====

@Composable
private fun ResetMissionDialog(
    missionToReset: MissionWithSubTasks?,
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit
) {
    missionToReset?.let { missionWithTasks ->
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    "Resettare la missione?",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column {
                    Text(
                        "I task della missione \"${missionWithTasks.mission.title}\" verranno resettati.",
                        style = MaterialTheme.typography.bodyMedium
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
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Annulla")
                }
            }
        )
    }
}

// ===== FUNZIONI PURE =====

private fun filterMission(
    item: MissionWithSubTasks,
    searchQuery: String,
    selectedFilter: FilterStatus
): Boolean {
    val matchesSearch = item.mission.title.contains(searchQuery, ignoreCase = true) ||
            item.mission.description.contains(searchQuery, ignoreCase = true)

    val matchesStatus = when (selectedFilter) {
        FilterStatus.ALL -> true
        FilterStatus.IN_PROGRESS -> !item.mission.completed
        FilterStatus.COMPLETED -> item.mission.completed
    }

    return matchesSearch && matchesStatus
}

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