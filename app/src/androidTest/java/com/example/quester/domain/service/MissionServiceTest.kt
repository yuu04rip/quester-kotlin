package com.example.quester.domain.service

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.quester.data.database.AppDatabase
import com.example.quester.data.model.User
import com.example.quester.data.repository.MissionRepository
import com.example.quester.data.repository.UserRepository
import com.example.quester.data.session.SessionManager
import com.example.quester.ui.screens.mission.model.MissionType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MissionServiceTest {

    private lateinit var db: AppDatabase
    private lateinit var missionRepository: MissionRepository
    private lateinit var userRepository: UserRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var currencyService: CurrencyService
    private lateinit var missionService: MissionService
    private var testUserId: Long = 0L

    @Before
    fun setup() = runBlocking {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = Room.inMemoryDatabaseBuilder(ctx, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        sessionManager = SessionManager(ctx)
        missionRepository = MissionRepository(db.missionDao(), db.subTaskDao())
        userRepository = UserRepository(db.userDao(), db.ownedCosmeticDao())
        currencyService = CurrencyService(userRepository, sessionManager)

        missionService = MissionService(
            missionRepository = missionRepository,
            userRepository = userRepository,
            currencyService = currencyService,
            sessionManager = sessionManager,
            securityNotificationService = null,
            reminderService = null,
            isTestMode = true
        )

        testUserId = db.userDao().insertUser(
            User(username = "tester", passwordHash = "hash", xpTotale = 0, livello = 1, coins = 0)
        )
        sessionManager.createSession(testUserId)
    }

    @After
    fun tearDown() = runBlocking {
        sessionManager.clearSession()
        db.close()
    }

    @Test
    fun toggle_all_subtasks_completes_mission_and_awards_xp_once() = runBlocking {
        // Arrange
        val missionTitle = "Study"
        val missionDescription = "Do chapters"
        val missionType = MissionType.GIORNALIERO
        val subtasks = listOf("A", "B")


        val expectedXp = missionType.xpReward  // 30 per GIORNALIERO
        val expectedCoins = missionType.coinReward  // 1 per GIORNALIERO

        // Act - Creazione missione
        missionService.createMissionFromForm(
            title = missionTitle,
            description = missionDescription,
            type = missionType.dbValue,
            dueDate = null,
            subtasks = subtasks
        )

        // Assert - Verifica creazione
        val mission = db.missionDao().getAllMissionsForUser(testUserId).first().first()
        val subtaskList = db.subTaskDao().getSubTasksByMissionId(mission.id).first()
        assertEquals("Dovrebbero esserci 2 subtask", 2, subtaskList.size)
        assertEquals("XP dovrebbe essere quello fisso del tipo", expectedXp, mission.xpReward)

        // Act - Completamento primo subtask
        missionService.toggleSubTask(subtaskList[0], true)

        // Assert - Nessun XP ancora
        val midUser = db.userDao().getUserById(testUserId)!!
        assertEquals("Nessun XP dovrebbe essere stato assegnato", 0, midUser.xpTotale)

        // Act - Completamento secondo subtask
        missionService.toggleSubTask(subtaskList[1], true)

        // Assert - Missione completata
        val updatedMission = db.missionDao().getMissionByIdOnce(mission.id)!!
        assertTrue("La missione dovrebbe essere completata", updatedMission.completed)

        // Assert - XP e monete assegnati correttamente
        val userAfter = db.userDao().getUserById(testUserId)!!
        assertEquals("XP dovrebbero essere $expectedXp (fissi per tipo)", expectedXp, userAfter.xpTotale)
        assertEquals("Monete dovrebbero essere $expectedCoins (fisse per tipo)", expectedCoins, userAfter.coins)

        // Act - Tentativo di toggle dello stesso subtask
        try {
            missionService.toggleSubTask(subtaskList[1].copy(done = true), true)
            fail("Dovrebbe lanciare IllegalStateException perché la missione è già completata")
        } catch (e: IllegalStateException) {
            assertEquals("✧ Questa missione è già stata completata ✧", e.message)
        }

        // Assert - Nessun doppio XP
        val userAfterSecond = db.userDao().getUserById(testUserId)!!
        assertEquals("XP non dovrebbero raddoppiare", expectedXp, userAfterSecond.xpTotale)

        // Verifica che la missione sia ancora completata
        val finalMission = db.missionDao().getMissionByIdOnce(mission.id)!!
        assertTrue("La missione dovrebbe rimanere completata", finalMission.completed)
    }

    @Test
    fun delete_mission_removes_it_and_its_subtasks() = runBlocking {
        // Arrange
        val missionTitle = "Missione da Eliminare"
        val missionDescription = "Test cancella"
        val missionType = MissionType.GIORNALIERO
        val subtaskList = listOf("Subtask 1", "Subtask 2")

        missionService.createMissionFromForm(
            title = missionTitle,
            description = missionDescription,
            type = missionType.dbValue,
            dueDate = null,
            subtasks = subtaskList
        )

        val mission = db.missionDao().getAllMissionsForUser(testUserId).first().first()
        val initialSubtasks = db.subTaskDao().getSubTasksByMissionId(mission.id).first()
        assertEquals("Dovrebbero esserci 2 subtask", 2, initialSubtasks.size)

        // Act
        missionService.deleteMission(mission)

        // Assert
        val deletedMission = db.missionDao().getMissionByIdOnce(mission.id)
        assertNull("La missione dovrebbe essere eliminata", deletedMission)

        val subtasksAfterDelete = db.subTaskDao().getSubTasksByMissionId(mission.id).first()
        assertTrue("I subtask dovrebbero essere stati eliminati", subtasksAfterDelete.isEmpty())
    }

    @Test
    fun create_mission_uses_fixed_xp_for_type() = runBlocking {
        // Arrange - Test per ogni tipo di missione
        val testCases = listOf(
            MissionType.GIORNALIERO to 30,
            MissionType.SETTIMANALE to 120,
            MissionType.SPECIALE to 400
        )

        testCases.forEachIndexed { index, (missionType, expectedXp) ->
            // Act
            missionService.createMissionFromForm(
                title = "Test XP ${missionType.label} $index",
                description = "Test XP fisso per ${missionType.label}",
                type = missionType.dbValue,
                dueDate = null,
                subtasks = listOf("Task 1")
            )

            // Assert
            val missions = db.missionDao().getAllMissionsForUser(testUserId).first()
            val mission = missions.find { it.title == "Test XP ${missionType.label} $index" }
            assertNotNull("Missione ${missionType.label} non trovata", mission)
            assertEquals(
                "XP per ${missionType.label} dovrebbe essere $expectedXp",
                expectedXp,
                mission?.xpReward
            )
        }
    }

    @Test
    fun update_mission_all_fields_correctly() = runBlocking {
        // Arrange
        val originalTitle = "Original Title"
        val originalDesc = "Original Description"
        val originalType = MissionType.GIORNALIERO

        missionService.createMissionFromForm(
            title = originalTitle,
            description = originalDesc,
            type = originalType.dbValue,
            dueDate = null,
            subtasks = listOf("Task 1", "Task 2")
        )

        val mission = db.missionDao().getAllMissionsForUser(testUserId).first().first()

        // Act
        val newTitle = "Updated Title"
        val newDesc = "Updated Description"
        val newType = MissionType.SETTIMANALE
        val newSubtasks = listOf("New Task 1", "New Task 2", "New Task 3")

        missionService.updateMissionFromForm(
            mission = mission,
            newTitle = newTitle,
            newDescription = newDesc,
            newType = newType.dbValue,
            newSubtasksText = newSubtasks
        )

        // Assert
        val updatedMission = db.missionDao().getMissionByIdOnce(mission.id)!!
        assertEquals("Titolo dovrebbe essere aggiornato", newTitle, updatedMission.title)
        assertEquals("Descrizione dovrebbe essere aggiornata", newDesc, updatedMission.description)
        assertEquals("Tipo dovrebbe essere aggiornato", newType.dbValue, updatedMission.type)
        assertEquals("XP dovrebbe essere quello del nuovo tipo (${newType.xpReward})", newType.xpReward, updatedMission.xpReward)

        val updatedSubtasks = db.subTaskDao().getSubTasksByMissionId(mission.id).first()
        assertEquals("Dovrebbero esserci 3 subtask", 3, updatedSubtasks.size)
        assertEquals("Primo subtask dovrebbe essere aggiornato", "New Task 1", updatedSubtasks[0].text)
        assertFalse("Subtask dovrebbe essere non completato", updatedSubtasks[0].done)
    }

    @Test
    fun restore_mission_restores_mission_and_subtasks() = runBlocking {
        // Arrange
        val missionTitle = "Missione da Ripristinare"
        val subtaskList = listOf("Subtask 1", "Subtask 2", "Subtask 3")
        val missionType = MissionType.GIORNALIERO

        missionService.createMissionFromForm(
            title = missionTitle,
            description = "Test ripristino",
            type = missionType.dbValue,
            dueDate = null,
            subtasks = subtaskList
        )

        val mission = db.missionDao().getAllMissionsForUser(testUserId).first().first()
        val subtasks = db.subTaskDao().getSubTasksByMissionId(mission.id).first()

        // Act - Eliminazione
        missionService.deleteMission(mission)

        // Assert - Verifica eliminazione
        assertNull("Missione dovrebbe essere eliminata", db.missionDao().getMissionByIdOnce(mission.id))
        assertTrue("Subtask dovrebbero essere eliminati", db.subTaskDao().getSubTasksByMissionId(mission.id).first().isEmpty())

        // Act - Ripristino
        missionService.restoreMission(mission, subtasks)

        // Assert - Verifica ripristino
        val restoredMission = db.missionDao().getMissionByIdOnce(mission.id)!!
        assertEquals("Missione dovrebbe essere ripristinata", missionTitle, restoredMission.title)
        assertEquals("XP dovrebbe essere quello del tipo (${missionType.xpReward})", missionType.xpReward, restoredMission.xpReward)
        assertFalse("Missione dovrebbe essere non completata", restoredMission.completed)

        val restoredSubtasks = db.subTaskDao().getSubTasksByMissionId(mission.id).first()
        assertEquals("Dovrebbero esserci 3 subtask", 3, restoredSubtasks.size)
        assertEquals("Primo subtask dovrebbe essere ripristinato", "Subtask 1", restoredSubtasks[0].text)
        assertFalse("Subtask dovrebbe essere non completato", restoredSubtasks[0].done)
    }

    @Test
    fun create_mission_with_empty_subtasks_creates_mission_without_subtasks() = runBlocking {
        // Arrange
        val missionTitle = "Missione senza subtask"
        val missionType = MissionType.GIORNALIERO

        // Act
        missionService.createMissionFromForm(
            title = missionTitle,
            description = "Test senza subtask",
            type = missionType.dbValue,
            dueDate = null,
            subtasks = emptyList()
        )

        // Assert
        val mission = db.missionDao().getAllMissionsForUser(testUserId).first().first()
        assertEquals("Missione dovrebbe essere creata", missionTitle, mission.title)
        assertEquals("XP dovrebbe essere quello del tipo (${missionType.xpReward})", missionType.xpReward, mission.xpReward)

        val subtasks = db.subTaskDao().getSubTasksByMissionId(mission.id).first()
        assertTrue("Non dovrebbero esserci subtask", subtasks.isEmpty())
    }

    @Test
    fun create_mission_with_whitespace_subtasks_filters_them_out() = runBlocking {
        // Arrange
        val subtasksWithWhitespace = listOf("  ", "Task 1", "   ", "Task 2", "")
        val missionType = MissionType.GIORNALIERO

        // Act
        missionService.createMissionFromForm(
            title = "Test Whitespace",
            description = "Test filtraggio whitespace",
            type = missionType.dbValue,
            dueDate = null,
            subtasks = subtasksWithWhitespace
        )

        // Assert
        val mission = db.missionDao().getAllMissionsForUser(testUserId).first().first()
        val subtasks = db.subTaskDao().getSubTasksByMissionId(mission.id).first()
        assertEquals("Dovrebbero esserci solo 2 subtask validi", 2, subtasks.size)
        assertEquals("Primo subtask dovrebbe essere 'Task 1'", "Task 1", subtasks[0].text)
        assertEquals("Secondo subtask dovrebbe essere 'Task 2'", "Task 2", subtasks[1].text)
        assertEquals("XP dovrebbe essere quello del tipo (${missionType.xpReward})", missionType.xpReward, mission.xpReward)
    }

    @Test
    fun toggle_subtasks_too_fast_does_not_complete_mission() = runBlocking {
        // Crea un service senza test mode per questo test
        val realMissionService = MissionService(
            missionRepository = missionRepository,
            userRepository = userRepository,
            currencyService = currencyService,
            sessionManager = sessionManager,
            securityNotificationService = null,
            reminderService = null,
            isTestMode = false
        )

        // Arrange
        val missionType = MissionType.GIORNALIERO
        realMissionService.createMissionFromForm(
            title = "Fast Mission",
            description = "Test completamento veloce",
            type = missionType.dbValue,
            dueDate = null,
            subtasks = listOf("A", "B")
        )

        val mission = db.missionDao().getAllMissionsForUser(testUserId).first().first()
        val subtasks = db.subTaskDao().getSubTasksByMissionId(mission.id).first()

        // Act - Completa subito entrambi i subtask (troppo veloce)
        realMissionService.toggleSubTask(subtasks[0], true)
        realMissionService.toggleSubTask(subtasks[1], true)

        // Assert - La missione NON dovrebbe essere completata
        val updatedMission = db.missionDao().getMissionByIdOnce(mission.id)!!
        assertFalse("La missione non dovrebbe essere completata (troppo veloce)", updatedMission.completed)

        val user = db.userDao().getUserById(testUserId)!!
        assertEquals("Nessun XP dovrebbe essere stato assegnato", 0, user.xpTotale)
    }
}