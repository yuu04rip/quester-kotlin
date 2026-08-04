package com.example.quester.domain.service

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.quester.data.database.AppDatabase
import com.example.quester.data.model.User
import com.example.quester.data.repository.UserRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CurrencyServiceTest {

    private lateinit var db: AppDatabase
    private lateinit var userRepository: UserRepository
    private lateinit var currencyService: CurrencyService

    @Before
    fun setup() {
        runBlocking {
            val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
            db = Room.inMemoryDatabaseBuilder(ctx, AppDatabase::class.java)
                .allowMainThreadQueries()
                .build()

            userRepository = UserRepository(db.userDao())
            currencyService = CurrencyService(userRepository)

            db.userDao().insertUser(
                User(username = "tester", passwordHash = "hash", xpTotale = 0, livello = 1, coins = 0)
            )
        }
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun onLevelUp_assigns_expected_coins() = runBlocking {
        currencyService.onLevelUp(beforeLevel = 1, afterLevel = 3) // +2 livelli => 100 coins
        val user = db.userDao().getUser()!!
        assertEquals(100, user.coins)
    }

    @Test
    fun onLevelUp_no_gain_assigns_zero() = runBlocking {
        currencyService.onLevelUp(beforeLevel = 2, afterLevel = 2)
        val user = db.userDao().getUser()!!
        assertEquals(0, user.coins)
    }

    @Test
    fun onSpecialEvent_adds_coins() = runBlocking {
        currencyService.onSpecialEvent(35)
        val user = db.userDao().getUser()!!
        assertEquals(35, user.coins)
    }

    @Test
    fun onMissionRedeemed_adds_fixed_reward() = runBlocking {
        currencyService.onMissionRedeemed()
        val user = db.userDao().getUser()!!
        assertEquals(20, user.coins)
    }
}