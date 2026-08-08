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
    fun register_newUser_success_withOptionalEmail() = runBlocking {
        val result = authRepository.register("mario", "mario@test.com", "Password1")

        assertTrue(result is AuthResult.Success)
        val success = result as AuthResult.Success
        assertEquals("mario", success.user.username)
        assertEquals("mario@test.com", success.user.email)
        assertTrue(success.user.passwordHash.isNotBlank())
    }

    @Test
    fun register_newUser_success_withoutEmail() = runBlocking {
        val result = authRepository.register("luigi", null, "Password1")

        assertTrue(result is AuthResult.Success)
        val success = result as AuthResult.Success
        assertEquals("luigi", success.user.username)
        assertEquals(null, success.user.email)
    }

    @Test
    fun register_duplicateUsername_fails() = runBlocking {
        authRepository.register("mario", "mario1@test.com", "Password1")
        val result2 = authRepository.register("mario", "mario2@test.com", "Password2")

        assertTrue(result2 is AuthResult.Error)
        val error = result2 as AuthResult.Error
        assertTrue(error.message.contains("username", ignoreCase = true))
    }

    @Test
    fun register_duplicateEmail_fails() = runBlocking {
        authRepository.register("mario", "same@test.com", "Password1")
        val result2 = authRepository.register("luigi", "same@test.com", "Password2")

        assertTrue(result2 is AuthResult.Error)
        val error = result2 as AuthResult.Error
        assertTrue(error.message.contains("email", ignoreCase = true))
    }

    @Test
    fun login_correctCredentials_withUsername_success() = runBlocking {
        authRepository.register("peach", "peach@test.com", "Password1")
        val login = authRepository.login("peach", "Password1")

        assertTrue(login is AuthResult.Success)
        val success = login as AuthResult.Success
        assertEquals("peach", success.user.username)
    }

    @Test
    fun login_correctCredentials_withEmail_success() = runBlocking {
        authRepository.register("toad", "toad@test.com", "Password1")
        val login = authRepository.login("toad@test.com", "Password1")

        assertTrue(login is AuthResult.Success)
        val success = login as AuthResult.Success
        assertEquals("toad", success.user.username)
    }

    @Test
    fun login_wrongPassword_fails() = runBlocking {
        authRepository.register("yoshi", "yoshi@test.com", "Password1")
        val login = authRepository.login("yoshi", "WrongPass1")

        assertTrue(login is AuthResult.Error)
        val error = login as AuthResult.Error
        assertTrue(error.message.contains("credenziali", ignoreCase = true))
    }

    @Test
    fun login_unknownUser_fails() = runBlocking {
        val login = authRepository.login("bowser", "AnyPass1")
        assertTrue(login is AuthResult.Error)
    }
}