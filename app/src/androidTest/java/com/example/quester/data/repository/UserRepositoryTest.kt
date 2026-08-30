package com.example.quester.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.quester.data.database.AppDatabase
import com.example.quester.data.model.User
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserRepositoryTest {

    private lateinit var db: AppDatabase
    private lateinit var userRepository: UserRepository
    private var testUserId: Long = 0L

    @Before
    fun setup() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        userRepository = UserRepository(db.userDao(), db.ownedCosmeticDao())

        testUserId = db.userDao().insertUser(
            User(username = "tester", passwordHash = "hash", xpTotale = 0, livello = 1, coins = 0)
        )
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun addXp_increases_xp_and_updates_level() = runBlocking {
        // Al livello 1 servono 100 XP per il livello 2
        userRepository.addXp(testUserId, 150)

        val user = userRepository.getUserById(testUserId)!!
        assertEquals(150, user.xpTotale)
        assertEquals(2, user.livello)
    }

    @Test
    fun addXp_awards_correct_coins_on_level_up() = runBlocking {
        // Livello 1 -> 2: coins + 3 (da getLevelUpCoins)
        userRepository.addXp(testUserId, 100)

        val user = userRepository.getUserById(testUserId)!!
        assertEquals(2, user.livello)
        assertEquals(3, user.coins)
    }

    @Test
    fun addXp_caps_at_MAX_TOTAL_XP() = runBlocking {
        userRepository.addXp(testUserId, 100000)

        val user = userRepository.getUserById(testUserId)!!
        assertEquals(User.MAX_TOTAL_XP, user.xpTotale)
        assertEquals(User.MAX_LEVEL, user.livello)
    }

    @Test
    fun addXp_at_cap_does_nothing() = runBlocking {
        userRepository.addXp(testUserId, User.MAX_TOTAL_XP)
        val userAfterFirst = userRepository.getUserById(testUserId)!!
        assertEquals(User.MAX_TOTAL_XP, userAfterFirst.xpTotale)

        userRepository.addXp(testUserId, 100)
        val userAfterSecond = userRepository.getUserById(testUserId)!!
        assertEquals(User.MAX_TOTAL_XP, userAfterSecond.xpTotale)
    }

    @Test
    fun getLevelUpCoins_returns_correct_values() {
        assertEquals(3, userRepository.getLevelUpCoins(5))
        assertEquals(5, userRepository.getLevelUpCoins(15))
        assertEquals(8, userRepository.getLevelUpCoins(25))
        assertEquals(12, userRepository.getLevelUpCoins(35))
        assertEquals(20, userRepository.getLevelUpCoins(45))
        assertEquals(20, userRepository.getLevelUpCoins(50))
        assertEquals(0, userRepository.getLevelUpCoins(51))
    }
}