package com.example.quester.ui.screens.mission.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quester.data.model.MissionWithSubTasks
import com.example.quester.data.model.SubTask
import com.example.quester.ui.components.FantasyTitle

@Composable
fun MissionCard(
    missionWithTasks: MissionWithSubTasks,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onResetClick: (() -> Unit)? = null
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
            MissionCardHeader(
                title = mission.title,
                showReset = shouldShowReset(missionWithTasks),
                onResetClick = onResetClick,
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick
            )

            Spacer(modifier = Modifier.height(8.dp))

            ProgressBar(progress = missionWithTasks.progress, percentage = percentage)

            if (missionWithTasks.subTasks.isNotEmpty()) {
                SubtasksSection(subtasks = missionWithTasks.subTasks)
            }
        }
    }
}

// ===== HEADER =====

@Composable
private fun MissionCardHeader(
    title: String,
    showReset: Boolean,
    onResetClick: (() -> Unit)?,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FantasyTitle(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Row {
            if (showReset && onResetClick != null) {
                IconButton(
                    onClick = onResetClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset missione",
                        tint = Color(0xFFFF9800),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            IconButton(
                onClick = onEditClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Modifica missione",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Elimina missione",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ===== SUBTASKS =====

@Composable
private fun SubtasksSection(subtasks: List<SubTask>) {
    Spacer(modifier = Modifier.height(16.dp))
    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = "Obiettivi:",
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    subtasks.forEach { task ->
        SubtaskItem(task = task)
    }
}

@Composable
private fun SubtaskItem(task: SubTask) {
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
            color = if (task.done) {
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

// ===== PROGRESS BAR =====

@Composable
private fun ProgressBar(progress: Float, percentage: Int) {
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
                    .fillMaxWidth(progress)
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
}

// ===== FUNZIONI PURE =====

/**
 * Determina se mostrare il pulsante Reset
 * - La missione non è completata
 * - Ha almeno un subtask
 * - Almeno un subtask è completato
 */
private fun shouldShowReset(missionWithTasks: MissionWithSubTasks): Boolean {
    val mission = missionWithTasks.mission
    val hasSubtasks = missionWithTasks.subTasks.isNotEmpty()
    val hasCompletedSubtask = missionWithTasks.subTasks.any { it.done }

    return !mission.completed && hasSubtasks && hasCompletedSubtask
}