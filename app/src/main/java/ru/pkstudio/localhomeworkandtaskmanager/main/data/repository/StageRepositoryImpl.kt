package ru.pkstudio.localhomeworkandtaskmanager.main.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.pkstudio.localhomeworkandtaskmanager.main.data.local.StageDao
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toStageEntity
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toStageModel
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toStageModelList
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.StageModel
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository.StageRepository
import javax.inject.Inject

class StageRepositoryImpl @Inject constructor(
    private val stageDao: StageDao
): StageRepository {
    override suspend fun insertStage(stage: StageModel) = withContext(Dispatchers.IO) {
        stageDao.insertStage(stage.toStageEntity())
    }

    override suspend fun deleteStage(stage: StageModel) = withContext(Dispatchers.IO) {
        stageDao.deleteStage(stage.toStageEntity())
    }

    override suspend fun updateStage(stage: StageModel) = withContext(Dispatchers.IO) {
        stageDao.updateStage(stage.toStageEntity())
    }

    override suspend fun getStageById(stageId: Long) = withContext(Dispatchers.IO) {
        stageDao.getStageById(stageId).toStageModel()
    }

    override suspend fun getAllStages() = withContext(Dispatchers.IO) {
        stageDao.getAllStages().map {
            it.toStageModelList()
        }
    }
}