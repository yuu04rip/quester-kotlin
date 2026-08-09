package com.example.quester.domain

import com.example.quester.ui.screens.mission.model.MissionType
import org.junit.Assert.*
import org.junit.Test

class MissionLogicTest {

    // ===== TEST VALIDAZIONE XP =====

    @Test
    fun validateXp_withinRange_returnsSameValue() {
        // Arrange
        val xpReward = 30
        val missionType = MissionType.GIORNALIERO

        // Act
        val result = validateAndNormalizeXp(xpReward, missionType)

        // Assert
        assertEquals(30, result)
    }

    @Test
    fun validateXp_aboveRange_returnsDefault() {
        // Arrange
        val xpReward = 999
        val missionType = MissionType.GIORNALIERO

        // Act
        val result = validateAndNormalizeXp(xpReward, missionType)

        // Assert
        assertEquals(MissionType.GIORNALIERO.defaultXp, result)
    }

    @Test
    fun validateXp_belowRange_returnsDefault() {
        // Arrange
        val xpReward = 1
        val missionType = MissionType.GIORNALIERO

        // Act
        val result = validateAndNormalizeXp(xpReward, missionType)

        // Assert
        assertEquals(MissionType.GIORNALIERO.defaultXp, result)
    }

    @Test
    fun validateXp_withSpecialType_returnsDefaultWhenAboveRange() {
        // Arrange
        val xpReward = 1500
        val missionType = MissionType.SPECIALE

        // Act
        val result = validateAndNormalizeXp(xpReward, missionType)

        // Assert
        assertEquals(MissionType.SPECIALE.defaultXp, result)
    }

    @Test
    fun validateXp_withSpecialType_returnsValueWhenWithinRange() {
        // Arrange
        val xpReward = 500
        val missionType = MissionType.SPECIALE

        // Act
        val result = validateAndNormalizeXp(xpReward, missionType)

        // Assert
        assertEquals(500, result)
    }

    // ===== TEST CALCOLO LEVEL-UP =====

    @Test
    fun calculateLevel_fromZeroXp_returnsLevel1() {
        // Arrange
        val xpTotale = 0

        // Act
        val level = calculateLevel(xpTotale)

        // Assert
        assertEquals(1, level)
    }

    @Test
    fun calculateLevel_from99Xp_returnsLevel1() {
        // Arrange
        val xpTotale = 99

        // Act
        val level = calculateLevel(xpTotale)

        // Assert
        assertEquals(1, level)
    }

    @Test
    fun calculateLevel_from100Xp_returnsLevel2() {
        // Arrange
        val xpTotale = 100

        // Act
        val level = calculateLevel(xpTotale)

        // Assert
        assertEquals(2, level)
    }

    @Test
    fun calculateLevel_from250Xp_returnsLevel3() {
        // Arrange
        val xpTotale = 250

        // Act
        val level = calculateLevel(xpTotale)

        // Assert
        assertEquals(3, level)
    }

    @Test
    fun calculateXpToNextLevel_fromLevel1_with0Xp_returns100() {
        // Arrange
        val xpTotale = 0
        val livello = 1

        // Act
        val xpNeeded = calculateXpToNextLevel(xpTotale, livello)

        // Assert
        assertEquals(100, xpNeeded)
    }

    @Test
    fun calculateXpToNextLevel_fromLevel1_with50Xp_returns50() {
        // Arrange
        val xpTotale = 50
        val livello = 1

        // Act
        val xpNeeded = calculateXpToNextLevel(xpTotale, livello)

        // Assert
        assertEquals(50, xpNeeded)
    }

    @Test
    fun calculateXpToNextLevel_fromLevel2_with150Xp_returns50() {
        // Arrange
        val xpTotale = 150
        val livello = 2

        // Act
        val xpNeeded = calculateXpToNextLevel(xpTotale, livello)

        // Assert
        assertEquals(50, xpNeeded)
    }

    @Test
    fun calculateXpToNextLevel_fromLevel5_with500Xp_returns0() {
        // Arrange
        val xpTotale = 500
        val livello = 5

        // Act
        val xpNeeded = calculateXpToNextLevel(xpTotale, livello)

        // Assert
        assertEquals(0, xpNeeded)
    }

    // ===== TEST FILTRAGGIO MISSIONI =====

    @Test
    fun filterMissions_bySearchQuery_matchesTitle() {
        // Arrange
        val missionTitle = "Missione di prova"
        val searchQuery = "prova"

        // Act
        val matches = matchesSearchQuery(missionTitle, searchQuery)

        // Assert
        assertTrue(matches)
    }

    @Test
    fun filterMissions_bySearchQuery_notMatches() {
        // Arrange
        val missionTitle = "Missione di prova"
        val searchQuery = "altro"

        // Act
        val matches = matchesSearchQuery(missionTitle, searchQuery)

        // Assert
        assertFalse(matches)
    }

    @Test
    fun filterMissions_bySearchQuery_caseInsensitive() {
        // Arrange
        val missionTitle = "Missione di Prova"
        val searchQuery = "prova"

        // Act
        val matches = matchesSearchQuery(missionTitle, searchQuery)

        // Assert
        assertTrue(matches)
    }

    // ===== TEST CALCOLO PROGRESSO =====

    @Test
    fun calculateProgress_noSubtasks_returnsZero() {
        // Arrange
        val subtasks = emptyList<Task>()

        // Act
        val progress = calculateProgress(subtasks)

        // Assert
        assertEquals(0f, progress, 0.01f)
    }

    @Test
    fun calculateProgress_allSubtasksDone_returnsOne() {
        // Arrange
        val subtasks = listOf(
            Task(done = true),
            Task(done = true),
            Task(done = true)
        )

        // Act
        val progress = calculateProgress(subtasks)

        // Assert
        assertEquals(1f, progress, 0.01f)
    }

    @Test
    fun calculateProgress_halfSubtasksDone_returnsHalf() {
        // Arrange
        val subtasks = listOf(
            Task(done = true),
            Task(done = false),
            Task(done = true),
            Task(done = false)
        )

        // Act
        val progress = calculateProgress(subtasks)

        // Assert
        assertEquals(0.5f, progress, 0.01f)
    }

    @Test
    fun calculateProgress_noSubtasksDone_returnsZero() {
        // Arrange
        val subtasks = listOf(
            Task(done = false),
            Task(done = false)
        )

        // Act
        val progress = calculateProgress(subtasks)

        // Assert
        assertEquals(0f, progress, 0.01f)
    }

    // ===== FUNZIONI DI TEST (logica pura) =====

    private fun validateAndNormalizeXp(xpReward: Int, missionType: MissionType): Int {
        return if (xpReward in missionType.minXp..missionType.maxXp) {
            xpReward
        } else {
            missionType.defaultXp
        }
    }

    private fun calculateLevel(xpTotale: Int): Int {
        return (xpTotale / 100) + 1
    }

    private fun calculateXpToNextLevel(xpTotale: Int, livello: Int): Int {
        val xpForNextLevel = livello * 100
        return (xpForNextLevel - xpTotale).coerceAtLeast(0)
    }

    private fun matchesSearchQuery(title: String, query: String): Boolean {
        return title.contains(query, ignoreCase = true)
    }

    private fun calculateProgress(subtasks: List<Task>): Float {
        if (subtasks.isEmpty()) return 0f
        val done = subtasks.count { it.done }
        return done.toFloat() / subtasks.size
    }

    // Data class per i test
    private data class Task(val done: Boolean)
}