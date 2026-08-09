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

// Costanti di gioco - XP per livello con crescita progressiva
const val XP_BASE = 100          // XP base per il livello 1
const val XP_MULTIPLIER = 1.5f   // Fattore di crescita (esponenziale)
const val XP_INCREMENT = 50      // Incremento lineare alternativo

// Funzione per calcolare l'XP necessario per un dato livello
// Usa una formula di crescita: XP = XP_BASE * (livello ^ 1.5)
// Oppure puoi usare una progressione lineare con incremento
fun getXpRequiredForLevel(level: Int): Int {
    // Opzione 1: Crescita esponenziale (più difficile)
    return (XP_BASE * Math.pow(level.toDouble(), 1.5)).toInt()

    // Opzione 2: Crescita lineare con incremento (più graduale)
    //return XP_BASE + (level - 1) * XP_INCREMENT

    // Opzione 3: Crescita quadratica (molto difficile)
    // return XP_BASE * level * level
}