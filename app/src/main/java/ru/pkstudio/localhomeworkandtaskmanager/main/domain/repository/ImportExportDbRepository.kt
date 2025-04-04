package ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository

import android.net.Uri

interface ImportExportDbRepository {
    suspend fun exportDatabase(uri: Uri)

    suspend fun importDatabase(uri: Uri)
}