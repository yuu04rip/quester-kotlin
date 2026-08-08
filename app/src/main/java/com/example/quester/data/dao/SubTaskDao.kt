package com.example.quester.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.quester.data.model.SubTask
import kotlinx.coroutines.flow.Flow

@Dao
interface SubTaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubTasks(subTasks: List<SubTask>)

    @Update
    suspend fun updateSubTask(subTask: SubTask)

    @Query("SELECT * FROM subtasks WHERE missionId = :missionId")
    fun getSubTasksByMissionId(missionId: Long): Flow<List<SubTask>>

    @Query("SELECT COUNT(*) FROM subtasks WHERE missionId = :missionId")
    suspend fun countAllSubTasks(missionId: Long): Int

    @Query("SELECT COUNT(*) FROM subtasks WHERE missionId = :missionId AND done = 1")
    suspend fun countCompletedSubTasks(missionId: Long): Int

    @Query("DELETE FROM subtasks WHERE missionId = :missionId")
    suspend fun deleteSubTasksForMission(missionId: Long)
}