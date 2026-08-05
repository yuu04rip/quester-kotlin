package com.example.quester.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity Room che rappresenta un sotto-task di una missione.
 *
 * UC collegati:
 * - UC7: Confermare la todolist di un evento
 *
 * Ogni SubTask appartiene a una Mission tramite missionId.
 * onDelete = CASCADE: se elimini la missione, vengono eliminati anche i suoi subtask.
 */
@Entity(
    tableName = "subtasks",
    foreignKeys = [
        ForeignKey(
            entity = Mission::class,
            parentColumns = ["id"],
            childColumns = ["missionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["missionId"])]
)
data class SubTask(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // FK verso Mission.id
    val missionId: Long,

    // Testo del task (es. "Preparare slide")
    val text: String,

    // true se il task è completato
    val done: Boolean = false
)