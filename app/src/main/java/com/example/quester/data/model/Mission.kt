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
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // Titolo breve missione (es. "Allenamento")
    val title: String,

    // Descrizione opzionale
    val description: String = "",

    // Tipo missione: DAILY, WEEKLY, SPECIAL
    val type: String,

    // Scadenza in formato semplice (es. "2026-08-03")
    // Per MVP la lasciamo String per velocizzare.
    val dueDate: String? = null,

    // XP assegnata al completamento missione
    val xpReward: Int = 20,

    // Stato completamento missione
    val completed: Boolean = false
)