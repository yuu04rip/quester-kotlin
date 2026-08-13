package com.example.quester.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shop_items")
data class ShopItem(
    @PrimaryKey
    val itemId: String,
    val name: String,
    val price: Int,
    val description: String = "",
    val iconName: String = "shopping_cart",
    val iconScale: Float = 1.0f
)