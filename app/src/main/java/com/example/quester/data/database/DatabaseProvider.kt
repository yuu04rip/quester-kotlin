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

    // ============================================================
    // MIGRATION 3 -> 4
    // Rimuove reputation, warnings, xpEarnedToday, lastXpResetDate
    // ============================================================

    private val MIGRATION_3_4 = object : Migration(3, 4) {

        override fun migrate(database: SupportSQLiteDatabase) {

            // Crea una nuova tabella users senza i campi rimossi
            database.execSQL(
                """
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
                """.trimIndent()
            )

            // Copia i dati dalla vecchia tabella
            database.execSQL(
                """
                INSERT INTO users_new (
                    id,
                    username,
                    email,
                    passwordHash,
                    xpTotale,
                    livello,
                    coins,
                    profileImageUri
                )
                SELECT
                    id,
                    username,
                    email,
                    passwordHash,
                    xpTotale,
                    livello,
                    coins,
                    profileImageUri
                FROM users
                """.trimIndent()
            )

            // Sostituisce la vecchia tabella
            database.execSQL("DROP TABLE users")
            database.execSQL("ALTER TABLE users_new RENAME TO users")
        }
    }

    // ============================================================
    // MIGRATION 4 -> 5
    // Aggiunge iconName alla tabella shop_items
    // ============================================================

    private val MIGRATION_4_5 = object : Migration(4, 5) {

        override fun migrate(database: SupportSQLiteDatabase) {

            database.execSQL(
                """
                ALTER TABLE shop_items
                ADD COLUMN iconName TEXT NOT NULL DEFAULT 'shopping_cart'
                """.trimIndent()
            )
        }
    }

    private val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                """
            ALTER TABLE users
            ADD COLUMN equippedHat TEXT NOT NULL DEFAULT 'NONE'
            """.trimIndent()
            )

            database.execSQL(
                """
            ALTER TABLE users
            ADD COLUMN equippedWeapon TEXT NOT NULL DEFAULT 'NONE'
            """.trimIndent()
            )

            database.execSQL(
                """
            ALTER TABLE users
            ADD COLUMN equippedFrame TEXT NOT NULL DEFAULT 'NONE'
            """.trimIndent()
            )
        }
    }

    // ============================================================
    // DATABASE
    // ============================================================

    fun getDatabase(context: Context): AppDatabase {

        return INSTANCE ?: synchronized(this) {

            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "quester_db"
            )
                // Migration 3 -> 4
                .addMigrations(MIGRATION_3_4)

                // Migration 4 -> 5
                .addMigrations(MIGRATION_4_5)
                // Migration 5 -> 6
                .addMigrations(MIGRATION_5_6)

                .build()

            INSTANCE = instance

            instance
        }
    }
}