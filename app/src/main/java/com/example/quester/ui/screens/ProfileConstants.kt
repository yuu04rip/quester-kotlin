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
// 1. RICOMPENSE MISSIONI (VALORI DI CONFIGURAZIONE)
// ============================================================

const val XP_DAILY = 30
const val XP_WEEKLY = 120
const val XP_SPECIAL = 400

const val COINS_DAILY = 1
const val COINS_WEEKLY = 5
const val COINS_SPECIAL = 15

// ============================================================
// 2. COSMETICI E PREZZI
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
// 3. REWARD FINALE (LIVELLO 50)
// ============================================================

data class FinalReward(
    val name: String = "👑 Corona dell'Eroe",
    val themeName: String = "Tema Regale",
    val description: String = "Un cosmetico unico per il vero campione!",
    val valueInCoins: Int = 600
)

fun getFinalReward(): FinalReward = FinalReward()