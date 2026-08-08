package com.example.quester.ui.screens.mission.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.quester.ui.screens.mission.model.FilterStatus

@Composable
fun FilterChips(
    selectedFilter: FilterStatus,
    onFilterSelected: (FilterStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        FilterStatus.entries.forEach { status ->
            FilterChip(
                selected = selectedFilter == status,
                onClick = { onFilterSelected(status) },
                label = { Text(status.label) }
            )
        }
    }
}