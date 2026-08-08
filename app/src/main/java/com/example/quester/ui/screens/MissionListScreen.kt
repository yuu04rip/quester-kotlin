package com.example.quester.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quester.data.model.MissionWithSubTasks
import com.example.quester.data.model.SubTask
import com.example.quester.data.repository.MissionRepository
import com.example.quester.data.repository.UserRepository
import com.example.quester.domain.service.MissionService
import kotlinx.coroutines.launch

// 1. Modello UI per mappare i dati DB al frontend
enum class MissionType(val label: String, val dbValue: String) {
    GIORNALIERO("Giornaliero", "GIORNALIERO"),
    SETTIMANALE("Settimanale", "SETTIMANALE"),
    SPECIALE("Speciale", "SPECIALE");

    companion object {
        fun fromDbValue(value: String): MissionType {
            return entries.find { it.dbValue == value } ?: GIORNALIERO
        }
    }
}

@Composable
fun MissionList(
    missionService: MissionService,
    missionRepository: MissionRepository,
    userRepository: UserRepository
) {
    var selectedMissionId by remember { mutableStateOf<Long?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    // 2. Osserviamo i dati reali dal Repository
    val missionsWithTasks by missionRepository.getAllMissionsWithSubTasks()
        .collectAsState(initial = emptyList())

    val user by userRepository.getUserFlow().collectAsState(initial = null)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // INTESTAZIONE
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Ciao, ${user?.username ?: "Eroe"}! 👋",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Bacheca Missioni",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = { showAddDialog = true },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Nuova Missione")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Nuova")
                }
            }

            // LISTA DELLE MISSIONI IN BACHECA
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(missionsWithTasks, key = { it.mission.id }) { missionWithTasks ->
                    MissionCard(
                        missionWithTasks = missionWithTasks,
                        onClick = { selectedMissionId = missionWithTasks.mission.id }
                    )
                }
            }
        }

        // POP-UP DETTAGLI MISSIONE (Cliccabile)
        selectedMissionId?.let { targetId ->
            val currentMission = missionsWithTasks.find { it.mission.id == targetId }
            if (currentMission != null) {
                MissionDetailDialog(
                    missionWithTasks = currentMission,
                    onDismiss = { selectedMissionId = null },
                    onTaskToggle = { subTask ->
                        scope.launch {
                            missionService.toggleSubTask(subTask, !subTask.done)
                        }
                    }
                )
            }
        }
    }

    if (showAddDialog) {
        AddMissionDialog(
            onDismiss = { showAddDialog = false },
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
                }
            }
        )
    }
}

@Composable
fun MissionCard(
    missionWithTasks: MissionWithSubTasks,
    onClick: () -> Unit
) {
    val mission = missionWithTasks.mission
    val percentage = (missionWithTasks.progress * 100).toInt()

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = mission.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // BARRA DI PROGRESSO
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(missionWithTasks.progress)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (percentage == 100) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
                            )
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "$percentage%",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = if (percentage == 100) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
                )
            }

            // LISTA TASK IN BACHECA (Solo visualizzazione)
            if (missionWithTasks.subTasks.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Obiettivi:",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                missionWithTasks.subTasks.forEach { task ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    ) {
                        Checkbox(
                            checked = task.done,
                            onCheckedChange = null,
                            enabled = false,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = task.text,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (task.done)
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MissionDetailDialog(
    missionWithTasks: MissionWithSubTasks,
    onDismiss: () -> Unit,
    onTaskToggle: (SubTask) -> Unit
) {
    val mission = missionWithTasks.mission
    val percentage = (missionWithTasks.progress * 100).toInt()

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Chiudi")
            }
        },
        title = {
            Column {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = MissionType.fromDbValue(mission.type).label,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = mission.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = mission.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                HorizontalDivider()

                Text(
                    text = "Completamento: $percentage%",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                LinearProgressIndicator(
                    progress = { missionWithTasks.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )

                if (missionWithTasks.subTasks.isNotEmpty()) {
                    Text(
                        text = "Task della missione:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Column {
                        missionWithTasks.subTasks.forEach { task ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                            ) {
                                Checkbox(
                                    checked = task.done,
                                    onCheckedChange = { onTaskToggle(task) }
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = task.text,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (task.done)
                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun AddMissionDialog(
    onDismiss: () -> Unit,
    onMissionCreated: (String, String, MissionType, Int, List<String>) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(MissionType.GIORNALIERO) }
    var xpReward by remember { mutableStateOf("50") }
    var subtasks by remember { mutableStateOf(listOf("")) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onMissionCreated(
                            title,
                            description,
                            selectedType,
                            xpReward.toIntOrNull() ?: 0,
                            subtasks.filter { it.isNotBlank() }
                        )
                        onDismiss()
                    }
                }
            ) {
                Text("Crea")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annulla")
            }
        },
        title = {
            Text(text = "Nuova Missione", style = MaterialTheme.typography.headlineSmall)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Titolo Missione") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrizione") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = xpReward,
                    onValueChange = { if (it.all { char -> char.isDigit() }) xpReward = it },
                    label = { Text("Ricompensa XP") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Tipo di Missione:",
                    style = MaterialTheme.typography.labelLarge
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MissionType.entries.forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = { Text(type.label) }
                        )
                    }
                }

                HorizontalDivider()

                Text(
                    text = "Task della missione:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                subtasks.forEachIndexed { index, subtask ->
                    OutlinedTextField(
                        value = subtask,
                        onValueChange = { newValue ->
                            val newList = subtasks.toMutableList()
                            newList[index] = newValue
                            subtasks = newList
                        },
                        label = { Text("Task ${index + 1}") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                TextButton(
                    onClick = { subtasks = subtasks + "" },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Aggiungi Task")
                }
            }
        }
    )
}
/*
@Preview(showBackground = true)
@Composable
fun MissionListPreview() {
    // Requires services for preview
}
*/
