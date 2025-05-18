package ru.pkstudio.localhomeworkandtaskmanager.main.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.pkstudio.localhomeworkandtaskmanager.main.data.local.UtilsDao
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toListUtilsModel
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toUtilsEntity
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.UtilsModel
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository.UtilsRepository
import javax.inject.Inject

class UtilsRepositoryImpl @Inject constructor(
    private val utilsDao: UtilsDao
): UtilsRepository {
    override suspend fun insertUtils(utils: UtilsModel) = withContext(Dispatchers.IO) {
        utilsDao.insertUtils(utils.toUtilsEntity())
    }

    override suspend fun updateUtils(utils: UtilsModel) = withContext(Dispatchers.IO) {
        utilsDao.updateUtils(utils.toUtilsEntity())
    }

    override suspend fun getAllUtils() = withContext(Dispatchers.IO) {
        utilsDao.getAllUtils().toListUtilsModel()
    }
}