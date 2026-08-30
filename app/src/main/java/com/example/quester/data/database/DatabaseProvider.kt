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
    // Rimuove reputation, warnings, xpEarnedToday, lastXpResetDate e profileImageUri
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
                    coins INTEGER NOT NULL DEFAULT 0
                )
                """.trimIndent()
            )

            database.execSQL(
                """
                INSERT INTO users_new (
                    id,
                    username,
                    email,
                    passwordHash,
                    xpTotale,
                    livello,
                    coins
                )
                SELECT
                    id,
                    username,
                    email,
                    passwordHash,
                    xpTotale,
                    livello,
                    coins
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
    // MIGRATION 6 -> 7
    // Aggiunge iconScale alla tabella shop_items
    // ============================================================

    private val MIGRATION_6_7 = object : Migration(6, 7) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                """
                ALTER TABLE shop_items
                ADD COLUMN iconScale REAL NOT NULL DEFAULT 1.0
                """.trimIndent()
            )
        }
    }

    // ============================================================
    // MIGRATION 7 -> 8
    // Rimuove definitivamente profileImageUri dalla tabella users
    // ============================================================

    private val MIGRATION_7_8 = object : Migration(7, 8) {
        override fun migrate(database: SupportSQLiteDatabase) {
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
                    equippedHat TEXT NOT NULL DEFAULT 'NONE',
                    equippedWeapon TEXT NOT NULL DEFAULT 'NONE',
                    equippedFrame TEXT NOT NULL DEFAULT 'NONE'
                )
                """.trimIndent()
            )

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
                    equippedHat,
                    equippedWeapon,
                    equippedFrame
                )
                SELECT
                    id,
                    username,
                    email,
                    passwordHash,
                    xpTotale,
                    livello,
                    coins,
                    equippedHat,
                    equippedWeapon,
                    equippedFrame
                FROM users
                """.trimIndent()
            )

            database.execSQL("DROP TABLE users")
            database.execSQL("ALTER TABLE users_new RENAME TO users")
        }
    }

    // ============================================================
    // MIGRATION 8 -> 9
    // Aggiunge supporto per la versione 9 del database (aggiornamento User entity)
    // ============================================================

    private val MIGRATION_8_9 = object : Migration(8, 9) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Se in futuro aggiungi colonne o modifichi la struttura senza voler perdere i dati,
            // puoi gestirlo qui. Per adesso esegue una migrazione sicura preservando tutti i campi esistenti.
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
                    equippedHat TEXT NOT NULL DEFAULT 'NONE',
                    equippedWeapon TEXT NOT NULL DEFAULT 'NONE',
                    equippedFrame TEXT NOT NULL DEFAULT 'NONE'
                )
                """.trimIndent()
            )

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
                    equippedHat,
                    equippedWeapon,
                    equippedFrame
                )
                SELECT
                    id,
                    username,
                    email,
                    passwordHash,
                    xpTotale,
                    livello,
                    coins,
                    equippedHat,
                    equippedWeapon,
                    equippedFrame
                FROM users
                """.trimIndent()
            )

            database.execSQL("DROP TABLE users")
            database.execSQL("ALTER TABLE users_new RENAME TO users")
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
                .addMigrations(MIGRATION_3_4)
                .addMigrations(MIGRATION_4_5)
                .addMigrations(MIGRATION_5_6)
                .addMigrations(MIGRATION_6_7)
                .addMigrations(MIGRATION_7_8)
                .addMigrations(MIGRATION_8_9) // Registrata la nuova migrazione
                .fallbackToDestructiveMigration() // Utile in fase di sviluppo per evitare crash di migrazione
                .build()

            INSTANCE = instance

            instance
        }
    }
}