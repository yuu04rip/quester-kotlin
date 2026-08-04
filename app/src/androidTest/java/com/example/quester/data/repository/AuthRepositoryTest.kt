package com.example.quester.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.quester.data.database.AppDatabase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthRepositoryTest {

    private lateinit var db: AppDatabase
    private lateinit var authRepository: AuthRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        authRepository = AuthRepository(db.userDao())
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun register_newUser_success() = runBlocking {
        val result = authRepository.register("mario", "password123")

        assertTrue(result is AuthResult.Success)
        val success = result as AuthResult.Success
        assertEquals("mario", success.user.username)
        assertTrue(success.user.passwordHash.isNotBlank())
    }

    @Test
    fun register_duplicateUsername_fails() = runBlocking {
        authRepository.register("mario", "password123")
        val result2 = authRepository.register("mario", "password456")

        assertTrue(result2 is AuthResult.Error)
        val error = result2 as AuthResult.Error
        assertTrue(error.message.contains("esistente", ignoreCase = true))
    }

    @Test
    fun login_correctCredentials_success() = runBlocking {
        authRepository.register("luigi", "securePass1")
        val login = authRepository.login("luigi", "securePass1")

        assertTrue(login is AuthResult.Success)
        val success = login as AuthResult.Success
        assertEquals("luigi", success.user.username)
    }

    @Test
    fun login_wrongPassword_fails() = runBlocking {
        authRepository.register("peach", "correctPass")
        val login = authRepository.login("peach", "wrongPass")

        assertTrue(login is AuthResult.Error)
        val error = login as AuthResult.Error
        assertTrue(error.message.contains("credenziali", ignoreCase = true))
    }

    @Test
    fun login_unknownUser_fails() = runBlocking {
        val login = authRepository.login("bowser", "anyPass")

        assertTrue(login is AuthResult.Error)
    }

    @Test
    fun register_shortPassword_fails() = runBlocking {
        val result = authRepository.register("toad", "123")

        assertTrue(result is AuthResult.Error)
        val error = result as AuthResult.Error
        assertTrue(error.message.contains("Password", ignoreCase = true))
    }
}