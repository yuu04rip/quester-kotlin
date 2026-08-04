package com.example.quester.domain.service

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.quester.data.database.AppDatabase
import com.example.quester.data.model.User
import com.example.quester.data.repository.MissionRepository
import com.example.quester.data.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MissionServiceTest {

    private lateinit var db: AppDatabase
    private lateinit var missionRepository: MissionRepository
    private lateinit var userRepository: UserRepository
    private lateinit var currencyService: CurrencyService
    private lateinit var missionService: MissionService

    @Before
    fun setup() {
        runBlocking {
            val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
            db = Room.inMemoryDatabaseBuilder(ctx, AppDatabase::class.java)
                .allowMainThreadQueries()
                .build()

            missionRepository = MissionRepository(db.missionDao(), db.subTaskDao())
            userRepository = UserRepository(db.userDao())
            currencyService = CurrencyService(userRepository)
            missionService = MissionService(missionRepository, userRepository, currencyService)

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
    fun toggle_all_subtasks_completes_mission_and_awards_xp_once() = runBlocking {
        missionService.createMissionFromForm(
            title = "Study",
            description = "Do chapters",
            type = "DAILY",
            dueDate = null,
            xpReward = 120,
            subtasks = listOf("A", "B")
        )

        val mission = db.missionDao().getAllMissions().first().first()
        val subtasks = db.subTaskDao().getSubTasksByMissionId(mission.id).first()
        assertEquals(2, subtasks.size)

        missionService.toggleSubTask(subtasks[0], true)
        val midUser = db.userDao().getUser()!!
        assertEquals(0, midUser.xpTotale)

        missionService.toggleSubTask(subtasks[1], true)

        val updatedMission = db.missionDao().getMissionByIdOnce(mission.id)!!
        assertTrue(updatedMission.completed)

        val userAfter = db.userDao().getUser()!!
        assertEquals(120, userAfter.xpTotale)

        // ritoggle stesso subtask -> no doppio XP (mission già completed)
        missionService.toggleSubTask(subtasks[1].copy(done = true), true)
        val userAfterSecond = db.userDao().getUser()!!
        assertEquals(120, userAfterSecond.xpTotale)
    }
}