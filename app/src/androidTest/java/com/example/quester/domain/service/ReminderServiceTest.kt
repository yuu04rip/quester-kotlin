package com.example.quester.domain.service

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import androidx.work.testing.WorkManagerTestInitHelper
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReminderServiceTest {

    private lateinit var reminderService: ReminderService

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        WorkManagerTestInitHelper.initializeTestWorkManager(context)
        reminderService = ReminderService(context)
    }

    @Test
    fun schedule_and_cancel_do_not_crash() {
        reminderService.scheduleMissionReminder(
            missionId = 10L,
            missionTitle = "Test Mission",
            delayMinutes = 15L
        )
        reminderService.cancelMissionReminder(missionId = 10L)
        assertNotNull(reminderService)
    }
}