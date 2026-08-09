package com.example.quester.ui.screens

import androidx.compose.ui.graphics.Color

// Colori del tema Fantasy
val FantasyBackground = Color(0xFF0D0B14)
val FantasySurface = Color(0xFF171321)
val FantasySurfaceLight = Color(0xFF221B2E)

val FantasyGold = Color(0xFFD4A84F)
val FantasyGoldLight = Color(0xFFF0CC78)

val FantasyPurple = Color(0xFF6B4C9A)
val FantasyPurpleDark = Color(0xFF2B1D42)

val FantasyText = Color(0xFFF3EBD8)
val FantasyTextSecondary = Color(0xFFC8BDA8)
val FantasyError = Color(0xFFE57373)

// ===== COSTANTI XP =====
const val XP_BASE = 100

// Formula esponenziale per XP per livello
fun getXpRequiredForLevel(level: Int): Int {
    return (XP_BASE * Math.pow(level.toDouble(), 1.5)).toInt()
}

// ===== FUNZIONI DI CALCOLO XP =====

fun calculateLevelFromXp(totalXp: Int): Int {
    var remainingXp = totalXp
    var level = 1
    while (true) {
        val xpNeeded = getXpRequiredForLevel(level)
        if (remainingXp >= xpNeeded) {
            remainingXp -= xpNeeded
            level++
        } else {
            break
        }
    }
    return level
}

fun getXpInCurrentLevel(totalXp: Int, level: Int = calculateLevelFromXp(totalXp)): Int {
    var totalXpForPreviousLevels = 0
    for (i in 1 until level) {
        totalXpForPreviousLevels += getXpRequiredForLevel(i)
    }
    return totalXp - totalXpForPreviousLevels
}

fun getXpProgress(totalXp: Int, level: Int = calculateLevelFromXp(totalXp)): Float {
    val xpInCurrent = getXpInCurrentLevel(totalXp, level)
    val xpNeeded = getXpRequiredForLevel(level)
    return (xpInCurrent.toFloat() / xpNeeded).coerceIn(0f, 1f)
}

fun getTotalXpRequiredForLevel(level: Int): Int {
    var total = 0
    for (i in 1 until level) {
        total += getXpRequiredForLevel(i)
    }
    return total
}

// ===== CONFIGURAZIONE RICOMPENSE MISSIONI =====

// Valori BASE (senza scaling)
const val MISSION_COINS_NORMAL = 20
const val MISSION_COINS_SPECIAL = 50
const val MISSION_COINS_WEEKLY = 80
const val MISSION_COINS_DAILY = 30

const val MISSION_XP_NORMAL = 100
const val MISSION_XP_SPECIAL = 500
const val MISSION_XP_WEEKLY = 800
const val MISSION_XP_DAILY = 150

// Fattori di scaling per livello
const val COIN_SCALING_PER_LEVEL = 1   // +1 coin per livello
const val XP_SCALING_PER_LEVEL = 5     // +5 XP per livello

// ===== FUNZIONI DI CALCOLO RICOMPENSE =====

fun getMissionCoins(missionType: String, playerLevel: Int): Int {
    val baseCoins = when (missionType) {
        "NORMAL" -> MISSION_COINS_NORMAL
        "SPECIAL" -> MISSION_COINS_SPECIAL
        "WEEKLY" -> MISSION_COINS_WEEKLY
        "DAILY" -> MISSION_COINS_DAILY
        else -> MISSION_COINS_NORMAL
    }
    val levelBonus = (playerLevel - 1) * COIN_SCALING_PER_LEVEL
    return baseCoins + levelBonus
}

fun getMissionXp(missionType: String, playerLevel: Int): Int {
    val baseXp = when (missionType) {
        "NORMAL" -> MISSION_XP_NORMAL
        "SPECIAL" -> MISSION_XP_SPECIAL
        "WEEKLY" -> MISSION_XP_WEEKLY
        "DAILY" -> MISSION_XP_DAILY
        else -> MISSION_XP_NORMAL
    }
    val levelBonus = (playerLevel - 1) * XP_SCALING_PER_LEVEL
    return baseXp + levelBonus
}

// ===== VALIDAZIONE XP PER MISSIONI =====

/**
 * Verifica se l'XP è valido per il tipo di missione
 */
fun isXpValidForMissionType(missionType: String, xp: Int): Boolean {
    val missionTypeEnum = com.example.quester.ui.screens.mission.model.MissionType.fromDbValue(missionType)
    return xp in missionTypeEnum.minXp..missionTypeEnum.maxXp
}

/**
 * Ottiene il messaggio di errore per XP non valido
 */
fun getXpValidationMessage(missionType: String, xp: Int): String {
    val missionTypeEnum = com.example.quester.ui.screens.mission.model.MissionType.fromDbValue(missionType)
    val range = missionTypeEnum.minXp..missionTypeEnum.maxXp
    return when {
        xp < range.first -> "✦ L'XP è troppo basso! Minimo: ${range.first} XP per ${missionTypeEnum.label}"
        xp > range.last -> "✦ L'XP è troppo alto! Massimo: ${range.last} XP per ${missionTypeEnum.label}"
        else -> ""
    }
}

/**
 * Ottiene il range di XP per un tipo di missione come stringa
 */
fun getXpRangeString(missionType: String): String {
    val missionTypeEnum = com.example.quester.ui.screens.mission.model.MissionType.fromDbValue(missionType)
    return "${missionTypeEnum.minXp} - ${missionTypeEnum.maxXp} XP"
}

/**
 * Ottiene il valore di default per un tipo di missione
 */
fun getDefaultXpForMissionType(missionType: String): Int {
    val missionTypeEnum = com.example.quester.ui.screens.mission.model.MissionType.fromDbValue(missionType)
    return missionTypeEnum.defaultXp
}

/**
 * Normalizza l'XP: se fuori range usa il default
 */
fun normalizeXpForMissionType(missionType: String, xp: Int): Int {
    val missionTypeEnum = com.example.quester.ui.screens.mission.model.MissionType.fromDbValue(missionType)
    return if (xp in missionTypeEnum.minXp..missionTypeEnum.maxXp) {
        xp
    } else {
        missionTypeEnum.defaultXp
    }
}