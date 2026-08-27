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

    // XP Totale (se supera 63750, lo blocchiamo subito alla fonte)
    val xpTotale: Int = 0,

    // 👑 LIVELLO PERSISTENTE NEL DB
    val livello: Int = 1,

    val coins: Int = 0,

    val equippedHat: String = "NONE",
    val equippedWeapon: String = "NONE",
    val equippedFrame: String = "NONE"
) {
    companion object {
        const val MAX_LEVEL = 50
        const val MAX_TOTAL_XP = 63750

        fun calculateLevel(xpTotale: Int): Int {
            if (xpTotale >= MAX_TOTAL_XP) return MAX_LEVEL

            var remainingXp = xpTotale
            var currentLevel = 1

            while (currentLevel < MAX_LEVEL) {
                val xpNeeded = if (currentLevel == 49) 2550 else 100 + (currentLevel - 1) * 50
                if (remainingXp >= xpNeeded) {
                    remainingXp -= xpNeeded
                    currentLevel++
                } else {
                    break
                }
            }
            return currentLevel
        }
    }
}
