package com.example.quester.ui.screens

import androidx.compose.ui.graphics.Color

// ===== COLORI DEL TEMA FANTASY (FALLBACK) =====
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

// ============================================================
// 1. SISTEMA XP E LIVELLI (LINEARE)
// ============================================================

const val XP_BASE = 100
const val XP_INCREMENT = 50

fun getXpRequiredForLevel(level: Int): Int {
    return XP_BASE + (level - 1) * XP_INCREMENT
}

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

// ============================================================
// 2. RICOMPENSE MISSIONI (VALORI FINALI)
// ============================================================

const val XP_DAILY = 30
const val XP_WEEKLY = 120
const val XP_SPECIAL = 400

const val COINS_DAILY = 1
const val COINS_WEEKLY = 5
const val COINS_SPECIAL = 15

fun getLevelUpCoins(level: Int): Int {
    return when (level) {
        in 1..10 -> 3
        in 11..20 -> 5
        in 21..30 -> 8
        in 31..40 -> 12
        in 41..50 -> 20
        else -> 0
    }
}

fun getTotalLevelUpCoins(level: Int): Int {
    var total = 0
    for (i in 1..level) {
        total += getLevelUpCoins(i)
    }
    return total
}

// ============================================================
// 3. COSMETICI E PREZZI
// ============================================================

const val PRICE_FRAME = 30
const val PRICE_COSMETIC = 100
const val PRICE_THEME = 500

val FRAME_NAMES = listOf(
    "Cornice del Mago",
    "Cornice del Cavaliere",
    "Cornice Sci-Fi"
)

val COSMETIC_NAMES = listOf(
    "Cappello del Mago",
    "Bastone del Mago",
    "Pistola Spaziale",
    "Spada del Cavaliere",
    "Elmo del Cavaliere",
    "Visore Futuristico"
)

val THEME_NAMES = listOf(
    "Arcade",
    "Bacheca Fantasy"
)

// ============================================================
// 4. REWARD FINALE (LIVELLO 50)
// ============================================================

data class FinalReward(
    val name: String = "👑 Corona dell'Eroe",
    val themeName: String = "Tema Regale",
    val description: String = "Un cosmetico unico per il vero campione!",
    val valueInCoins: Int = 600
)

fun getFinalReward(): FinalReward = FinalReward()

// ============================================================
// 5. TABELLA RIASSUNTIVA
// ============================================================

/**
 * | Range Livelli | Monete Level Up |
 * |---------------|-----------------|
 * | 1-10          | 3               |
 * | 11-20         | 5               |
 * | 21-30         | 8               |
 * | 31-40         | 12              |
 * | 41-50         | 20              |
 *
 * | Tipo Missione | Monete | XP       |
 * |---------------|--------|----------|
 * | Giornaliera   | 1      | 30       |
 * | Settimanale   | 5      | 120      |
 * | Speciale      | 15     | 400      |
 *
 * | Cosmetico     | Prezzo |
 * |---------------|--------|
 * | Cornice       | 30     |
 * | Cosmetico     | 100    |
 * | Tema          | 500    |
 *
 * | Reward Finale (Livello 50) | Valore |
 * |----------------------------|--------|
 * | Corona dell'Eroe + Tema Regale | 600 |
 */