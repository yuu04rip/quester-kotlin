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
        private const val MAX_DAILY_MISSIONS = 5
        private const val MAX_SUBTASKS_PER_MISSION = 10
        private const val MAX_DAILY_XP = 500
        private const val MIN_TIME_BETWEEN_COMPLETIONS = 30_000L
        private const val MIN_TIME_PER_MISSION = 30_000L
        private const val REPUTATION_PENALTY = 10
        private const val MAX_WARNINGS = 3

        // Costanti per messaggi di errore
        private const val ERROR_UNAUTHORIZED = "✦ Non sei autorizzato a modificare questa missione ✦"
        private const val ERROR_MISSION_ALREADY_COMPLETED = "✧ Questa missione è già stata completata ✧"
        private const val ERROR_CANNOT_MODIFY_COMPLETED = "✧ Impossibile modificare una missione già completata ✧"
        private const val ERROR_USER_NOT_FOUND = "❖ Utente non trovato nel reame"
        private const val ERROR_USER_NOT_AUTHENTICATED = "❖ Utente non autenticato"
        private const val ERROR_MISSION_NOT_FOUND = "❖ Missione non trovata nelle cronache"
    }

    private fun validateAndNormalizeXp(xpReward: Int, missionType: MissionType): Int {
        return if (xpReward in missionType.minXp..missionType.maxXp) {
            xpReward
        } else {
            missionType.defaultXp
        }
    }

    suspend fun createMissionFromForm(
        title: String,
        description: String?,
        type: String,
        dueDate: String?,
        xpReward: Int,
        subtasks: List<String>
    ) {
        val userId = sessionManager.loggedUserId.first()
            ?: throw IllegalStateException("Impossibile creare una missione: nessun utente autenticato.")

        require(title.isNotBlank()) { "✦ Il titolo è obbligatorio per l'impresa ✦" }

        val missionType = MissionType.fromDbValue(type)
        val validXp = validateAndNormalizeXp(xpReward, missionType)

        if (!isTestMode) {
            validateUserCanCreateMission(userId, validXp)
        }

        // 🔧 Validazione subtask - RIMOSSO il controllo sul minimo
        // Ora le missioni possono essere create anche senza task
        val cleanSubtasks = subtasks.map { it.trim() }.filter { it.isNotBlank() }
        require(cleanSubtasks.size <= MAX_SUBTASKS_PER_MISSION) {
            "Massimo $MAX_SUBTASKS_PER_MISSION subtask per missione"
        }
        // ✅ NON c'è più il controllo cleanSubtasks.size < MIN_SUBTASKS_PER_MISSION

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

        missionRepository.createMission(mission, cleanSubtasks)

        if (!isTestMode && reminderService != null) {
            scheduleReminderForMission(missionType, mission.id, mission.title)
        }

        if (!isTestMode) {
            securityNotificationService?.sendMissionCreatedAlert(
                userId = userId,
                missionTitle = mission.title
            )
        }
    }

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

    private suspend fun validateUserCanCreateMission(userId: Long, xpReward: Int) {
        val user = userRepository.getUserById(userId) ?: throw IllegalStateException(ERROR_USER_NOT_FOUND)

        check(user.reputation >= 50) { "✦ La tua reputazione è troppo bassa per creare nuove missioni ✦" }

        if (user.warnings >= MAX_WARNINGS) {
            securityNotificationService?.sendAccountSuspendedAlert(userId)
            throw IllegalStateException("✦ Il tuo account è stato sospeso. Contatta il Gran Consiglio. ✦")
        }

        val todayMissions = missionRepository.countMissionsCreatedToday(userId)
        check(todayMissions < MAX_DAILY_MISSIONS) {
            "✦ Hai raggiunto il limite di $MAX_DAILY_MISSIONS missioni giornaliere ✦"
        }

        val todayXp = userRepository.getXpEarnedToday(userId)
        check(todayXp + xpReward <= MAX_DAILY_XP) {
            "✦ Hai raggiunto il limite di $MAX_DAILY_XP XP giornalieri ✦"
        }
    }

    suspend fun updateMissionFromForm(
        mission: Mission,
        newTitle: String,
        newDescription: String,
        newType: String,
        newXpReward: Int,
        newSubtasksText: List<String>
    ) {
        require(newTitle.isNotBlank()) { "✦ Il titolo è obbligatorio per l'impresa ✦" }
        check(!mission.completed) { ERROR_CANNOT_MODIFY_COMPLETED }

        val userId = sessionManager.loggedUserId.first()
        check(mission.userId == userId) { ERROR_UNAUTHORIZED }

        val cleanSubtasks = newSubtasksText
            .map { it.trim() }
            .filter { it.isNotBlank() }

        // 🔧 RIMOSSO anche qui il controllo sul minimo
        require(cleanSubtasks.size <= MAX_SUBTASKS_PER_MISSION) {
            "Massimo $MAX_SUBTASKS_PER_MISSION subtask per missione"
        }

        val missionType = MissionType.fromDbValue(newType)
        val validXp = validateAndNormalizeXp(newXpReward, missionType)

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
        check(!mission.completed) { ERROR_MISSION_ALREADY_COMPLETED }

        if (!isTestMode) {
            val user = userRepository.getUserById(userId) ?: throw IllegalStateException(ERROR_USER_NOT_FOUND)
            if (user.warnings >= MAX_WARNINGS) {
                securityNotificationService?.sendAccountSuspendedAlert(userId)
                throw IllegalStateException("✦ Il tuo account è stato sospeso per comportamento sospetto ✦")
            }
        }

        missionRepository.updateSubTask(subTask.copy(done = done))

        val allDone = missionRepository.isMissionFullyCompleted(subTask.missionId)

        if (allDone) {
            val currentMission = missionRepository.getMissionByIdOnce(subTask.missionId) ?: return

            if (!isTestMode) {
                val timeCheckResult = validateCompletionTime(currentMission, userId)
                if (timeCheckResult != null) {
                    resetMissionSubtasks(currentMission.id)
                    handleSuspiciousBehavior(userId, currentMission.id, timeCheckResult)
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
            securityNotificationService?.sendSuspiciousBehaviorAlert(
                userId = userId,
                reason = "Completata troppo velocemente (${timeSpent}ms)"
            )
            return "Completata troppo velocemente (${timeSpent}ms)"
        }

        val lastCompletion = missionRepository.getLastCompletionTime(userId)
        if (lastCompletion != null && now - lastCompletion < MIN_TIME_BETWEEN_COMPLETIONS) {
            securityNotificationService?.sendSuspiciousBehaviorAlert(
                userId = userId,
                reason = "Troppi completamenti in breve tempo"
            )
            return "Troppi completamenti in breve tempo"
        }

        return null
    }

    private suspend fun completeMission(mission: Mission, userId: Long) {
        if (!mission.completed) {
            try {
                missionRepository.markMissionCompleted(mission.id)
                userRepository.addXp(userId, mission.xpReward)
                currencyService.onMissionRedeemed()

                if (!isTestMode) {
                    securityNotificationService?.sendMissionCompletionNotification(
                        userId = userId,
                        missionTitle = mission.title,
                        xpGained = mission.xpReward
                    )
                    userRepository.increaseReputation(userId, 1)
                }
            } catch (e: IllegalStateException) {
                missionRepository.updateMission(mission.copy(completed = false))
                throw e
            }
        }
    }

    suspend fun deleteMission(mission: Mission) {
        val userId = sessionManager.loggedUserId.first()
        check(mission.userId == userId) { "✦ Non sei autorizzato a eliminare questa missione ✦" }

        if (!isTestMode) {
            val user = userRepository.getUserById(userId) ?: return
            if (user.warnings >= MAX_WARNINGS) {
                securityNotificationService?.sendAccountSuspendedAlert(userId)
                throw IllegalStateException("✦ Il tuo account è stato sospeso per comportamento sospetto ✦")
            }
        }

        reminderService?.cancelMissionReminder(mission.id)
        resetMissionSubtasks(mission.id)
        missionRepository.deleteMission(mission)
    }

    suspend fun restoreMission(mission: Mission, subTasks: List<SubTask>) {
        missionRepository.restoreMission(mission, subTasks)
    }

    suspend fun redeemCompletedMission(missionId: Long) {
        // Implementazione se necessaria
    }

    // --- METODI PRIVATI ---

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

    private suspend fun handleSuspiciousBehavior(userId: Long, missionId: Long, reason: String) {
        println("⚔ SUSPICIOUS: User $userId, Mission $missionId, Reason: $reason")
        userRepository.decreaseReputation(userId, REPUTATION_PENALTY)
        userRepository.addWarning(userId)

        val user = userRepository.getUserById(userId)
        if (user?.warnings ?: 0 >= MAX_WARNINGS) {
            securityNotificationService?.sendAccountSuspendedAlert(userId)
        }
    }
}