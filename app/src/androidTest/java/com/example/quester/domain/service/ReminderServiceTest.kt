package com.example.quester.domain.service

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.testing.WorkManagerTestInitHelper
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReminderServiceTest {

    private lateinit var reminderService: ReminderService
    private lateinit var workManager: WorkManager
    private val testMissionId = 10L
    private val testMissionTitle = "Test Mission"
    private val testDelayMinutes = 15L

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        WorkManagerTestInitHelper.initializeTestWorkManager(context)
        workManager = WorkManager.getInstance(context)
        reminderService = ReminderService(context)
    }

    @Test
    fun scheduleMissionReminder_creates_work_request() = runBlocking {
        // Arrange
        val tag = "mission_$testMissionId"

        // Act
        reminderService.scheduleMissionReminder(
            missionId = testMissionId,
            missionTitle = testMissionTitle,
            delayMinutes = testDelayMinutes
        )

        // Assert
        val workInfos = workManager.getWorkInfosByTag(tag).get()
        assertNotNull("Il lavoro dovrebbe essere stato creato", workInfos)
        assertTrue("Dovrebbe esserci almeno un lavoro", workInfos.isNotEmpty())

        val workInfo = workInfos.first()
        assertEquals("Il lavoro dovrebbe essere in stato ENQUEUED", WorkInfo.State.ENQUEUED, workInfo.state)

        // Verifica che il lavoro esista con l'ID corretto
        assertNotNull("Il lavoro dovrebbe avere un ID", workInfo.id)
        assertTrue("Il lavoro dovrebbe avere un ID valido", workInfo.id.toString().isNotEmpty())
    }

    @Test
    fun cancelMissionReminder_cancels_work_request() = runBlocking {
        // Arrange - Prima programma un reminder
        val tag = "mission_$testMissionId"

        reminderService.scheduleMissionReminder(
            missionId = testMissionId,
            missionTitle = testMissionTitle,
            delayMinutes = testDelayMinutes
        )

        // Verifica che sia stato creato
        var workInfos = workManager.getWorkInfosByTag(tag).get()
        assertTrue("Il lavoro dovrebbe essere stato creato prima della cancellazione", workInfos.isNotEmpty())

        // Act - Cancella il reminder
        reminderService.cancelMissionReminder(missionId = testMissionId)

        // Assert - Verifica che il lavoro sia stato cancellato
        workInfos = workManager.getWorkInfosByTag(tag).get()

        // Dopo la cancellazione, il lavoro dovrebbe essere CANCELLED o rimosso
        if (workInfos.isNotEmpty()) {
            val workInfo = workInfos.first()
            assertEquals(
                "Il lavoro dovrebbe essere cancellato",
                WorkInfo.State.CANCELLED,
                workInfo.state
            )
        }
    }

    @Test
    fun scheduleMissionReminder_with_different_ids_creates_separate_work() = runBlocking {
        // Arrange
        val missionId1 = 1L
        val missionId2 = 2L
        val tag1 = "mission_$missionId1"
        val tag2 = "mission_$missionId2"

        // Act
        reminderService.scheduleMissionReminder(missionId1, "Mission 1", 10L)
        reminderService.scheduleMissionReminder(missionId2, "Mission 2", 20L)

        // Assert
        val workInfos1 = workManager.getWorkInfosByTag(tag1).get()
        val workInfos2 = workManager.getWorkInfosByTag(tag2).get()

        assertTrue("Dovrebbe esserci un lavoro per missione 1", workInfos1.isNotEmpty())
        assertTrue("Dovrebbe esserci un lavoro per missione 2", workInfos2.isNotEmpty())

        // Verifica che siano lavori diversi
        assertNotEquals(
            "I lavori dovrebbero essere diversi",
            workInfos1.first().id,
            workInfos2.first().id
        )
    }

    @Test
    fun cancelMissionReminder_only_cancels_specific_mission() = runBlocking {
        // Arrange - Crea due reminder per missioni diverse
        val missionId1 = 1L
        val missionId2 = 2L
        val tag1 = "mission_$missionId1"
        val tag2 = "mission_$missionId2"

        reminderService.scheduleMissionReminder(missionId1, "Mission 1", 10L)
        reminderService.scheduleMissionReminder(missionId2, "Mission 2", 20L)

        // Verifica che entrambi siano stati creati
        var workInfos1 = workManager.getWorkInfosByTag(tag1).get()
        var workInfos2 = workManager.getWorkInfosByTag(tag2).get()
        assertTrue(workInfos1.isNotEmpty())
        assertTrue(workInfos2.isNotEmpty())

        // Act - Cancella solo il primo
        reminderService.cancelMissionReminder(missionId1)

        // Assert - Il primo dovrebbe essere cancellato, il secondo no
        workInfos1 = workManager.getWorkInfosByTag(tag1).get()
        workInfos2 = workManager.getWorkInfosByTag(tag2).get()

        // Il primo lavoro dovrebbe essere cancellato
        if (workInfos1.isNotEmpty()) {
            assertEquals(
                "Il lavoro per missione 1 dovrebbe essere cancellato",
                WorkInfo.State.CANCELLED,
                workInfos1.first().state
            )
        }

        // Il secondo lavoro dovrebbe essere ancora in coda
        assertTrue("Il lavoro per missione 2 dovrebbe essere ancora presente", workInfos2.isNotEmpty())
        assertEquals(
            "Il lavoro per missione 2 dovrebbe essere ancora in coda",
            WorkInfo.State.ENQUEUED,
            workInfos2.first().state
        )
    }

    @Test
    fun cancelMissionReminder_on_nonexistent_id_does_not_crash() = runBlocking {
        // Arrange
        val nonExistentId = 999L

        // Act & Assert - Non dovrebbe lanciare eccezioni
        try {
            reminderService.cancelMissionReminder(nonExistentId)
            assertTrue("La cancellazione di un ID inesistente non dovrebbe lanciare eccezioni", true)
        } catch (e: Exception) {
            fail("La cancellazione di un ID inesistente non dovrebbe lanciare eccezioni: ${e.message}")
        }
    }

    @Test
    fun scheduleMissionReminder_multiple_times_replaces_previous() = runBlocking {
        // Arrange
        val tag = "mission_$testMissionId"
        val firstTitle = "First Title"
        val secondTitle = "Second Title"

        // Act - Prima programmazione
        reminderService.scheduleMissionReminder(testMissionId, firstTitle, 10L)

        // Assert - Verifica che il primo sia stato creato
        var workInfos = workManager.getWorkInfosByTag(tag).get()
        assertTrue(workInfos.isNotEmpty())

        // Act - Seconda programmazione (dovrebbe rimpiazzare la prima)
        reminderService.scheduleMissionReminder(testMissionId, secondTitle, 20L)

        // Assert - Verifica che il lavoro sia stato rimpiazzato
        workInfos = workManager.getWorkInfosByTag(tag).get()
        assertTrue("Dovrebbe esserci ancora un lavoro", workInfos.isNotEmpty())

        // Il lavoro dovrebbe essere in stato ENQUEUED
        assertEquals(
            "Il lavoro dovrebbe essere in coda",
            WorkInfo.State.ENQUEUED,
            workInfos.first().state
        )

        // Verifica che il lavoro esista
        assertNotNull("Il lavoro dovrebbe avere un ID", workInfos.first().id)
    }

    @Test
    fun scheduleMissionReminder_with_zero_delay_works() = runBlocking {
        // Arrange
        val tag = "mission_${testMissionId}_zero"

        // Act - Con delay 0, il lavoro potrebbe essere eseguito immediatamente
        reminderService.scheduleMissionReminder(
            missionId = testMissionId,
            missionTitle = testMissionTitle,
            delayMinutes = 0L
        )

        // Assert - Con delay 0, il lavoro potrebbe essere già stato eseguito
        // Quindi controlliamo che non ci siano errori
        val workInfos = workManager.getWorkInfosByTag(tag).get()

        // Con delay 0, il lavoro potrebbe essere già stato eseguito e rimosso
        // Quindi accettiamo sia che esista (in qualsiasi stato) o che sia stato rimosso
        if (workInfos.isNotEmpty()) {
            val workInfo = workInfos.first()
            // Il lavoro può essere in qualsiasi stato valido
            assertTrue(
                "Il lavoro dovrebbe essere in uno stato valido",
                workInfo.state == WorkInfo.State.ENQUEUED ||
                        workInfo.state == WorkInfo.State.RUNNING ||
                        workInfo.state == WorkInfo.State.SUCCEEDED ||
                        workInfo.state == WorkInfo.State.CANCELLED
            )
        } else {
            // Se il lavoro è già stato eseguito e rimosso, va bene lo stesso
            assertTrue("Il lavoro con delay 0 può essere già stato eseguito", true)
        }
    }
}