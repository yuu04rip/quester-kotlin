package com.example.quester.domain.service

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.quester.data.database.AppDatabase
import com.example.quester.data.model.ShopItem
import com.example.quester.data.model.User
import com.example.quester.data.repository.UserRepository
import com.example.quester.data.session.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ShopServiceTest {

    private lateinit var db: AppDatabase
    private lateinit var userRepository: UserRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var shopService: ShopService
    private var testUserId: Long = 0L

    @Before
    fun setup() = runBlocking {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = Room.inMemoryDatabaseBuilder(ctx, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        sessionManager = SessionManager(ctx)
        userRepository = UserRepository(db.userDao(), db.ownedCosmeticDao())
        shopService = ShopService(
            userRepository = userRepository,
            shopDao = db.shopDao(),
            ownedDao = db.ownedCosmeticDao(),
            sessionManager = sessionManager
        )

        testUserId = db.userDao().insertUser(
            User(username = "buyer", passwordHash = "hash", xpTotale = 0, livello = 1, coins = 200)
        )

        sessionManager.createSession(testUserId)

        // Inserisci oggetti nello shop
        db.shopDao().upsertItems(
            listOf(
                ShopItem(itemId = "hat_1", name = "Cool Hat", price = 120),
                ShopItem(itemId = "skin_1", name = "Blue Skin", price = 300)
            )
        )
    }

    @After
    fun tearDown() = runBlocking {
        sessionManager.clearSession()
        db.close()
    }

    @Test
    fun buyItem_success_scales_coins_and_owns_item() = runBlocking {
        // Arrange
        val itemId = "hat_1"
        val itemPrice = 120
        val expectedCoins = 200 - itemPrice

        // Act
        val result = shopService.buyItem(itemId)

        // Assert
        assertTrue("L'acquisto dovrebbe avere successo", result)

        val user = db.userDao().getUserById(testUserId)!!
        assertEquals("Le monete dovrebbero essere scalate correttamente", expectedCoins, user.coins)

        val owned = db.ownedCosmeticDao().isOwned(testUserId, itemId)
        assertTrue("L'oggetto dovrebbe essere posseduto", owned)
    }

    @Test
    fun buyItem_insufficient_funds_returns_false() = runBlocking {
        // Arrange
        val itemId = "skin_1"  // Prezzo 300, ma l'utente ha 200 monete

        // Act
        val result = shopService.buyItem(itemId)

        // Assert
        assertFalse("L'acquisto dovrebbe fallire per fondi insufficienti", result)

        val user = db.userDao().getUserById(testUserId)!!
        assertEquals("Le monete non dovrebbero essere scalate", 200, user.coins)

        val owned = db.ownedCosmeticDao().isOwned(testUserId, itemId)
        assertFalse("L'oggetto non dovrebbe essere posseduto", owned)
    }

    @Test
    fun buyItem_item_not_found_returns_false() = runBlocking {
        // Arrange
        val invalidItemId = "not_exists"

        // Act
        val result = shopService.buyItem(invalidItemId)

        // Assert
        assertFalse("L'acquisto dovrebbe fallire per oggetto non trovato", result)

        val user = db.userDao().getUserById(testUserId)!!
        assertEquals("Le monete non dovrebbero essere scalate", 200, user.coins)
    }

    @Test
    fun buyItem_already_owned_returns_false() = runBlocking {
        // Arrange
        val itemId = "hat_1"

        // Act - Primo acquisto (successo)
        val firstResult = shopService.buyItem(itemId)
        assertTrue("Il primo acquisto dovrebbe avere successo", firstResult)

        // Act - Secondo acquisto (dovrebbe fallire perché già posseduto)
        val secondResult = shopService.buyItem(itemId)

        // Assert
        assertFalse("Il secondo acquisto dovrebbe fallire per oggetto già posseduto", secondResult)

        val user = db.userDao().getUserById(testUserId)!!
        assertEquals("Le monete non dovrebbero essere scalate una seconda volta", 80, user.coins)

        // Verifica che l'oggetto sia ancora posseduto una volta sola
        val ownedList = db.ownedCosmeticDao().getOwnedByUser(testUserId).first()
        val ownedCount = ownedList.count { owned -> owned.itemId == itemId }
        assertEquals("L'oggetto dovrebbe essere posseduto una sola volta", 1, ownedCount)
    }

    @Test
    fun buyItem_with_zero_coins_returns_false() = runBlocking {
        // Arrange - Crea un utente con 0 monete
        val poorUserId = db.userDao().insertUser(
            User(username = "poor", passwordHash = "hash", xpTotale = 0, livello = 1, coins = 0)
        )
        sessionManager.createSession(poorUserId)

        // Act
        val result = shopService.buyItem("hat_1")

        // Assert
        assertFalse("L'acquisto con 0 monete dovrebbe fallire", result)

        val user = db.userDao().getUserById(poorUserId)!!
        assertEquals("Le monete dovrebbero rimanere 0", 0, user.coins)
    }

    @Test
    fun buyItem_with_no_logged_user_returns_false() = runBlocking {
        // Arrange
        sessionManager.clearSession()

        // Act
        val result = shopService.buyItem("hat_1")

        // Assert
        assertFalse("L'acquisto senza utente loggato dovrebbe fallire", result)
    }
}