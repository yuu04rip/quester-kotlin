package com.example.quester.domain.service

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.quester.data.database.AppDatabase
import com.example.quester.data.model.User
import com.example.quester.data.repository.UserRepository
import com.example.quester.data.session.SessionManager
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CurrencyServiceTest {

    private lateinit var db: AppDatabase
    private lateinit var userRepository: UserRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var currencyService: CurrencyService
    private var testUserId: Long = 0L

    @Before
    fun setup() = runBlocking {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = Room.inMemoryDatabaseBuilder(ctx, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        sessionManager = SessionManager(ctx)
        userRepository = UserRepository(db.userDao(), db.ownedCosmeticDao())
        currencyService = CurrencyService(userRepository, sessionManager)

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
    fun onSpecialEvent_adds_correct_coins() = runBlocking {
        // Arrange
        val coinsToAdd = 100

        // Act
        currencyService.onSpecialEvent(coinsToAdd)

        // Assert
        val user = db.userDao().getUserById(testUserId)!!
        assertEquals("Dovrebbero essere aggiunte $coinsToAdd monete", coinsToAdd, user.coins)
    }

    @Test
    fun onSpecialEvent_with_negative_coins_adds_zero() = runBlocking {
        // Arrange
        val coinsToAdd = -50

        // Act
        currencyService.onSpecialEvent(coinsToAdd)

        // Assert
        val user = db.userDao().getUserById(testUserId)!!
        assertEquals("Nessuna moneta dovrebbe essere aggiunta", 0, user.coins)
    }

    @Test
    fun onSpecialEvent_with_zero_adds_zero() = runBlocking {
        // Arrange
        val coinsToAdd = 0

        // Act
        currencyService.onSpecialEvent(coinsToAdd)

        // Assert
        val user = db.userDao().getUserById(testUserId)!!
        assertEquals("Nessuna moneta dovrebbe essere aggiunta", 0, user.coins)
    }

    // Il test onMissionRedeemed è stato RIMOSSO perché il metodo non esiste più
    // Le monete per le missioni vengono ora gestite direttamente da MissionService
}