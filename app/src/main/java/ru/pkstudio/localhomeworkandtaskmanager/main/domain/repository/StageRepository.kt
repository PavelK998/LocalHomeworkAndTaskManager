package ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.StageModel

interface StageRepository {
    suspend fun insertStage(stage: StageModel)

    suspend fun deleteStage(stage: StageModel)

    suspend fun updateStage(stage: StageModel)

    suspend fun getStageById(stageId: Long): StageModel

    suspend fun getAllStages(): Flow<List<StageModel>>

}