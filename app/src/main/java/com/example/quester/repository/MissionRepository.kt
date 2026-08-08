package com.example.quester.data.repository

import com.example.quester.data.dao.MissionDao
import com.example.quester.data.dao.SubTaskDao
import com.example.quester.data.model.Mission
import com.example.quester.data.model.MissionWithSubTasks
import com.example.quester.data.model.SubTask
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class MissionRepository(
    private val missionDao: MissionDao,
    private val subTaskDao: SubTaskDao
) {
    fun getAllMissions(): Flow<List<Mission>> = missionDao.getAllMissions()

    fun getAllMissionsWithSubTasks(): Flow<List<MissionWithSubTasks>> = missionDao.getAllMissionsWithSubTasks()

    fun getMissionById(missionId: Long): Flow<Mission?> = missionDao.getMissionById(missionId)

    fun getSubTasksByMissionId(missionId: Long): Flow<List<SubTask>> =
        subTaskDao.getSubTasksByMissionId(missionId)

    suspend fun createMission(mission: Mission, subTasks: List<String>) {
        val missionId = missionDao.insertMission(mission)
        if (subTasks.isNotEmpty()) {
            val items = subTasks.map { text -> SubTask(missionId = missionId, text = text) }
            subTaskDao.insertSubTasks(items)
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

    // --- AGGIUNTE ---

    suspend fun getMissionByIdOnce(missionId: Long): Mission? {
        return missionDao.getMissionById(missionId).first()
    }

    suspend fun markMissionCompleted(missionId: Long) {
        val mission = getMissionByIdOnce(missionId) ?: return
        if (!mission.completed) {
            missionDao.updateMission(mission.copy(completed = true))
        }
    }
    suspend fun markMissionXpAwarded(missionId: Long) {
        val m = missionDao.getMissionByIdOnce(missionId) ?: return
        if (!m.xpAwarded) missionDao.updateMission(m.copy(xpAwarded = true))
    }

    suspend fun redeemMission(missionId: Long) {
        val m = missionDao.getMissionByIdOnce(missionId) ?: return
        if (!m.redeemed) missionDao.updateMission(m.copy(redeemed = true))
    }
}

