package com.example.quester.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.quester.data.model.Mission
import com.example.quester.data.model.MissionWithSubTasks
import kotlinx.coroutines.flow.Flow

@Dao
interface MissionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMission(mission: Mission): Long

    @Query("SELECT * FROM missions WHERE userId = :userId ORDER BY id DESC")
    fun getAllMissionsForUser(userId: Long): Flow<List<Mission>>

    @Transaction
    @Query("SELECT * FROM missions WHERE userId = :userId ORDER BY id DESC")
    fun getAllMissionsWithSubTasksForUser(userId: Long): Flow<List<MissionWithSubTasks>>

    @Query("SELECT * FROM missions WHERE id = :missionId LIMIT 1")
    fun getMissionById(missionId: Long): Flow<Mission?>

    @Query("SELECT * FROM missions WHERE id = :missionId LIMIT 1")
    suspend fun getMissionByIdOnce(missionId: Long): Mission?

    @Update
    suspend fun updateMission(mission: Mission)

    @Delete
    suspend fun deleteMission(mission: Mission)

    @Query("SELECT MAX(completedAt) FROM missions WHERE userId = :userId AND completed = 1")
    suspend fun getLastCompletionTime(userId: Long): Long?
}
