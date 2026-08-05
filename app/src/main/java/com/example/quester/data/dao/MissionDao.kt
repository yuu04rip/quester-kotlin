package com.example.quester.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.quester.data.model.Mission
import kotlinx.coroutines.flow.Flow

/**
 * DAO per gestione missioni/eventi.
 * UC5: creazione missione
 * UC6: visualizzazione missioni/dettaglio
 */
@Dao
interface MissionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMission(mission: Mission): Long

    @Query("SELECT * FROM missions ORDER BY id DESC")
    fun getAllMissions(): Flow<List<Mission>>

    @Query("SELECT * FROM missions WHERE id = :missionId LIMIT 1")
    fun getMissionById(missionId: Long): Flow<Mission?>

    @Query("SELECT * FROM missions WHERE id = :missionId LIMIT 1")
    suspend fun getMissionByIdOnce(missionId: Long): Mission?

    @Update
    suspend fun updateMission(mission: Mission)

    @Delete
    suspend fun deleteMission(mission: Mission)
}