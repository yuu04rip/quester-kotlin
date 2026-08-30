package com.example.quester.ui.theme

import androidx.compose.material3.ButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

interface ButtonStyle {

    // Button pieno
    @Composable
    fun getButtonColors(): ButtonColors

    // OutlinedButton (bordo)
    @Composable
    fun getOutlinedButtonColors(): ButtonColors

    // TextButton (solo testo)
    @Composable
    fun getTextButtonColors(): ButtonColors

    // Shape per tutti i bottoni
    @Composable
    fun getButtonShape(): androidx.compose.ui.graphics.Shape

    // TextStyle per i testi dei bottoni
    @Composable
    fun getTextStyle(): TextStyle

    // Modificatore per effetto press (firmato come extension function)
    fun Modifier.getButtonModifier(pressed: Boolean, enabled: Boolean): Modifier

    // Colore del testo per errori/warning
    @Composable
    fun getErrorTextColor(): Color

    // Colore del testo per successi
    @Composable
    fun getSuccessTextColor(): Color
}