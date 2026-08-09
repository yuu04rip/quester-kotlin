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
    fun coinsForLevel_returns_correct_amount() = runBlocking {
        // Arrange
        val levelsGained = 3

        // Act
        val coins = currencyService.coinsForLevel(levelsGained)

        // Assert
        assertEquals("3 livelli dovrebbero dare 150 monete", 150, coins)
    }

    @Test
    fun coinsForLevel_with_zero_returns_zero() = runBlocking {
        // Arrange
        val levelsGained = 0

        // Act
        val coins = currencyService.coinsForLevel(levelsGained)

        // Assert
        assertEquals("0 livelli dovrebbero dare 0 monete", 0, coins)
    }

    @Test
    fun coinsForLevel_with_negative_returns_zero() = runBlocking {
        // Arrange
        val levelsGained = -5

        // Act
        val coins = currencyService.coinsForLevel(levelsGained)

        // Assert
        assertEquals("Livelli negativi dovrebbero dare 0 monete", 0, coins)
    }

    @Test
    fun onLevelUp_adds_correct_coins() = runBlocking {
        // Arrange
        val beforeLevel = 1
        val afterLevel = 5
        val expectedCoins = 200 // (5-1) * 50 = 200

        // Act
        currencyService.onLevelUp(beforeLevel, afterLevel)

        // Assert
        val user = db.userDao().getUserById(testUserId)!!
        assertEquals("Dovrebbero essere aggiunte $expectedCoins monete", expectedCoins, user.coins)
    }

    @Test
    fun onLevelUp_with_no_level_gain_adds_zero_coins() = runBlocking {
        // Arrange
        val beforeLevel = 5
        val afterLevel = 5

        // Act
        currencyService.onLevelUp(beforeLevel, afterLevel)

        // Assert
        val user = db.userDao().getUserById(testUserId)!!
        assertEquals("Nessuna moneta dovrebbe essere aggiunta", 0, user.coins)
    }

    @Test
    fun onLevelUp_with_level_decrease_adds_zero_coins() = runBlocking {
        // Arrange
        val beforeLevel = 5
        val afterLevel = 3

        // Act
        currencyService.onLevelUp(beforeLevel, afterLevel)

        // Assert
        val user = db.userDao().getUserById(testUserId)!!
        assertEquals("Nessuna moneta dovrebbe essere aggiunta", 0, user.coins)
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

    @Test
    fun multiple_level_ups_accumulate_correctly() = runBlocking {
        // Arrange
        val initialCoins = db.userDao().getUserById(testUserId)!!.coins

        // Act
        currencyService.onLevelUp(1, 3) // +100 coins
        currencyService.onLevelUp(3, 5) // +100 coins
        currencyService.onLevelUp(5, 7) // +100 coins

        // Assert
        val user = db.userDao().getUserById(testUserId)!!
        val expectedCoins = initialCoins + 300
        assertEquals("Dovrebbero essere accumulate 300 monete", expectedCoins, user.coins)
    }

    @Test
    fun onSpecialEvent_after_level_up_accumulates_correctly() = runBlocking {
        // Arrange
        val initialCoins = db.userDao().getUserById(testUserId)!!.coins

        // Act
        currencyService.onLevelUp(1, 3) // +100 coins
        currencyService.onSpecialEvent(50) // +50 coins
        currencyService.onLevelUp(3, 5) // +100 coins

        // Assert
        val user = db.userDao().getUserById(testUserId)!!
        val expectedCoins = initialCoins + 250
        assertEquals("Dovrebbero essere accumulate 250 monete", expectedCoins, user.coins)
    }

    // Il test onMissionRedeemed è stato RIMOSSO perché il metodo non esiste più
    // Le monete per le missioni vengono ora gestite direttamente da MissionService
}