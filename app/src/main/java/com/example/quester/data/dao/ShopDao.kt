package com.example.quester.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.quester.data.model.ShopItem

@Dao
interface ShopDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertItems(items: List<ShopItem>)

    @Query("SELECT * FROM shop_items WHERE id = :itemId LIMIT 1")
    suspend fun getItemById(itemId: String): ShopItem?
}