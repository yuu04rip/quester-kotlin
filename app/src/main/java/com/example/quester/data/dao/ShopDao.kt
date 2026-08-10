package com.example.quester.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.quester.data.model.ShopItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopDao {
    @Query("SELECT * FROM shop_items ORDER BY price ASC")
    fun getAllItemsFlow(): Flow<List<ShopItem>>

    @Query("SELECT * FROM shop_items ORDER BY price ASC")
    suspend fun getAllItems(): List<ShopItem>

    @Query("DELETE FROM shop_items")
    suspend fun deleteAllItems()

    @Query("SELECT * FROM shop_items WHERE itemId = :itemId LIMIT 1")
    suspend fun getItemByItemId(itemId: String): ShopItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)  // REPLACE evita duplicati
    suspend fun upsertItems(items: List<ShopItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ShopItem)
}