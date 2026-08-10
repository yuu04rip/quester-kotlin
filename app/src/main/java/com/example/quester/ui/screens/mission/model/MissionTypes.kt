package com.example.quester.ui.screens.mission.model

enum class MissionType(
    val label: String,
    val dbValue: String,
    val xpReward: Int,
    val coinReward: Int
) {
    GIORNALIERO("Giornaliero", "GIORNALIERO", xpReward = 30, coinReward = 1),
    SETTIMANALE("Settimanale", "SETTIMANALE", xpReward = 120, coinReward = 5),
    SPECIALE("Speciale", "SPECIALE", xpReward = 400, coinReward = 15);

    companion object {
        fun fromDbValue(value: String): MissionType {
            return entries.find { it.dbValue.equals(value, ignoreCase = true) } ?: GIORNALIERO
        }
    }
}

enum class FilterStatus(val label: String) {
    ALL("Tutte"),
    IN_PROGRESS("In corso"),
    COMPLETED("Completate")
}