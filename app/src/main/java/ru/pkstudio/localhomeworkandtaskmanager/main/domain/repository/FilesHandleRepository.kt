package ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository

import android.graphics.Bitmap
import android.net.Uri

interface FilesHandleRepository {

    suspend fun uploadImageToUserFolder(folderUri: Uri, bitmapList: List<Bitmap>): List<String>

    suspend fun checkUriPermission(folderUri: Uri): Boolean

}