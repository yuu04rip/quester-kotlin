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

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Update
    suspend fun updateUser(user: User)

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}