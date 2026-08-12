package com.example.quester.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.quester.data.model.User
import com.example.quester.data.repository.AuthResult
import com.example.quester.domain.service.AuthService
import com.example.quester.ui.theme.QuesterTheme
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val authService: AuthService = mockk(relaxed = true)

    @Test
    fun testAuthScreenDisplaysLoginElementsByDefault() {
        composeTestRule.setContent {
            QuesterTheme {
                AuthScreen(
                    authService = authService,
                    onAuthSuccess = {}
                )
            }
        }

        composeTestRule.onNodeWithText("✦ QUESTER ✦").assertIsDisplayed()
        composeTestRule.onNodeWithText("Bentornato, avventuriero").assertIsDisplayed()
        composeTestRule.onNodeWithText("Username o Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("ENTRA NEL REGNO").assertIsDisplayed()
    }

    @Test
    fun testToggleToRegisterModeDisplaysRegistrationFields() {
        composeTestRule.setContent {
            QuesterTheme {
                AuthScreen(
                    authService = authService,
                    onAuthSuccess = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Non hai un account? Registrati").performClick()

        composeTestRule.onNodeWithText("Crea il tuo personaggio").assertIsDisplayed()
        composeTestRule.onNodeWithText("Nome avventuriero").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email (opzionale)").assertIsDisplayed()
        composeTestRule.onNodeWithText("INIZIA L'AVVENTURA").assertIsDisplayed()
    }

    @Test
    fun testLoginSuccessTriggersCallback() {
        var successCalled = false
        val mockUser = mockk<User>(relaxed = true)

        coEvery { authService.isAuthenticated } returns MutableStateFlow(false)
        coEvery { authService.login(any(), any()) } returns AuthResult.Success(mockUser)

        composeTestRule.setContent {
            QuesterTheme {
                AuthScreen(
                    authService = authService,
                    onAuthSuccess = { successCalled = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Username o Email").performTextInput("EroeTest")
        composeTestRule.onNodeWithText("Password").performTextInput("Password123")
        composeTestRule.onNodeWithText("ENTRA NEL REGNO").performClick()

        composeTestRule.waitUntil(timeoutMillis = 3000) { successCalled }
        assert(successCalled)
    }
}