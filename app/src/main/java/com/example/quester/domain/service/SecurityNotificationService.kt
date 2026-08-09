package com.example.quester.domain.service

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

class SecurityNotificationService(
    private val context: Context
) {

    companion object {
        private const val SECURITY_NOTIFICATION_TAG = "security_notification"
        private const val SECURITY_NOTIFICATION_ID_BASE = 3000
    }

    /**
     * Invia una notifica di sicurezza all'utente
     */
    suspend fun sendSecurityAlert(
        userId: Long,
        title: String,
        message: String,
        priority: NotificationPriority = NotificationPriority.HIGH
    ) {
        val inputData = workDataOf(
            "userId" to userId,
            "title" to title,
            "message" to message,
            "priority" to priority.ordinal
        )

        val request = OneTimeWorkRequestBuilder<SecurityNotificationWorker>()
            .setInputData(inputData)
            .addTag("security_$userId")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "security_notification_$userId",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    /**
     * Invia una notifica di completamento missione
     */
    suspend fun sendMissionCompletionNotification(
        userId: Long,
        missionTitle: String,
        xpGained: Int
    ) {
        sendSecurityAlert(
            userId = userId,
            title = "✦ Missione Completata ✦",
            message = "「$missionTitle」\n\n⚔ XP guadagnati: $xpGained\n★ Gloria eterna per l'eroe!",
            priority = NotificationPriority.HIGH
        )
    }

    /**
     * Invia una notifica di completamento missione con ricompense complete
     * (Versione con XP, monete e livello)
     */
    suspend fun sendMissionCompletionNotification(
        userId: Long,
        missionTitle: String,
        xpGained: Int,
        coinsGained: Int,
        playerLevel: Int
    ) {
        sendSecurityAlert(
            userId = userId,
            title = "✦ Missione Completata ✦",
            message = buildString {
                appendLine("「$missionTitle」")
                appendLine()
                appendLine("⚔ XP guadagnati: $xpGained")
                appendLine("🪙 Monete guadagnate: $coinsGained")
                appendLine("⭐ Livello raggiunto: $playerLevel")
                appendLine()
                appendLine("★ Gloria eterna per l'eroe!")
            },
            priority = NotificationPriority.HIGH
        )
    }

    /**
     * Invia un avviso di comportamento sospetto
     */
    suspend fun sendSuspiciousBehaviorAlert(
        userId: Long,
        reason: String
    ) {
        sendSecurityAlert(
            userId = userId,
            title = "⚔ Avviso dell'Ordine",
            message = "§ $reason\n\n☆ Il Consiglio degli Eroi ti invita a seguire il codice d'onore.",
            priority = NotificationPriority.HIGH
        )
    }

    /**
     * Invia notifica di account sospeso
     */
    suspend fun sendAccountSuspendedAlert(userId: Long) {
        sendSecurityAlert(
            userId = userId,
            title = "✧ Sentenza del Regno ✧",
            message = "❖ Il tuo accesso al reame è stato sospeso.\n\n⚜ Contatta il Gran Consiglio per appello.",
            priority = NotificationPriority.CRITICAL
        )
    }

    /**
     * Notifica di missione creata
     */
    suspend fun sendMissionCreatedAlert(
        userId: Long,
        missionTitle: String
    ) {
        sendSecurityAlert(
            userId = userId,
            title = "✦ Nuova Avventura ✦",
            message = "「$missionTitle」\n\n★ La tua leggenda inizia ora, eroe!",
            priority = NotificationPriority.NORMAL
        )
    }

    /**
     * Notifica di missione resettata
     */
    suspend fun sendMissionResetAlert(
        userId: Long,
        missionTitle: String
    ) {
        sendSecurityAlert(
            userId = userId,
            title = "↺ Missione Resettata",
            message = "「$missionTitle」\n\n☆ Il tempo si è fermato... Puoi ricominciare l'impresa.",
            priority = NotificationPriority.LOW
        )
    }
}

enum class NotificationPriority {
    LOW,
    NORMAL,
    HIGH,
    CRITICAL
}