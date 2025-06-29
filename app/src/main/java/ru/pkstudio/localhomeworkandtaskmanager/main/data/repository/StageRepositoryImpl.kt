package ru.pkstudio.localhomeworkandtaskmanager.main.data.repository

import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.pkstudio.localhomeworkandtaskmanager.main.data.local.realm.StageObject
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toListStageModel
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toListStageObject
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toStageModel
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toStageObject
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.StageModel
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository.StageRepository
import javax.inject.Inject

class StageRepositoryImpl @Inject constructor(
    private val realmDb: Realm
): StageRepository {



    override suspend fun insertStage(stage: StageModel) = withContext(Dispatchers.IO) {
        realmDb.write {
            copyToRealm(
                instance = stage.toStageObject(),
                UpdatePolicy.ALL
            )
        }
        Unit
    }

    override suspend fun insertStages(stagesList: List<StageModel>) = withContext(Dispatchers.IO) {
        realmDb.write {
            stagesList.toListStageObject().forEach {
                copyToRealm(
                    instance = it,
                    UpdatePolicy.ALL
                )
            }
        }
    }

    override suspend fun deleteStage(stage: StageModel) = withContext(Dispatchers.IO) {
        realmDb.write {
            val objectToDelete = query<StageObject>("_id == $0", stage.id).find().firstOrNull()
            objectToDelete?.let {
                delete(it)
            } ?: throw Exception("There is not such object")

        }
    }

    override suspend fun updateStage(stage: StageModel) = withContext(Dispatchers.IO) {
        realmDb.write {
            val existingStage = realmDb
                .query<StageObject>("_id == $0", stage.toStageObject()._id)
                .first()
                .find()
            if (existingStage != null) {
                copyToRealm(stage.toStageObject(), UpdatePolicy.ALL)
            }
        }
    }

    override suspend fun getStageById(stageId: String) = withContext(Dispatchers.IO) {
        realmDb.query<StageObject>("_id == $0", stageId).find().firstOrNull()?.toStageModel()
            ?: throw Exception("No such object")
    }

    override suspend fun getAllStagesSingleTime() = withContext(Dispatchers.IO) {
        realmDb.query<StageObject>().sort("position").find().toListStageModel()

    }

    override suspend fun getAllStages()= withContext(Dispatchers.IO) {
        realmDb.query<StageObject>().asFlow().map {
            it.list.toList().sortedBy { it.position }.toListStageModel()
        }

    }

    override suspend fun deleteStageFromPosition(stage: StageModel)= withContext(Dispatchers.IO) {
        val queryObjects = realmDb.query<StageObject>("position >= $0", stage.toStageObject().position).find()
        if (queryObjects.isNotEmpty()){
            realmDb.write {
                queryObjects.forEach {
                    findLatest(it)?.position = it.position - 1
                }
                val objectToDelete = query<StageObject>("_id == $0", stage.toStageObject()._id).find().firstOrNull()
                objectToDelete?.let {
                    delete(it)
                } ?: throw Exception("There is not such object")
            }
        }
    }

    override suspend fun insertStageToPosition(stage: StageModel) = withContext(Dispatchers.IO) {

        val queryObjects = realmDb.query<StageObject>("position >= $0", stage.position).find()
        if (queryObjects.isNotEmpty()){

            realmDb.write {
                queryObjects.forEach {
                    findLatest(it)?.position = it.position + 1
                }
                copyToRealm(stage.toStageObject(), UpdatePolicy.ERROR)
            }
        }
    }
}