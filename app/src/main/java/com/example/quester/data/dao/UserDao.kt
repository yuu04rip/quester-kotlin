package com.example.quester.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.quester.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getUser(): User?

    @Query("SELECT * FROM users LIMIT 1")
    fun getUserFlow(): Flow<User?>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Long): User?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun getUserByIdFlow(userId: Long): Flow<User?>

    @Query("SELECT * FROM users WHERE username = :username COLLATE NOCASE LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM users WHERE email = :email COLLATE NOCASE LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE username = :identity COLLATE NOCASE OR email = :identity COLLATE NOCASE LIMIT 1")
    suspend fun getUserByIdentity(identity: String): User?

    @Update
    suspend fun updateUser(user: User)

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()

    // NUOVE QUERY PER SICUREZZA

    // Aggiorna reputation
    @Query("UPDATE users SET reputation = reputation + :delta WHERE id = :userId")
    suspend fun updateReputation(userId: Long, delta: Int)

    // Aggiorna warnings
    @Query("UPDATE users SET warnings = warnings + 1 WHERE id = :userId")
    suspend fun incrementWarnings(userId: Long)

    // Resetta XP giornalieri (chiamato quando cambia giorno)
    @Query("UPDATE users SET xpEarnedToday = 0, lastXpResetDate = :resetDate WHERE id = :userId")
    suspend fun resetDailyXp(userId: Long, resetDate: Long)

    // Aggiungi XP giornalieri
    @Query("UPDATE users SET xpEarnedToday = xpEarnedToday + :xpAmount WHERE id = :userId")
    suspend fun addDailyXp(userId: Long, xpAmount: Int)
}