package com.example.quester.utils

import org.junit.Assert.*
import org.junit.Test

class TextUtilsTest {

    @Test
    fun splitTextAndNumbers_textOnly_returnsSinglePart() {
        // Arrange
        val text = "Missione di prova"

        // Act
        val parts = splitTextAndNumbers(text)

        // Assert
        assertEquals(1, parts.size)
        assertEquals("Missione di prova", parts[0])
    }

    @Test
    fun splitTextAndNumbers_numbersOnly_returnsSinglePart() {
        // Arrange
        val text = "12345"

        // Act
        val parts = splitTextAndNumbers(text)

        // Assert
        assertEquals(1, parts.size)
        assertEquals("12345", parts[0])
    }

    @Test
    fun splitTextAndNumbers_textAndNumbers_returnsSeparatedParts() {
        // Arrange
        val text = "Missione 5"

        // Act
        val parts = splitTextAndNumbers(text)

        // Assert
        assertEquals(2, parts.size)
        assertEquals("Missione ", parts[0])
        assertEquals("5", parts[1])
    }

    @Test
    fun splitTextAndNumbers_textWithSpacesAndNumbers_returnsCorrectParts() {
        // Arrange
        val text = "Livello 10 XP 500"

        // Act
        val parts = splitTextAndNumbers(text)

        // Assert
        // La funzione divide in base a: lettere/spazi vs numeri
        // "Livello " (testo) → "10" (numero) → " XP " (testo) → "500" (numero)
        // Quindi il risultato ha 4 parti!
        assertEquals(4, parts.size)
        assertEquals("Livello ", parts[0])
        assertEquals("10", parts[1])
        assertEquals(" XP ", parts[2])
        assertEquals("500", parts[3])
    }

    @Test
    fun splitTextAndNumbers_multipleNumbers_returnsCorrectParts() {
        // Arrange
        val text = "XP 100 livello 5"

        // Act
        val parts = splitTextAndNumbers(text)

        // Assert
        // "XP " → "100" → " livello " → "5"
        assertEquals(4, parts.size)
        assertEquals("XP ", parts[0])
        assertEquals("100", parts[1])
        assertEquals(" livello ", parts[2])
        assertEquals("5", parts[3])
    }

    @Test
    fun splitTextAndNumbers_textStartsWithNumber_returnsCorrectParts() {
        // Arrange
        val text = "100 XP per livello 5"

        // Act
        val parts = splitTextAndNumbers(text)

        // Assert
        // "100" → " XP per livello " → "5"
        assertEquals(3, parts.size)
        assertEquals("100", parts[0])
        assertEquals(" XP per livello ", parts[1])
        assertEquals("5", parts[2])
    }

    @Test
    fun splitTextAndNumbers_emptyString_returnsEmptyList() {
        // Arrange
        val text = ""

        // Act
        val parts = splitTextAndNumbers(text)

        // Assert
        assertTrue(parts.isEmpty())
    }

    @Test
    fun splitTextAndNumbers_onlySpaces_returnsSinglePart() {
        // Arrange
        val text = "   "

        // Act
        val parts = splitTextAndNumbers(text)

        // Assert
        assertEquals(1, parts.size)
        assertEquals("   ", parts[0])
    }

    // La funzione da testare (dovrebbe essere la stessa di FantasyTitle)
    private fun splitTextAndNumbers(text: String): List<String> {
        if (text.isEmpty()) return emptyList()

        val result = mutableListOf<String>()
        var currentPart = ""
        var currentIsDigit = text[0].isDigit()

        for (char in text) {
            val isDigit = char.isDigit()
            if (isDigit == currentIsDigit) {
                currentPart += char
            } else {
                if (currentPart.isNotEmpty()) {
                    result.add(currentPart)
                }
                currentPart = char.toString()
                currentIsDigit = isDigit
            }
        }
        if (currentPart.isNotEmpty()) {
            result.add(currentPart)
        }
        return result
    }
}