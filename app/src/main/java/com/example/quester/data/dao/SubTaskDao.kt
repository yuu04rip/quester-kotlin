package com.example.quester.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.quester.data.model.SubTask
import kotlinx.coroutines.flow.Flow

/**
 * DAO per sotto-task missione.
 * UC7: conferma todolist/completamento task.
 */
@Dao
interface SubTaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubTasks(subTasks: List<SubTask>)

    @Query("SELECT * FROM subtasks WHERE missionId = :missionId ORDER BY id ASC")
    fun getSubTasksByMissionId(missionId: Long): Flow<List<SubTask>>

    @Update
    suspend fun updateSubTask(subTask: SubTask)

    @Query("SELECT COUNT(*) FROM subtasks WHERE missionId = :missionId AND done = 1")
    suspend fun countCompletedSubTasks(missionId: Long): Int

    @Query("SELECT COUNT(*) FROM subtasks WHERE missionId = :missionId")
    suspend fun countAllSubTasks(missionId: Long): Int
}