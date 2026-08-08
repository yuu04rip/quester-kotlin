package com.example.quester.data.repository

import com.example.quester.data.dao.MissionDao
import com.example.quester.data.dao.SubTaskDao
import com.example.quester.data.model.Mission
import com.example.quester.data.model.MissionWithSubTasks
import com.example.quester.data.model.SubTask
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class MissionRepository(
    private val missionDao: MissionDao,
    private val subTaskDao: SubTaskDao
) {
    // --- Query filtrate per Utente Loggato ---
    fun getAllMissionsForUser(userId: Long): Flow<List<Mission>> =
        missionDao.getAllMissionsForUser(userId)

    fun getAllMissionsWithSubTasksForUser(userId: Long): Flow<List<MissionWithSubTasks>> =
        missionDao.getAllMissionsWithSubTasksForUser(userId)

    fun getMissionById(missionId: Long): Flow<Mission?> = missionDao.getMissionById(missionId)

    fun getSubTasksByMissionId(missionId: Long): Flow<List<SubTask>> =
        subTaskDao.getSubTasksByMissionId(missionId)

    suspend fun createMission(mission: Mission, subTasks: List<String>): Long {
        val missionId = missionDao.insertMission(mission)
        if (subTasks.isNotEmpty()) {
            val items = subTasks.map { text -> SubTask(missionId = missionId, text = text) }
            subTaskDao.insertSubTasks(items)
        }
        return missionId
    }

    suspend fun updateMission(mission: Mission) {
        missionDao.updateMission(mission)
    }

    suspend fun updateMissionWithSubTasks(mission: Mission, subtasks: List<SubTask>) {
        missionDao.updateMission(mission)
        subTaskDao.deleteSubTasksForMission(mission.id)
        if (subtasks.isNotEmpty()) {
            subTaskDao.insertSubTasks(subtasks.map { it.copy(id = 0, missionId = mission.id) })
        }
    }

    suspend fun updateSubTask(subTask: SubTask) {
        subTaskDao.updateSubTask(subTask)
    }

    suspend fun setMissionCompleted(mission: Mission, completed: Boolean) {
        missionDao.updateMission(mission.copy(completed = completed))
    }

    suspend fun isMissionFullyCompleted(missionId: Long): Boolean {
        val done = subTaskDao.countCompletedSubTasks(missionId)
        val total = subTaskDao.countAllSubTasks(missionId)
        return total > 0 && done == total
    }

    suspend fun getMissionByIdOnce(missionId: Long): Mission? {
        return missionDao.getMissionByIdOnce(missionId)
    }

    suspend fun markMissionCompleted(missionId: Long) {
        val mission = getMissionByIdOnce(missionId) ?: return
        if (!mission.completed) {
            missionDao.updateMission(
                mission.copy(
                    completed = true,
                    completedAt = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun markMissionXpAwarded(missionId: Long) {
        val m = getMissionByIdOnce(missionId) ?: return
        if (!m.xpAwarded) missionDao.updateMission(m.copy(xpAwarded = true))
    }

    suspend fun redeemMission(missionId: Long) {
        val m = getMissionByIdOnce(missionId) ?: return
        if (!m.redeemed) missionDao.updateMission(m.copy(redeemed = true))
    }

    // --- ELIMINAZIONE E RIPRISTINO ---
    suspend fun deleteMission(mission: Mission) {
        missionDao.deleteMission(mission)
    }

    suspend fun deleteMissionById(missionId: Long) {
        missionDao.deleteMissionById(missionId)
    }

    suspend fun restoreMission(mission: Mission, subTasks: List<SubTask>) {
        missionDao.insertMission(mission)
        if (subTasks.isNotEmpty()) {
            subTaskDao.insertSubTasks(subTasks)
        }
    }

    // --- NUOVI METODI PER SICUREZZA ---

    suspend fun countMissionsCreatedToday(userId: Long): Int {
        val startOfDay = getStartOfDay()
        return missionDao.countMissionsCreatedToday(userId, startOfDay)
    }

    suspend fun getLastCompletionTime(userId: Long): Long? {
        return missionDao.getLastCompletionTime(userId)
    }

    suspend fun countRecentCompletions(userId: Long, timeThreshold: Long): Int {
        return missionDao.countRecentCompletions(userId, timeThreshold)
    }

    suspend fun getCompletionsInTimeRange(userId: Long, startTime: Long, endTime: Long): List<Mission> {
        return missionDao.getCompletionsInTimeRange(userId, startTime, endTime)
    }

    private fun getStartOfDay(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}