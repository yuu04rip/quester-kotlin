package com.example.quester.domain.service

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

class ReminderService(private val context: Context) {

    fun scheduleMissionReminder(missionId: Long, missionTitle: String, delayMinutes: Long) {
        // Se delay è 0 o negativo, non programmare
        if (delayMinutes <= 0) return

        val inputData = workDataOf(
            "missionId" to missionId,
            "missionTitle" to missionTitle
        )

        val req = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInputData(inputData)
            .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
            .addTag("mission_$missionId")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "mission_reminder_$missionId",
            ExistingWorkPolicy.REPLACE,
            req
        )
    }

    fun cancelMissionReminder(missionId: Long) {
        WorkManager.getInstance(context).cancelAllWorkByTag("mission_$missionId")
    }
}