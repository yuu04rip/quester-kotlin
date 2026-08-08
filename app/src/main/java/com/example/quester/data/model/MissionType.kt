package com.example.quester.data.model

enum class MissionType(
    val label: String,
    val dbValue: String,
    val defaultXp: Int,
    val minXp: Int,
    val maxXp: Int
) {
    GIORNALIERO("Giornaliero", "GIORNALIERO", defaultXp = 20, minXp = 5, maxXp = 50),
    SETTIMANALE("Settimanale", "SETTIMANALE", defaultXp = 100, minXp = 50, maxXp = 200),
    SPECIALE("Speciale", "SPECIALE", defaultXp = 250, minXp = 100, maxXp = 1000);

    companion object {
        fun fromDbValue(value: String): MissionType {
            return entries.find { it.dbValue.equals(value, ignoreCase = true) } ?: GIORNALIERO
        }
    }
}