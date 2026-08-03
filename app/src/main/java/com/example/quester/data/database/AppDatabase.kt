package com.example.quester.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.quester.data.dao.MissionDao
import com.example.quester.data.dao.SubTaskDao
import com.example.quester.data.dao.UserDao
import com.example.quester.data.model.Mission
import com.example.quester.data.model.SubTask
import com.example.quester.data.model.User

/**
 * Database principale dell'app (Room).
 * Contiene tutte le tabelle e fornisce accesso ai DAO.
 */
@Database(
    entities = [User::class, Mission::class, SubTask::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun missionDao(): MissionDao
    abstract fun subTaskDao(): SubTaskDao
}