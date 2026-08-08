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
        // Usa WorkManager per inviare la notifica in background
        val inputData = workDataOf(
            "userId" to userId,
            "title" to title,
            "message" to message,
            "priority" to priority.ordinal  // Converti enum in Int
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
            title = "Missione completata! 🎉",
            message = "Hai completato \"$missionTitle\" e guadagnato $xpGained XP!",
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
            title = "⚠️ Attenzione!",
            message = "Comportamento sospetto rilevato: $reason. Per favore, utilizza l'app correttamente.",
            priority = NotificationPriority.HIGH
        )
    }

    /**
     * Invia notifica di account sospeso
     */
    suspend fun sendAccountSuspendedAlert(userId: Long) {
        sendSecurityAlert(
            userId = userId,
            title = "🚫 Account sospeso",
            message = "Il tuo account è stato sospeso per comportamento sospetto. Contatta il supporto.",
            priority = NotificationPriority.CRITICAL
        )
    }
}

enum class NotificationPriority {
    LOW,
    NORMAL,
    HIGH,
    CRITICAL
}