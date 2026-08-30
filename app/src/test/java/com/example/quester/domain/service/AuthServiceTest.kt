package com.example.quester.domain.service

import com.example.quester.data.model.User
import com.example.quester.data.repository.AuthRepository
import com.example.quester.data.repository.AuthResult
import com.example.quester.data.repository.UserRepository
import com.example.quester.data.session.SessionManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AuthServiceTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var userRepository: UserRepository
    private lateinit var authService: AuthService

    @Before
    fun setup() {
        authRepository = mockk()
        sessionManager = mockk(relaxed = true)
        userRepository = mockk()
        authService = AuthService(authRepository, sessionManager, userRepository)
    }

    @Test
    fun `login with valid credentials should succeed`() = runTest {
        val user = User(id = 1, username = "testuser", passwordHash = "hash")
        coEvery { authRepository.login("testuser", "password123") } returns AuthResult.Success(user)

        val result = authService.login("testuser", "password123")

        assertTrue(result is AuthResult.Success)
        coVerify { sessionManager.createSession(1) }
    }

    @Test
    fun `login with empty identity should fail`() = runTest {
        val result = authService.login("", "password123")

        assertTrue(result is AuthResult.Error)
        assertEquals("Username o email obbligatorio", (result as AuthResult.Error).message)
        coVerify(exactly = 0) { authRepository.login(any(), any()) }
    }

    @Test
    fun `register with short password should fail`() = runTest {
        val result = authService.register("newuser", "test@example.com", "short")

        assertTrue(result is AuthResult.Error)
        assertEquals("Password troppo corta (minimo 8 caratteri)", (result as AuthResult.Error).message)
        coVerify(exactly = 0) { authRepository.register(any(), any(), any()) }
    }

    @Test
    fun `register with invalid email should fail`() = runTest {
        val result = authService.register("newuser", "invalid-email", "Password123")

        assertTrue(result is AuthResult.Error)
        assertEquals("Email non valida", (result as AuthResult.Error).message)
    }
    
    @Test
    fun `register with password missing digit should fail`() = runTest {
        val result = authService.register("newuser", "test@example.com", "PasswordNoDigit")

        assertTrue(result is AuthResult.Error)
        assertEquals("La password deve contenere almeno 1 numero", (result as AuthResult.Error).message)
    }

    @Test
    fun `register with password missing uppercase should fail`() = runTest {
        val result = authService.register("newuser", "test@example.com", "password123")

        assertTrue(result is AuthResult.Error)
        assertEquals("La password deve contenere almeno 1 maiuscola", (result as AuthResult.Error).message)
    }

    @Test
    fun `register with valid data should succeed`() = runTest {
        val user = User(id = 2, username = "newuser", passwordHash = "hash")
        coEvery { authRepository.register(any(), any(), any()) } returns AuthResult.Success(user)

        val result = authService.register("newuser", "test@example.com", "Password123")

        assertTrue(result is AuthResult.Success)
        coVerify { sessionManager.createSession(2) }
    }

    @Test
    fun `logout should clear session`() = runTest {
        authService.logout()
        coVerify { sessionManager.clearSession() }
    }
}