package ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository

import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.UtilsModel

interface UtilsRepository {
    suspend fun insertUtils(utils: UtilsModel)

    suspend fun updateUtils(utils: UtilsModel)

    suspend fun getAllUtils(): List<UtilsModel>
}