package ru.pkstudio.localhomeworkandtaskmanager.main.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.pkstudio.localhomeworkandtaskmanager.main.data.local.StageDao
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toStageEntity
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toStageEntityList
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

    override suspend fun insertStages(stagesList: List<StageModel>) = withContext(Dispatchers.IO) {
        stageDao.insertStages(
            stageList = stagesList.toStageEntityList()
        )
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

    override suspend fun getAllStagesSingleTime() = withContext(Dispatchers.IO) {
        stageDao.getAllStagesSingleTime().toStageModelList()
    }

    override suspend fun getAllStages()= withContext(Dispatchers.IO) {
        stageDao.getAllStages().map {
            it.toStageModelList()
        }
    }

    override suspend fun deleteStageFromPosition(stage: StageModel)= withContext(Dispatchers.IO) {
        stageDao.deleteWithShift(stage = stage.toStageEntity())
    }

    override suspend fun insertStageToPosition(stage: StageModel) = withContext(Dispatchers.IO) {
        stageDao.insertWithShift(stage = stage.toStageEntity())
    }

    override suspend fun shiftAllStagesPositionsPlusOne(position: Int) = withContext(Dispatchers.IO) {
        stageDao.shiftPositionsPlusOne(position)
    }
}