package ru.pkstudio.localhomeworkandtaskmanager.main.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.pkstudio.localhomeworkandtaskmanager.main.data.model.StageEntity

@Dao
interface StageDao {
    @Insert
    suspend fun insertStage(stage: StageEntity)

    @Delete
    suspend fun deleteStage(stage: StageEntity)

    @Update
    suspend fun updateStage(stage: StageEntity)

    @Query("SELECT * FROM stage WHERE id = :stageId")
    suspend fun getStageById(stageId: Long): StageEntity

    @Query("SELECT * FROM stage")
    fun getAllStages(): Flow<List<StageEntity>>
}