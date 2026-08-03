package com.example.quester.data.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.quester.data.model.Mission
import com.example.quester.data.model.SubTask
import com.example.quester.data.model.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {

    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun user_insert_and_read() = runBlocking {
        val userId = db.userDao().insertUser(User(username = "tester"))
        val user = db.userDao().getUser()

        assertTrue(userId > 0)
        assertNotNull(user)
        assertEquals("tester", user?.username)
    }

    @Test
    fun mission_with_subtasks_flow() = runBlocking {
        val missionId = db.missionDao().insertMission(
            Mission(title = "M1", type = "DAILY")
        )

        db.subTaskDao().insertSubTasks(
            listOf(
                SubTask(missionId = missionId, text = "T1"),
                SubTask(missionId = missionId, text = "T2")
            )
        )

        val missions = db.missionDao().getAllMissions().first()
        val subtasks = db.subTaskDao().getSubTasksByMissionId(missionId).first()

        assertEquals(1, missions.size)
        assertEquals(2, subtasks.size)

        db.subTaskDao().updateSubTask(subtasks.first().copy(done = true))
        val done = db.subTaskDao().countCompletedSubTasks(missionId)
        val total = db.subTaskDao().countAllSubTasks(missionId)

        assertEquals(1, done)
        assertEquals(2, total)
    }

    @Test
    fun delete_mission_cascades_subtasks() = runBlocking {
        val missionId = db.missionDao().insertMission(
            Mission(title = "Missione con cascade", type = "DAILY")
        )

        db.subTaskDao().insertSubTasks(
            listOf(
                SubTask(missionId = missionId, text = "S1"),
                SubTask(missionId = missionId, text = "S2")
            )
        )

        val beforeDelete = db.subTaskDao().getSubTasksByMissionId(missionId).first()
        assertEquals(2, beforeDelete.size)

        val mission = db.missionDao().getMissionById(missionId).first()
        requireNotNull(mission)
        db.missionDao().deleteMission(mission)

        val afterDelete = db.subTaskDao().getSubTasksByMissionId(missionId).first()
        assertTrue(afterDelete.isEmpty())
    }
}