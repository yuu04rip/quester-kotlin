package com.example.quester.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Singleton che crea una sola istanza del database per tutta l'app.
 */
object DatabaseProvider {

    @Volatile
    private var INSTANCE: AppDatabase? = null

    // Migration dalla versione 3 alla 4 (rimuove reputation, warnings, xpEarnedToday, lastXpResetDate)
    private val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Crea una nuova tabella senza i campi rimossi
            database.execSQL("""
                CREATE TABLE users_new (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    username TEXT NOT NULL,
                    email TEXT,
                    passwordHash TEXT NOT NULL,
                    xpTotale INTEGER NOT NULL DEFAULT 0,
                    livello INTEGER NOT NULL DEFAULT 1,
                    coins INTEGER NOT NULL DEFAULT 0,
                    profileImageUri TEXT
                )
            """)

            // Copia i dati dalla vecchia tabella
            database.execSQL("""
                INSERT INTO users_new (id, username, email, passwordHash, xpTotale, livello, coins, profileImageUri)
                SELECT id, username, email, passwordHash, xpTotale, livello, coins, profileImageUri
                FROM users
            """)

            // Sostituisci la vecchia tabella con la nuova
            database.execSQL("DROP TABLE users")
            database.execSQL("ALTER TABLE users_new RENAME TO users")
        }
    }

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "quester_db"
            )
                .addMigrations(MIGRATION_3_4)  // ← Aggiungi la migration
                .build()
            INSTANCE = instance
            instance
        }
    }
}