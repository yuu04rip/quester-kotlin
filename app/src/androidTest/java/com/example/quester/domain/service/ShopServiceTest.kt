package com.example.quester.domain.service

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.quester.data.database.AppDatabase
import com.example.quester.data.model.ShopItem
import com.example.quester.data.model.User
import com.example.quester.data.repository.UserRepository
import com.example.quester.data.session.SessionManager
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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

        db.shopDao().upsertItems(
            listOf(
                ShopItem(id = "hat_1", name = "Cool Hat", price = 120),
                ShopItem(id = "skin_1", name = "Blue Skin", price = 300)
            )
        )
    }

    @After
    fun tearDown() = runBlocking {
        sessionManager.clearSession()
        db.close()
    }

    @Test
    fun purchase_success_scales_coins_and_owns_item() = runBlocking {
        val result = shopService.purchase("hat_1")
        assertTrue(result is PurchaseResult.Success)

        val user = db.userDao().getUserById(testUserId)!!
        assertEquals(80, user.coins)

        val owned = db.ownedCosmeticDao().isOwned(user.id, "hat_1")
        assertTrue(owned)
    }

    @Test
    fun purchase_insufficient_funds() = runBlocking {
        val result = shopService.purchase("skin_1")
        assertTrue(result is PurchaseResult.InsufficientFunds)

        val user = db.userDao().getUserById(testUserId)!!
        assertEquals(200, user.coins)
    }

    @Test
    fun purchase_item_not_found() = runBlocking {
        val result = shopService.purchase("not_exists")
        assertTrue(result is PurchaseResult.ItemNotFound)
    }

    @Test
    fun purchase_already_owned() = runBlocking {
        val first = shopService.purchase("hat_1")
        assertTrue(first is PurchaseResult.Success)

        val second = shopService.purchase("hat_1")
        assertTrue(second is PurchaseResult.AlreadyOwned)

        val user = db.userDao().getUserById(testUserId)!!
        assertEquals(80, user.coins) // Non scala una seconda volta
    }
}