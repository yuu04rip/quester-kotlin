package com.example.quester.ui.screens.mission

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.quester.data.model.Mission
import com.example.quester.data.model.MissionWithSubTasks
import com.example.quester.data.repository.MissionRepository
import com.example.quester.data.repository.UserRepository
import com.example.quester.data.session.SessionManager
import com.example.quester.domain.service.MissionService
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MissionListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val missionService = mockk<MissionService>(relaxed = true)
    private val missionRepository = mockk<MissionRepository>(relaxed = true)
    private val userRepository = mockk<UserRepository>(relaxed = true)
    private val sessionManager = mockk<SessionManager>(relaxed = true)

    private val testMission = Mission(
        id = 1,
        userId = 1,
        title = "Test Mission",
        description = "Description",
        type = "GIORNALIERO",
        xpReward = 30
    )
    private val missionWithTasks = MissionWithSubTasks(testMission, emptyList())

    @Before
    fun setup() {
        every { sessionManager.loggedUserId } returns flowOf(1L)
        every { missionRepository.getAllMissionsWithSubTasksForUser(1L) } returns flowOf(listOf(missionWithTasks))
        every { userRepository.getUserByIdFlow(1L) } returns flowOf(null)
    }

    @Test
    fun clicking_mission_card_opens_detail_dialog() {
        composeTestRule.setContent {
            MissionListScreen(
                missionService = missionService,
                missionRepository = missionRepository,
                userRepository = userRepository,
                sessionManager = sessionManager
            )
        }

        // Verifica che la missione sia visibile
        composeTestRule.onNodeWithText("Test Mission").assertExists()

        // Clicca sulla missione
        composeTestRule.onNodeWithText("Test Mission").performClick()

        // Verifica che il dialog di dettaglio sia aperto
        // Il dialog di dettaglio contiene il testo "Task della missione:" se ci sono subtask, 
        // oppure il tasto "Chiudi".
        composeTestRule.onNodeWithText("Chiudi").assertExists()
    }

    @Test
    fun clicking_add_button_opens_add_dialog() {
        composeTestRule.setContent {
            MissionListScreen(
                missionService = missionService,
                missionRepository = missionRepository,
                userRepository = userRepository,
                sessionManager = sessionManager
            )
        }

        // Clicca sul pulsante "Nuova"
        composeTestRule.onNodeWithText("Nuova").performClick()

        // Verifica che il dialog di creazione sia aperto
        // Il titolo contiene "Nuova Missione" con dei simboli
        composeTestRule.onNodeWithText("✦ Nuova Missione ✦").assertExists()
    }
}