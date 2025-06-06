package ru.pkstudio.localhomeworkandtaskmanager.main.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.pkstudio.localhomeworkandtaskmanager.main.data.model.StageEntity

@Dao
interface StageDao {
    @Insert
    suspend fun insertStage(stage: StageEntity)

    @Insert
    suspend fun insertStages(stageList: List<StageEntity>)

    @Delete
    suspend fun deleteStage(stage: StageEntity)

    @Update
    suspend fun updateStage(stage: StageEntity)

    @Query("SELECT * FROM stage WHERE id = :stageId")
    suspend fun getStageById(stageId: Long): StageEntity

    @Query("SELECT * FROM stage ORDER BY position ASC")
    fun getAllStages(): Flow<List<StageEntity>>

    @Query("SELECT * FROM stage ORDER BY position ASC")
    fun getAllStagesSingleTime(): List<StageEntity>

    @Query("UPDATE stage SET position = position + 1 WHERE position >= :newPosition")
    suspend fun shiftPositionsPlusOne(newPosition: Int)

    @Query("UPDATE stage SET position = position - 1 WHERE position > :newPosition")
    suspend fun shiftPositionsMinusOne(newPosition: Int)

    @Transaction
    suspend fun insertWithShift(stage: StageEntity) {
        shiftPositionsPlusOne(stage.position)
        insertStage(stage)
    }

    @Transaction
    suspend fun deleteWithShift(stage: StageEntity) {
        shiftPositionsMinusOne(stage.position)
        deleteStage(stage)
    }

    @Query("UPDATE stage SET position = :position WHERE id = :id")
    suspend fun updatePosition(id: Int, position: Int)
}