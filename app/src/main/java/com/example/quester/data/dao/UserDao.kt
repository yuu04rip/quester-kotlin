package com.example.quester.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.quester.data.model.User

/**
 * DAO per accesso ai dati utente.
 * Gestisce lettura/scrittura di XP e livello (UC9, UC10).
 */
@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getUser(): User?

    @Update
    suspend fun updateUser(user: User)
}