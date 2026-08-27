package com.example.quester.domain.service

import com.example.quester.data.model.Mission
import com.example.quester.data.model.MissionType
import com.example.quester.data.model.SubTask
import com.example.quester.data.model.VerificationLevel
import com.example.quester.data.repository.MissionRepository
import com.example.quester.data.repository.UserRepository
import com.example.quester.data.session.SessionManager
import kotlinx.coroutines.flow.first

class MissionService(
    private val missionRepository: MissionRepository,
    private val userRepository: UserRepository,
    private val currencyService: CurrencyService,
    private val sessionManager: SessionManager,
    private val securityNotificationService: SecurityNotificationService? = null,
    private val reminderService: ReminderService? = null,
    private val isTestMode: Boolean = false
) {

    companion object {
        private const val MAX_SUBTASKS_PER_MISSION = 10
        private const val MIN_TIME_BETWEEN_COMPLETIONS = 30_000L
        private const val MIN_TIME_PER_MISSION = 30_000L

        private const val ERROR_UNAUTHORIZED = "✦ Non sei autorizzato a modificare questa missione ✦"
        private const val ERROR_MISSION_ALREADY_COMPLETED = "✧ Questa missione è già stata completata ✧"
        private const val ERROR_CANNOT_MODIFY_COMPLETED = "✧ Impossibile modificare una missione già completata ✧"
        private const val ERROR_USER_NOT_FOUND = "❖ Utente non trovato nel reame"
        private const val ERROR_USER_NOT_AUTHENTICATED = "❖ Utente non autenticato"
        private const val ERROR_MISSION_NOT_FOUND = "❖ Missione non trovata nelle cronache"
    }

    // ===== CREAZIONE MISSIONE =====

    suspend fun createMissionFromForm(
        title: String,
        description: String?,
        type: String,
        dueDate: String?,
        subtasks: List<String>
    ) {
        val userId = sessionManager.loggedUserId.first()
            ?: throw IllegalStateException("Impossibile creare una missione: nessun utente autenticato.")

        require(title.isNotBlank()) { "✦ Il titolo è obbligatorio per l'impresa ✦" }

        val missionType = MissionType.fromDbValue(type)
        val validXp = missionType.xpReward

        val cleanSubtasks = subtasks.map { it.trim() }.filter { it.isNotBlank() }
        require(cleanSubtasks.size <= MAX_SUBTASKS_PER_MISSION) {
            "Massimo $MAX_SUBTASKS_PER_MISSION subtask per missione"
        }

        val verificationLevel = if (validXp > 200) VerificationLevel.MANUAL else VerificationLevel.AUTO

        val mission = Mission(
            userId = userId,
            title = title.trim(),
            description = description?.trim().orEmpty(),
            type = missionType.dbValue,
            dueDate = dueDate,
            xpReward = validXp,
            createdAt = System.currentTimeMillis(),
            verificationLevel = verificationLevel.name
        )

        // 🛠️ FIX: Catturiamo l'ID reale generato dal database
        val generatedMissionId = missionRepository.createMission(mission, cleanSubtasks)

        if (!isTestMode && reminderService != null) {
            scheduleReminderForMission(missionType, generatedMissionId, mission.title)
        }

        if (!isTestMode) {
            securityNotificationService?.sendMissionCreatedAlert(
                userId = userId,
                missionTitle = mission.title
            )
        }
    }

    // ===== COMPLETAMENTO MISSIONE =====

    private suspend fun completeMission(mission: Mission, userId: Long) {
        if (!mission.completed) {
            try {
                val user = userRepository.getUserById(userId)
                    ?: throw IllegalStateException(ERROR_USER_NOT_FOUND)

                val missionType = MissionType.fromDbValue(mission.type)

                // 🛡️ BLOCCO XP/MONETE AL RAGGIUNGIMENTO DEL LIVELLO 50
                val finalXp = if (user.livello >= 50) 0 else missionType.xpReward
                val finalCoins = if (user.livello >= 50) 0 else missionType.coinReward

                missionRepository.markMissionCompleted(mission.id)

                if (finalXp > 0) {
                    userRepository.addXp(userId, finalXp)
                }
                if (finalCoins > 0) {
                    userRepository.addCoins(userId, finalCoins)
                }

                if (!isTestMode) {
                    securityNotificationService?.sendMissionCompletionNotification(
                        userId = userId,
                        missionTitle = mission.title,
                        xpGained = finalXp,
                        coinsGained = finalCoins,
                        playerLevel = user.livello
                    )
                }

            } catch (e: IllegalStateException) {
                missionRepository.updateMission(mission.copy(completed = false))
                throw e
            }
        }
    }

    // ===== ALTRI METODI =====

    private fun scheduleReminderForMission(
        missionType: MissionType,
        missionId: Long,
        missionTitle: String
    ) {
        val delayMinutes = when (missionType) {
            MissionType.GIORNALIERO -> 12 * 60L
            MissionType.SETTIMANALE -> 3 * 24 * 60L
            MissionType.SPECIALE -> 15 * 24 * 60L
        }

        reminderService?.scheduleMissionReminder(
            missionId = missionId,
            missionTitle = missionTitle,
            delayMinutes = delayMinutes
        )
    }

    suspend fun updateMissionFromForm(
        mission: Mission,
        newTitle: String,
        newDescription: String,
        newType: String,
        newSubtasksText: List<String>
    ) {
        require(newTitle.isNotBlank()) { "✦ Il titolo è obbligatorio per l'impresa ✦" }
        check(!mission.completed) { ERROR_CANNOT_MODIFY_COMPLETED }

        val userId = sessionManager.loggedUserId.first()
        check(mission.userId == userId) { ERROR_UNAUTHORIZED }

        val cleanSubtasks = newSubtasksText
            .map { it.trim() }
            .filter { it.isNotBlank() }

        require(cleanSubtasks.size <= MAX_SUBTASKS_PER_MISSION) {
            "Massimo $MAX_SUBTASKS_PER_MISSION subtask per missione"
        }

        val missionType = MissionType.fromDbValue(newType)
        val validXp = missionType.xpReward

        val updatedMission = mission.copy(
            title = newTitle.trim(),
            description = newDescription.trim(),
            type = missionType.dbValue,
            xpReward = validXp
        )

        val subtaskList = cleanSubtasks.map { text ->
            SubTask(missionId = mission.id, text = text, done = false)
        }

        missionRepository.updateMissionWithSubTasks(updatedMission, subtaskList)
    }

    suspend fun toggleSubTask(subTask: SubTask, done: Boolean) {
        val userId = sessionManager.loggedUserId.first()
            ?: throw IllegalStateException(ERROR_USER_NOT_AUTHENTICATED)

        val mission = missionRepository.getMissionByIdOnce(subTask.missionId)
            ?: throw IllegalStateException(ERROR_MISSION_NOT_FOUND)

        check(mission.userId == userId) { ERROR_UNAUTHORIZED }
        
        // Se la missione è già completata, non permettiamo modifiche e usciamo silenziosamente
        if (mission.completed) return

        missionRepository.updateSubTask(subTask.copy(done = done))

        val allDone = missionRepository.isMissionFullyCompleted(subTask.missionId)

        if (allDone) {
            val currentMission = missionRepository.getMissionByIdOnce(subTask.missionId) ?: return

            if (!isTestMode) {
                val timeCheckResult = validateCompletionTime(currentMission, userId)
                if (timeCheckResult != null) {
                    resetMissionSubtasks(currentMission.id)
                    securityNotificationService?.sendSuspiciousBehaviorAlert(
                        userId = userId,
                        reason = timeCheckResult
                    )
                    return
                }
            }

            completeMission(currentMission, userId)
            reminderService?.cancelMissionReminder(currentMission.id)
        }
    }

    private suspend fun validateCompletionTime(
        mission: Mission,
        userId: Long
    ): String? {
        val now = System.currentTimeMillis()
        val timeSpent = now - mission.createdAt

        if (timeSpent < MIN_TIME_PER_MISSION) {
            return "Completata troppo velocemente (${timeSpent}ms)"
        }

        val lastCompletion = missionRepository.getLastCompletionTime(userId)
        if (lastCompletion != null && now - lastCompletion < MIN_TIME_BETWEEN_COMPLETIONS) {
            return "Troppi completamenti in breve tempo"
        }

        return null
    }

    suspend fun deleteMission(mission: Mission) {
        val userId = sessionManager.loggedUserId.first()
        check(mission.userId == userId) { "✦ Non sei autorizzato a eliminare questa missione ✦" }

        reminderService?.cancelMissionReminder(mission.id)
        resetMissionSubtasks(mission.id)
        missionRepository.deleteMission(mission)
    }

    suspend fun restoreMission(mission: Mission, subTasks: List<SubTask>) {
        missionRepository.restoreMission(mission, subTasks)
    }

    private suspend fun resetMissionSubtasks(missionId: Long) {
        val allSubtasks = missionRepository.getSubTasksByMissionId(missionId).first()
        allSubtasks.forEach { subtask ->
            if (subtask.done) {
                missionRepository.updateSubTask(subtask.copy(done = false))
            }
        }
    }

    suspend fun resetMission(missionId: Long) {
        val userId = sessionManager.loggedUserId.first()
            ?: throw IllegalStateException(ERROR_USER_NOT_AUTHENTICATED)

        val mission = missionRepository.getMissionByIdOnce(missionId)
            ?: throw IllegalStateException(ERROR_MISSION_NOT_FOUND)

        check(mission.userId == userId) { ERROR_UNAUTHORIZED }

        resetMissionSubtasks(missionId)

        if (mission.completed && !mission.xpAwarded) {
            missionRepository.updateMission(mission.copy(completed = false))
        }

        securityNotificationService?.sendMissionResetAlert(
            userId = userId,
            missionTitle = mission.title
        )
    }
}