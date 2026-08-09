package com.example.quester.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shop_items")
data class ShopItem(
    @PrimaryKey  // Rendi itemId la chiave primaria invece di id
    val itemId: String,
    val name: String,
    val price: Int,
    val description: String = ""
)