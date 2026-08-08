package com.example.quester.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class MissionWithSubTasks(
    @Embedded val mission: Mission,
    @Relation(
        parentColumn = "id",
        entityColumn = "missionId"
    )
    val subTasks: List<SubTask>
) {
    val progress: Float
        get() = if (subTasks.isEmpty()) 0f else subTasks.count { it.done }.toFloat() / subTasks.size
}
