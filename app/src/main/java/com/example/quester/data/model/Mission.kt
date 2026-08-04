package com.example.quester.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity Room che rappresenta una missione/evento creato dall'utente.
 *
 * UC collegati:
 * - UC5: Creare evento/missione
 * - UC6: Visualizzare dati missione
 */
@Entity(tableName = "missions")
data class Mission(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val type: String,
    val dueDate: String? = null,
    val xpReward: Int = 0,
    val completed: Boolean = false,
    val xpAwarded: Boolean = false,
    val redeemed: Boolean = false
)