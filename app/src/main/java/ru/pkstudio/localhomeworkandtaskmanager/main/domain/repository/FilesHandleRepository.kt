package ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository

import android.graphics.Bitmap
import android.net.Uri

interface FilesHandleRepository {

    suspend fun uploadImageToUserFolder(folderUri: Uri, bitmapList: List<Bitmap>): List<String>

    suspend fun uploadImageToUserFolderWithImageUriList(folderUri: Uri, imageUriList: List<Uri>): List<String>

    suspend fun deleteImageInUserFolder(folderUri: Uri, imageName: String)

    suspend fun deleteAllImagesInUserFolder(folderUri: Uri, namesList: List<String>): Boolean

    suspend fun findImagesInUserFolder(folderUri: Uri, namesList: List<String>): List<Uri>

    suspend fun checkUriPermission(folderUri: Uri): Boolean

    suspend fun uploadImageWithImageUriList(imageUriList: List<Uri>): List<String>

    suspend fun deleteImage(imageName: String)

    suspend fun deleteAllImages(namesList: List<String>): Boolean

    suspend fun findImagesInFolder(namesList: List<String>): List<Bitmap>

}