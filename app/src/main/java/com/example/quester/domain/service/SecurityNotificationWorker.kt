package com.example.quester.domain.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class SecurityNotificationWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    companion object {
        const val SECURITY_CHANNEL_ID = "quester_security_channel"
        const val SECURITY_CHANNEL_NAME = "Quester Sicurezza"
        const val NOTIFICATION_ID_BASE = 3000
    }

    override suspend fun doWork(): Result {
        val userId = inputData.getLong("userId", 0)
        val title = inputData.getString("title") ?: "Notifica"
        val message = inputData.getString("message") ?: ""
        val priority = inputData.getInt("priority", 2) // Default HIGH

        showSecurityNotification(userId, title, message, priority)
        return Result.success()
    }

    private fun showSecurityNotification(
        userId: Long,
        title: String,
        message: String,
        priority: Int
    ) {
        createSecurityNotificationChannel()

        // Verifica permessi su Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        // Determina l'importanza della notifica
        val importance = when (priority) {
            0 -> NotificationCompat.PRIORITY_LOW
            1 -> NotificationCompat.PRIORITY_DEFAULT
            2 -> NotificationCompat.PRIORITY_HIGH
            3 -> NotificationCompat.PRIORITY_MAX
            else -> NotificationCompat.PRIORITY_DEFAULT
        }

        // Icona basata sul tipo di notifica
        val icon = when {
            title.contains("completata") -> android.R.drawable.ic_menu_save
            title.contains("Attenzione") -> android.R.drawable.ic_dialog_alert
            title.contains("sospeso") -> android.R.drawable.ic_dialog_alert
            else -> android.R.drawable.ic_dialog_info
        }

        val notification = NotificationCompat.Builder(applicationContext, SECURITY_CHANNEL_ID)
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(importance)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 200, 500)) // Vibrazione per notifiche importanti
            .build()

        NotificationManagerCompat.from(applicationContext)
            .notify((NOTIFICATION_ID_BASE + userId).toInt(), notification)
    }

    private fun createSecurityNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                SECURITY_CHANNEL_ID,
                SECURITY_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifiche di sicurezza e avvisi di Quester"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
            }
            val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}