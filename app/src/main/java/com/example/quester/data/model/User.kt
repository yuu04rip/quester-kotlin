package com.example.quester.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity Room che rappresenta il profilo utente.
 *
 * UC collegati:
 * - UC9 (Tracciamento level-up): xpTotale e livello vengono aggiornati
 *   quando l'utente completa task/missioni.
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // Nome visibile / username utente
    val username: String,

    // Totale XP accumulata (usata per progressione)
    val xpTotale: Int = 0,

    // Livello corrente dell'utente
    val livello: Int = 1
)