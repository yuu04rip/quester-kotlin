package com.example.quester.domain.service

import com.example.quester.data.model.Mission
import com.example.quester.data.model.SubTask
import com.example.quester.data.repository.MissionRepository
import com.example.quester.data.repository.UserRepository

class MissionService(
    private val missionRepository: MissionRepository,
    private val userRepository: UserRepository,
    private val currencyService: CurrencyService
) {

    suspend fun createMissionFromForm(
        title: String,
        description: String?,
        type: String,
        dueDate: String?,
        xpReward: Int,
        subtasks: List<String>
    ) {
        require(title.isNotBlank()) { "Titolo obbligatorio" }
        require(xpReward >= 0) { "XP non valida" }

        val mission = Mission(
            title = title.trim(),
            description = description?.trim().orEmpty(),
            type = type,
            dueDate = dueDate,
            xpReward = xpReward
        )

        val cleanSubtasks = subtasks.map { it.trim() }.filter { it.isNotBlank() }
        missionRepository.createMission(mission, cleanSubtasks)
    }

    suspend fun toggleSubTask(subTask: SubTask, done: Boolean) {
        missionRepository.updateSubTask(subTask.copy(done = done))

        val missionId = subTask.missionId
        val allDone = missionRepository.isMissionFullyCompleted(missionId)

        if (allDone) {
            val mission = missionRepository.getMissionByIdOnce(missionId) ?: return
            if (!mission.completed) {
                missionRepository.markMissionCompleted(missionId)
                userRepository.addXp(mission.xpReward)
            }
        }
    }

    suspend fun redeemCompletedMission(missionId: Long) {
        // da implementare se serve riscatto separato con reward extra
    }
}