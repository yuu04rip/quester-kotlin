package com.example.quester.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [
        Index(value = ["username"], unique = true),
        Index(value = ["email"], unique = true)
    ]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(collate = ColumnInfo.NOCASE)
    val username: String,

    @ColumnInfo(collate = ColumnInfo.NOCASE)
    val email: String? = null,

    val passwordHash: String,
    val xpTotale: Int = 0,
    val livello: Int = 1,
    val coins: Int = 0,
    val profileImageUri: String? = null,

    // NUOVI CAMPI PER SICUREZZA
    val reputation: Int = 100,        // Punteggio fiducia (default 100)
    val warnings: Int = 0,            // Numero di avvertimenti
    val xpEarnedToday: Int = 0,       // XP guadagnati oggi
    val lastXpResetDate: Long = System.currentTimeMillis() // Data ultimo reset XP giornaliero
)