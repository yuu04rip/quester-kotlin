package com.example.quester.data.database

import android.content.Context
import androidx.room.Room

/**
 * Singleton che crea una sola istanza del database per tutta l'app.
 */
object DatabaseProvider {

    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "quester_db"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}