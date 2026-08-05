package com.example.quester.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.quester.data.model.OwnedCosmetic
import kotlinx.coroutines.flow.Flow

@Dao
interface OwnedCosmeticDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOwned(item: OwnedCosmetic): Long

    @Query("SELECT COUNT(*) > 0 FROM owned_cosmetics WHERE userId = :userId AND itemId = :itemId")
    suspend fun isOwned(userId: Long, itemId: String): Boolean

    @Query("SELECT * FROM owned_cosmetics WHERE userId = :userId")
    fun getOwnedByUser(userId: Long): Flow<List<OwnedCosmetic>>
}