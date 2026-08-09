package com.example.quester.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.quester.data.dao.MissionDao
import com.example.quester.data.dao.OwnedCosmeticDao
import com.example.quester.data.dao.ShopDao
import com.example.quester.data.dao.SubTaskDao
import com.example.quester.data.dao.UserDao
import com.example.quester.data.model.Mission
import com.example.quester.data.model.OwnedCosmetic
import com.example.quester.data.model.ShopItem
import com.example.quester.data.model.SubTask
import com.example.quester.data.model.User

@Database(
    entities = [
        User::class,
        Mission::class,
        SubTask::class,
        ShopItem::class,
        OwnedCosmetic::class
    ],
    version = 4, // Versione incrementata per la migration
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun missionDao(): MissionDao
    abstract fun subTaskDao(): SubTaskDao
    abstract fun shopDao(): ShopDao
    abstract fun ownedCosmeticDao(): OwnedCosmeticDao
}