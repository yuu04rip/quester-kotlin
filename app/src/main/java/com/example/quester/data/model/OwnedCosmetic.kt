package com.example.quester.data.model

import androidx.room.Entity

@Entity(
    tableName = "owned_cosmetics",
    primaryKeys = ["userId", "itemId"]
)
data class OwnedCosmetic(
    val userId: Long,
    val itemId: String
)