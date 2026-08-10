package com.example.quester.ui.screens

import androidx.compose.ui.graphics.Color

// ===== COLORI DEL TEMA FANTASY (FALLBACK) =====
// Usa MaterialTheme.colorScheme per i colori dinamici
// Questi sono solo come fallback

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
// 1. SISTEMA XP E LIVELLI
// ============================================================

/**
 * Formula Lineare per l'XP necessario per salire di livello
 * XP = 100 + (livello - 1) * 50
 *
 * Esempio:
 * - Livello 1 → 2: 100 XP
 * - Livello 2 → 3: 150 XP
 * - Livello 50: 63.750 XP totali
 */
const val XP_BASE = 100
const val XP_INCREMENT = 50

fun getXpRequiredForLevel(level: Int): Int {
    return XP_BASE + (level - 1) * XP_INCREMENT
}

/**
 * Calcola il livello effettivo in base all'XP totale
 */
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

/**
 * Calcola l'XP nel livello corrente
 */
fun getXpInCurrentLevel(totalXp: Int, level: Int = calculateLevelFromXp(totalXp)): Int {
    var totalXpForPreviousLevels = 0
    for (i in 1 until level) {
        totalXpForPreviousLevels += getXpRequiredForLevel(i)
    }
    return totalXp - totalXpForPreviousLevels
}

/**
 * Calcola il progresso verso il prossimo livello (0.0 - 1.0)
 */
fun getXpProgress(totalXp: Int, level: Int = calculateLevelFromXp(totalXp)): Float {
    val xpInCurrent = getXpInCurrentLevel(totalXp, level)
    val xpNeeded = getXpRequiredForLevel(level)
    return (xpInCurrent.toFloat() / xpNeeded).coerceIn(0f, 1f)
}

/**
 * Calcola l'XP totale necessario per raggiungere un certo livello
 */
fun getTotalXpRequiredForLevel(level: Int): Int {
    var total = 0
    for (i in 1 until level) {
        total += getXpRequiredForLevel(i)
    }
    return total
}

// ============================================================
// 2. RICOMPENSE MISSIONI (VALORI FINALI)
// ============================================================

/**
 * XP per tipo di missione (FISSO - deciso dall'utente tramite range)
 * I valori qui sono i BASE per lo scaling (non più usato per XP)
 */
const val MISSION_XP_NORMAL = 30
const val MISSION_XP_SPECIAL = 400
const val MISSION_XP_WEEKLY = 120
const val MISSION_XP_DAILY = 30

/**
 * MONETE per tipo di missione (FISSO - non scalano con il livello)
 */
const val MISSION_COINS_NORMAL = 1
const val MISSION_COINS_SPECIAL = 15
const val MISSION_COINS_WEEKLY = 5
const val MISSION_COINS_DAILY = 1

/**
 * MONETE per Level Up (in base al range di livello)
 */
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

/**
 * Calcola le monete totali dal level up (dal livello 1 al livello specificato)
 */
fun getTotalLevelUpCoins(level: Int): Int {
    var total = 0
    for (i in 1..level) {
        total += getLevelUpCoins(i)
    }
    return total
}

// ============================================================
// 3. FUNZIONI DI CALCOLO RICOMPENSE
// ============================================================

/**
 * Calcola le monete per una missione (NON scalate con il livello)
 */

fun getMissionCoins(missionType: String): Int {
    return when (missionType) {
        "NORMAL" -> MISSION_COINS_NORMAL
        "SPECIAL" -> MISSION_COINS_SPECIAL
        "WEEKLY" -> MISSION_COINS_WEEKLY
        "DAILY" -> MISSION_COINS_DAILY
        else -> MISSION_COINS_NORMAL
    }
}

// ============================================================
// 4. VALIDAZIONE XP PER MISSIONI (RANGE)
// ============================================================

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

// ============================================================
// 5. UTILITY PER COSMETICI E PREZZI
// ============================================================

/**
 * Prezzi dei cosmetici
 */
const val PRICE_FRAME = 30
const val PRICE_COSMETIC = 100
const val PRICE_THEME = 500

/**
 * Nomi dei cosmetici disponibili
 */
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
// 6. REWARD FINALE (LIVELLO 50)
// ============================================================

/**
 * Reward finale per il raggiungimento del livello 50
 */
data class FinalReward(
    val name: String = "👑 Corona dell'Eroe",
    val themeName: String = "Tema Regale",
    val description: String = "Un cosmetico unico per il vero campione!",
    val valueInCoins: Int = 600 // 500 (tema) + 100 (corona)
)

fun getFinalReward(): FinalReward = FinalReward()

// ============================================================
// 7. TABELLA RIASSUNTIVA (per riferimento)
// ============================================================

/**
 * Tabella riassuntiva di XP e Monete
 *
 * | Range Livelli | Monete Level Up |
 * |---------------|-----------------|
 * | 1-10          | 3               |
 * | 11-20         | 5               |
 * | 21-30         | 8               |
 * | 31-40         | 12              |
 * | 41-50         | 20              |
 *
 * | Tipo Missione | Monete | XP (range)    |
 * |---------------|--------|---------------|
 * | Giornaliera   | 1      | 5-50          |
 * | Settimanale   | 5      | 50-200        |
 * | Speciale      | 15     | 100-1000      |
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