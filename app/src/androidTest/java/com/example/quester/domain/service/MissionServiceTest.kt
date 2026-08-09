package com.example.quester.domain.service

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.quester.data.database.AppDatabase
import com.example.quester.data.model.User
import com.example.quester.data.repository.MissionRepository
import com.example.quester.data.repository.UserRepository
import com.example.quester.data.session.SessionManager
import com.example.quester.ui.screens.getMissionCoins
import com.example.quester.ui.screens.getMissionXp
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
        val missionType = "NORMAL"
        val xpReward = 20
        val subtasks = listOf("A", "B")

        // Calcola l'XP e le monete attese con lo scaling (livello 1)
        val expectedXp = getMissionXp(missionType, 1)  // 100 XP per livello 1
        val expectedCoins = getMissionCoins(missionType, 1)  // 20 coin per livello 1

        // Act - Creazione missione
        missionService.createMissionFromForm(
            title = missionTitle,
            description = missionDescription,
            type = missionType,
            dueDate = null,
            xpReward = xpReward,
            subtasks = subtasks
        )

        // Assert - Verifica creazione
        val mission = db.missionDao().getAllMissionsForUser(testUserId).first().first()
        val subtaskList = db.subTaskDao().getSubTasksByMissionId(mission.id).first()
        assertEquals("Dovrebbero esserci 2 subtask", 2, subtaskList.size)

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

        // Assert - XP e monete assegnati correttamente (con scaling)
        val userAfter = db.userDao().getUserById(testUserId)!!
        assertEquals("XP dovrebbero essere $expectedXp (scalati)", expectedXp, userAfter.xpTotale)
        assertEquals("Monete dovrebbero essere $expectedCoins (scalate)", expectedCoins, userAfter.coins)

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
        val missionType = "NORMAL"
        val xpReward = 20
        val subtaskList = listOf("Subtask 1", "Subtask 2")

        missionService.createMissionFromForm(
            title = missionTitle,
            description = missionDescription,
            type = missionType,
            dueDate = null,
            xpReward = xpReward,
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
    fun create_mission_with_xp_above_range_uses_default() = runBlocking {
        // Arrange
        val aboveMaxXp = 999
        val expectedXp = MissionType.GIORNALIERO.defaultXp // 20

        // Act
        missionService.createMissionFromForm(
            title = "Test XP Max",
            description = "Test validazione XP max",
            type = MissionType.GIORNALIERO.dbValue,
            dueDate = null,
            xpReward = aboveMaxXp,
            subtasks = listOf("Task 1")
        )

        // Assert
        val mission = db.missionDao().getAllMissionsForUser(testUserId).first().first()
        assertEquals(
            "XP fuori range (sopra il massimo) dovrebbe usare il default (${MissionType.GIORNALIERO.defaultXp})",
            expectedXp,
            mission.xpReward
        )
    }

    @Test
    fun create_mission_with_xp_below_range_uses_default() = runBlocking {
        // Arrange
        val belowMinXp = 1
        val expectedXp = MissionType.GIORNALIERO.defaultXp // 20

        // Act
        missionService.createMissionFromForm(
            title = "Test XP Min",
            description = "Test validazione XP min",
            type = MissionType.GIORNALIERO.dbValue,
            dueDate = null,
            xpReward = belowMinXp,
            subtasks = listOf("Task 1")
        )

        // Assert
        val mission = db.missionDao().getAllMissionsForUser(testUserId).first().first()
        assertEquals(
            "XP fuori range (sotto il minimo) dovrebbe usare il default (${MissionType.GIORNALIERO.defaultXp})",
            expectedXp,
            mission.xpReward
        )
    }

    @Test
    fun create_mission_with_valid_xp_uses_provided_value() = runBlocking {
        // Arrange
        val validXp = 30

        // Act
        missionService.createMissionFromForm(
            title = "Test XP Valid",
            description = "Test validazione XP valido",
            type = MissionType.GIORNALIERO.dbValue,
            dueDate = null,
            xpReward = validXp,
            subtasks = listOf("Task 1")
        )

        // Assert
        val mission = db.missionDao().getAllMissionsForUser(testUserId).first().first()
        assertEquals("XP valido dovrebbe essere mantenuto", validXp, mission.xpReward)
    }

    @Test
    fun create_mission_with_xp_at_boundary_uses_boundary_value() = runBlocking {
        val testCases = listOf(
            MissionType.GIORNALIERO.minXp to MissionType.GIORNALIERO.minXp,
            MissionType.GIORNALIERO.maxXp to MissionType.GIORNALIERO.maxXp,
            MissionType.GIORNALIERO.defaultXp to MissionType.GIORNALIERO.defaultXp
        )

        testCases.forEachIndexed { index, (input, expected) ->
            missionService.createMissionFromForm(
                title = "Test XP Boundary $index",
                description = "Test XP ai limiti",
                type = MissionType.GIORNALIERO.dbValue,
                dueDate = null,
                xpReward = input,
                subtasks = listOf("Task 1")
            )

            val missions = db.missionDao().getAllMissionsForUser(testUserId).first()
            val mission = missions.find { it.title == "Test XP Boundary $index" }
            assertNotNull("Missione $index non trovata", mission)
            assertEquals("XP ai limiti ($input) dovrebbe essere mantenuto", expected, mission?.xpReward)
        }
    }

    @Test
    fun update_mission_all_fields_correctly() = runBlocking {
        // Arrange
        val originalTitle = "Original Title"
        val originalDesc = "Original Description"
        val originalType = "NORMAL"
        val originalXp = 20

        missionService.createMissionFromForm(
            title = originalTitle,
            description = originalDesc,
            type = originalType,
            dueDate = null,
            xpReward = originalXp,
            subtasks = listOf("Task 1", "Task 2")
        )

        val mission = db.missionDao().getAllMissionsForUser(testUserId).first().first()

        // Act
        val newTitle = "Updated Title"
        val newDesc = "Updated Description"
        val newType = MissionType.SETTIMANALE.dbValue
        val newXp = 150
        val newSubtasks = listOf("New Task 1", "New Task 2", "New Task 3")

        missionService.updateMissionFromForm(
            mission = mission,
            newTitle = newTitle,
            newDescription = newDesc,
            newType = newType,
            newXpReward = newXp,
            newSubtasksText = newSubtasks
        )

        // Assert
        val updatedMission = db.missionDao().getMissionByIdOnce(mission.id)!!
        assertEquals("Titolo dovrebbe essere aggiornato", newTitle, updatedMission.title)
        assertEquals("Descrizione dovrebbe essere aggiornata", newDesc, updatedMission.description)
        assertEquals("Tipo dovrebbe essere aggiornato", newType, updatedMission.type)
        assertEquals("XP dovrebbe essere aggiornato", newXp, updatedMission.xpReward)

        val updatedSubtasks = db.subTaskDao().getSubTasksByMissionId(mission.id).first()
        assertEquals("Dovrebbero esserci 3 subtask", 3, updatedSubtasks.size)
        assertEquals("Primo subtask dovrebbe essere aggiornato", "New Task 1", updatedSubtasks[0].text)
        assertFalse("Subtask dovrebbe essere non completato", updatedSubtasks[0].done)
    }

    @Test
    fun update_mission_with_invalid_xp_uses_default() = runBlocking {
        // Arrange
        missionService.createMissionFromForm(
            title = "Original Mission",
            description = "Original Description",
            type = "NORMAL",
            dueDate = null,
            xpReward = 20,
            subtasks = listOf("Task 1")
        )

        val mission = db.missionDao().getAllMissionsForUser(testUserId).first().first()
        val expectedXp = MissionType.GIORNALIERO.defaultXp // 20

        // Act
        missionService.updateMissionFromForm(
            mission = mission,
            newTitle = "Updated Mission",
            newDescription = "Updated Description",
            newType = MissionType.GIORNALIERO.dbValue,
            newXpReward = 999,
            newSubtasksText = listOf("Task 1")
        )

        // Assert
        val updatedMission = db.missionDao().getMissionByIdOnce(mission.id)!!
        assertEquals(
            "XP fuori range nell'update dovrebbe usare il default (${MissionType.GIORNALIERO.defaultXp})",
            expectedXp,
            updatedMission.xpReward
        )
    }

    @Test
    fun restore_mission_restores_mission_and_subtasks() = runBlocking {
        // Arrange
        val missionTitle = "Missione da Ripristinare"
        val subtaskList = listOf("Subtask 1", "Subtask 2", "Subtask 3")

        missionService.createMissionFromForm(
            title = missionTitle,
            description = "Test ripristino",
            type = "NORMAL",
            dueDate = null,
            xpReward = 20,
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
        assertEquals("XP dovrebbe essere ripristinato", 20, restoredMission.xpReward)
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

        // Act
        missionService.createMissionFromForm(
            title = missionTitle,
            description = "Test senza subtask",
            type = "NORMAL",
            dueDate = null,
            xpReward = 20,
            subtasks = emptyList()
        )

        // Assert
        val mission = db.missionDao().getAllMissionsForUser(testUserId).first().first()
        assertEquals("Missione dovrebbe essere creata", missionTitle, mission.title)

        val subtasks = db.subTaskDao().getSubTasksByMissionId(mission.id).first()
        assertTrue("Non dovrebbero esserci subtask", subtasks.isEmpty())
    }

    @Test
    fun create_mission_with_whitespace_subtasks_filters_them_out() = runBlocking {
        // Arrange
        val subtasksWithWhitespace = listOf("  ", "Task 1", "   ", "Task 2", "")

        // Act
        missionService.createMissionFromForm(
            title = "Test Whitespace",
            description = "Test filtraggio whitespace",
            type = "NORMAL",
            dueDate = null,
            xpReward = 20,
            subtasks = subtasksWithWhitespace
        )

        // Assert
        val mission = db.missionDao().getAllMissionsForUser(testUserId).first().first()
        val subtasks = db.subTaskDao().getSubTasksByMissionId(mission.id).first()
        assertEquals("Dovrebbero esserci solo 2 subtask validi", 2, subtasks.size)
        assertEquals("Primo subtask dovrebbe essere 'Task 1'", "Task 1", subtasks[0].text)
        assertEquals("Secondo subtask dovrebbe essere 'Task 2'", "Task 2", subtasks[1].text)
    }

    @Test
    fun create_mission_with_special_type_uses_correct_range() = runBlocking {
        // Arrange
        val testCases = listOf(
            MissionType.GIORNALIERO to 999,
            MissionType.SETTIMANALE to 999,
            MissionType.SPECIALE to 1500
        )

        testCases.forEach { (missionType, invalidXp) ->
            // Act
            missionService.createMissionFromForm(
                title = "Test ${missionType.label}",
                description = "Test range ${missionType.label}",
                type = missionType.dbValue,
                dueDate = null,
                xpReward = invalidXp,
                subtasks = listOf("Task 1")
            )

            // Assert
            val missions = db.missionDao().getAllMissionsForUser(testUserId).first()
            val mission = missions.find { it.title == "Test ${missionType.label}" }
            assertNotNull("Missione ${missionType.label} non trovata", mission)
            assertEquals(
                "XP per ${missionType.label} dovrebbe usare il default (${missionType.defaultXp})",
                missionType.defaultXp,
                mission?.xpReward
            )
        }
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
        realMissionService.createMissionFromForm(
            title = "Fast Mission",
            description = "Test completamento veloce",
            type = "NORMAL",
            dueDate = null,
            xpReward = 20,
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

    @Test
    fun scaling_rewards_increase_with_player_level() = runBlocking {
        // Arrange - Crea un utente di livello più alto
        val highLevelUserId = db.userDao().insertUser(
            User(username = "highlevel", passwordHash = "hash", xpTotale = 0, livello = 10, coins = 0)
        )
        sessionManager.createSession(highLevelUserId)

        // Act - Crea e completa una missione
        val missionTitle = "Scaled Mission"
        missionService.createMissionFromForm(
            title = missionTitle,
            description = "Test scaling",
            type = "NORMAL",
            dueDate = null,
            xpReward = 20,
            subtasks = listOf("A", "B")
        )

        val mission = db.missionDao().getAllMissionsForUser(highLevelUserId).first().first()
        val subtasks = db.subTaskDao().getSubTasksByMissionId(mission.id).first()

        // Completa entrambi i subtask
        missionService.toggleSubTask(subtasks[0], true)
        missionService.toggleSubTask(subtasks[1], true)

        // Assert - Verifica ricompense scalate per livello 10
        val userAfter = db.userDao().getUserById(highLevelUserId)!!
        val expectedXp = getMissionXp("NORMAL", 10)  // 100 + (10-1) * 5 = 145
        val expectedCoins = getMissionCoins("NORMAL", 10)  // 20 + (10-1) * 1 = 29

        assertEquals("XP dovrebbero essere scalati per livello 10", expectedXp, userAfter.xpTotale)
        assertEquals("Monete dovrebbero essere scalate per livello 10", expectedCoins, userAfter.coins)

        // Cleanup
        sessionManager.clearSession()
    }
}