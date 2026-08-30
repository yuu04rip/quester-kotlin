package com.example.quester.data.repository

import com.example.quester.data.dao.MissionDao
import com.example.quester.data.dao.SubTaskDao
import com.example.quester.data.model.Mission
import com.example.quester.data.model.MissionWithSubTasks
import com.example.quester.data.model.SubTask
import kotlinx.coroutines.flow.Flow

class MissionRepository(
    private val missionDao: MissionDao,
    private val subTaskDao: SubTaskDao
) {
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

    suspend fun deleteMission(mission: Mission) {
        missionDao.deleteMission(mission)
    }

    suspend fun restoreMission(mission: Mission, subTasks: List<SubTask>) {
        missionDao.insertMission(mission)
        if (subTasks.isNotEmpty()) {
            subTaskDao.insertSubTasks(subTasks)
        }
    }

    suspend fun getLastCompletionTime(userId: Long): Long? {
        return missionDao.getLastCompletionTime(userId)
    }
}
