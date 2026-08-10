package com.example.quester.domain

import com.example.quester.ui.screens.mission.model.MissionType
import org.junit.Assert.*
import org.junit.Test

class MissionLogicTest {

    // ===== TEST XP FISSO PER TIPO MISSIONE =====

    @Test
    fun giornaliero_hasCorrectXpAndCoins() {
        assertEquals(30, MissionType.GIORNALIERO.xpReward)
        assertEquals(1, MissionType.GIORNALIERO.coinReward)
    }

    @Test
    fun settimanale_hasCorrectXpAndCoins() {
        assertEquals(120, MissionType.SETTIMANALE.xpReward)
        assertEquals(5, MissionType.SETTIMANALE.coinReward)
    }

    @Test
    fun speciale_hasCorrectXpAndCoins() {
        assertEquals(400, MissionType.SPECIALE.xpReward)
        assertEquals(15, MissionType.SPECIALE.coinReward)
    }

    // ===== TEST CALCOLO LEVEL-UP =====

    @Test
    fun calculateLevel_fromZeroXp_returnsLevel1() {
        val xpTotale = 0
        val level = calculateLevel(xpTotale)
        assertEquals(1, level)
    }

    @Test
    fun calculateLevel_from99Xp_returnsLevel1() {
        val xpTotale = 99
        val level = calculateLevel(xpTotale)
        assertEquals(1, level)
    }

    @Test
    fun calculateLevel_from100Xp_returnsLevel2() {
        val xpTotale = 100
        val level = calculateLevel(xpTotale)
        assertEquals(2, level)
    }

    @Test
    fun calculateLevel_from249Xp_returnsLevel2() {
        val xpTotale = 249
        val level = calculateLevel(xpTotale)
        assertEquals(2, level)
    }

    @Test
    fun calculateLevel_from250Xp_returnsLevel3() {
        val xpTotale = 250
        val level = calculateLevel(xpTotale)
        assertEquals(3, level)
    }

    @Test
    fun calculateLevel_from450Xp_returnsLevel4() {
        val xpTotale = 450
        val level = calculateLevel(xpTotale)
        assertEquals(4, level)
    }

    @Test
    fun calculateLevel_from700Xp_returnsLevel5() {
        val xpTotale = 700
        val level = calculateLevel(xpTotale)
        assertEquals(5, level)
    }

    @Test
    fun calculateXpToNextLevel_fromLevel1_with0Xp_returns100() {
        val xpTotale = 0
        val livello = 1
        val xpNeeded = calculateXpToNextLevel(xpTotale, livello)
        assertEquals(100, xpNeeded)
    }

    @Test
    fun calculateXpToNextLevel_fromLevel1_with50Xp_returns50() {
        val xpTotale = 50
        val livello = 1
        val xpNeeded = calculateXpToNextLevel(xpTotale, livello)
        assertEquals(50, xpNeeded)
    }

    @Test
    fun calculateXpToNextLevel_fromLevel2_with150Xp_returns100() {
        val xpTotale = 150
        val livello = 2
        val xpNeeded = calculateXpToNextLevel(xpTotale, livello)
        assertEquals(100, xpNeeded)
    }

    @Test
    fun calculateXpToNextLevel_fromLevel3_with450Xp_returns0() {
        val xpTotale = 450
        val livello = 3
        val xpNeeded = calculateXpToNextLevel(xpTotale, livello)
        assertEquals(0, xpNeeded)
    }

    // ===== TEST FILTRAGGIO MISSIONI =====

    @Test
    fun filterMissions_bySearchQuery_matchesTitle() {
        val missionTitle = "Missione di prova"
        val searchQuery = "prova"
        val matches = matchesSearchQuery(missionTitle, searchQuery)
        assertTrue(matches)
    }

    @Test
    fun filterMissions_bySearchQuery_notMatches() {
        val missionTitle = "Missione di prova"
        val searchQuery = "altro"
        val matches = matchesSearchQuery(missionTitle, searchQuery)
        assertFalse(matches)
    }

    @Test
    fun filterMissions_bySearchQuery_caseInsensitive() {
        val missionTitle = "Missione di Prova"
        val searchQuery = "prova"
        val matches = matchesSearchQuery(missionTitle, searchQuery)
        assertTrue(matches)
    }

    @Test
    fun filterMissions_bySearchQuery_matchesDescription() {
        val missionDescription = "Questa è una descrizione di prova"
        val searchQuery = "prova"
        val matches = matchesSearchQuery(missionDescription, searchQuery)
        assertTrue(matches)
    }

    // ===== TEST CALCOLO PROGRESSO =====

    @Test
    fun calculateProgress_noSubtasks_returnsZero() {
        val subtasks = emptyList<Task>()
        val progress = calculateProgress(subtasks)
        assertEquals(0f, progress, 0.01f)
    }

    @Test
    fun calculateProgress_allSubtasksDone_returnsOne() {
        val subtasks = listOf(
            Task(done = true),
            Task(done = true),
            Task(done = true)
        )
        val progress = calculateProgress(subtasks)
        assertEquals(1f, progress, 0.01f)
    }

    @Test
    fun calculateProgress_halfSubtasksDone_returnsHalf() {
        val subtasks = listOf(
            Task(done = true),
            Task(done = false),
            Task(done = true),
            Task(done = false)
        )
        val progress = calculateProgress(subtasks)
        assertEquals(0.5f, progress, 0.01f)
    }

    @Test
    fun calculateProgress_noSubtasksDone_returnsZero() {
        val subtasks = listOf(
            Task(done = false),
            Task(done = false)
        )
        val progress = calculateProgress(subtasks)
        assertEquals(0f, progress, 0.01f)
    }

    @Test
    fun calculateProgress_oneOfThreeDone_returnsOneThird() {
        val subtasks = listOf(
            Task(done = true),
            Task(done = false),
            Task(done = false)
        )
        val progress = calculateProgress(subtasks)
        assertEquals(0.333f, progress, 0.01f)
    }

    // ===== FUNZIONI DI TEST (logica pura) =====

    /**
     * Calcola il livello in base all'XP totale usando la formula lineare
     * XP = 100 + (livello - 1) * 50
     */
    private fun calculateLevel(xpTotale: Int): Int {
        var remainingXp = xpTotale
        var level = 1
        while (true) {
            val xpNeeded = 100 + (level - 1) * 50
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
     * Calcola l'XP rimanente per il prossimo livello
     */
    private fun calculateXpToNextLevel(xpTotale: Int, livello: Int): Int {
        val xpForCurrentLevel = 100 + (livello - 1) * 50
        var remainingXp = xpTotale
        // Sottrai l'XP dei livelli precedenti
        for (i in 1 until livello) {
            remainingXp -= 100 + (i - 1) * 50
        }
        return (xpForCurrentLevel - remainingXp).coerceAtLeast(0)
    }

    /**
     * Verifica se una stringa contiene la query di ricerca (case insensitive)
     */
    private fun matchesSearchQuery(text: String, query: String): Boolean {
        return text.contains(query, ignoreCase = true)
    }

    /**
     * Calcola il progresso di completamento dei subtask
     */
    private fun calculateProgress(subtasks: List<Task>): Float {
        if (subtasks.isEmpty()) return 0f
        val done = subtasks.count { it.done }
        return done.toFloat() / subtasks.size
    }

    // ===== DATA CLASS PER I TEST =====

    private data class Task(val done: Boolean)
}