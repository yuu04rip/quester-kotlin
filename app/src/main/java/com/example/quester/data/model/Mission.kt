package com.example.quester.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "missions")
data class Mission(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val title: String,
    val description: String = "",
    val type: String,
    val dueDate: String? = null,
    val xpReward: Int = 0,
    val completed: Boolean = false,
    val xpAwarded: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val verificationLevel: String = VerificationLevel.AUTO.name
)

enum class VerificationLevel {
    NONE,      // Nessuna verifica
    AUTO,      // Verifica automatica (controlli anti-abuso)
    MANUAL     // Richiede approvazione manuale
}