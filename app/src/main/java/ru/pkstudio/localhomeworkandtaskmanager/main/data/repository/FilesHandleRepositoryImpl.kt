package ru.pkstudio.localhomeworkandtaskmanager.main.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.DocumentsContract
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository.FilesHandleRepository
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class FilesHandleRepositoryImpl @Inject constructor(
    private val context: Context
) : FilesHandleRepository {
    override suspend fun uploadImageToUserFolder(folderUri: Uri, bitmapList: List<Bitmap>) =
        withContext(Dispatchers.Default) {
            val deferredResultList:MutableList<Deferred<Unit>> = mutableListOf()
            val imageNamesList:MutableList<String> = mutableListOf()
            bitmapList.forEach { bitmap ->
                val deferredResult = async {
                    val time = LocalDateTime.now()
                    val timeStamp = time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH_mm_ss.SSS"))
                    val documentName = "IMG_${timeStamp}.jpeg"
                    imageNamesList.add(documentName)
                    val targetFileUri = DocumentsContract.createDocument(
                        context.contentResolver,
                        folderUri,
                        "image/*",
                        documentName
                    ) ?: throw Exception("Something went wrong when try to create a file")
                    targetFileUri.let { uri ->
                        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                            outputStream.flush()
                        } ?: throw Exception("Something went wrong when try to create a file")
                    }

                }
                deferredResultList.add(deferredResult)
                delay(10)
            }
            deferredResultList.awaitAll()
            imageNamesList

        }

    override suspend fun uploadImageToUserFolderWithImageUriList(
        folderUri: Uri,
        imageUriList: List<Uri>
    ) = withContext(Dispatchers.IO) {
        val imageNamesList:MutableList<String> = mutableListOf()
        imageUriList.forEach { imageUri ->
            Log.d("nvbnvbnvbnbvnv", "uploadImage: $imageUri")
            // Получаем InputStream из content:// Uri
            context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                Log.d("nvbnvbnvbnbvnv", "uploadImage: input stream open")
                // Создаем новый файл в папке, выбранной через SAF
                val documentFile = DocumentFile.fromTreeUri(context, folderUri)
                val time = LocalDateTime.now()
                val timeStamp = time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH_mm_ss.SSS"))
                val documentName = "IMG_${timeStamp}.jpeg"
                val newFile = documentFile?.createFile("image/jpeg", documentName)
                newFile?.uri?.let { outputUri ->
                    // Получаем OutputStream для нового файла
                    context.contentResolver.openOutputStream(outputUri)?.use { outputStream ->
                        // Копируем данные
                        inputStream.copyTo(outputStream)
                    }
                }
                imageNamesList.add(documentName)
            } ?: throw Exception("something went wrong when saving images")
        }
        imageNamesList
    }




    override suspend fun deleteImageInUserFolder(folderUri: Uri, imageName: String) = withContext(Dispatchers.IO) {
        val folder = DocumentFile.fromTreeUri(context, folderUri)
            ?: throw IllegalArgumentException("Invalid folder Uri: $folderUri")
        if (!folder.isDirectory) {
            throw IllegalArgumentException("Uri does not point to a directory: $folderUri")
        }
        val documentFile= folder.findFile(imageName)
        documentFile?.let {
            if (it.type == "image/jpeg") {
                val result = documentFile.delete()
                if (!result) {
                    throw Exception("can not delete a file")
                }
            }
        } ?: throw Exception("can not delete a file")
    }

    override suspend fun deleteAllImagesInUserFolder(folderUri: Uri, namesList: List<String>) = withContext(Dispatchers.IO) {
        val listDeferredResult: MutableList<Deferred<Boolean>> = mutableListOf()
        val folder = DocumentFile.fromTreeUri(context, folderUri)
            ?: throw IllegalArgumentException("Invalid folder Uri: $folderUri")
        Log.d("nbnbvnvbnvbn", "folder name: ${folder.name}")
        if (!folder.isDirectory) {
            throw IllegalArgumentException("Uri does not point to a directory: $folderUri")
        }
        namesList.forEach { displayName ->
            val result = async {
                var deleteResult = false
                val documentFile= folder.findFile(displayName)
                documentFile?.let {
                    if (it.type == "image/jpeg") {
                        deleteResult = documentFile.delete()
                    }
                }
                deleteResult
            }
            listDeferredResult.add(result)
        }
        listDeferredResult.awaitAll().any { !it }
    }

    override suspend fun findImagesInUserFolder(
        folderUri: Uri,
        namesList: List<String>
    ): List<Uri> = withContext(Dispatchers.IO) {
        val listDeferredResult: MutableList<Deferred<Uri?>> = mutableListOf()
        val folder = DocumentFile.fromTreeUri(context, folderUri)
            ?: throw IllegalArgumentException("Invalid folder Uri: $folderUri")
        Log.d("nbnbvnvbnvbn", "folder name: ${folder.name}")
        if (!folder.isDirectory) {
            throw IllegalArgumentException("Uri does not point to a directory: $folderUri")
        }
        namesList.forEach { displayName ->
            val result = async {
                var uriResult: Uri? = null
                val documentFile= folder.findFile(displayName)
                documentFile?.let {
                    Log.d("nbnbvnvbnvbn", "document = $documentFile")
                    if (it.type == "image/jpeg") {
                        uriResult = documentFile.uri
                    }
                }
                uriResult
            }
            listDeferredResult.add(result)
        }
        listDeferredResult.awaitAll().requireNoNulls()
    }

    override suspend fun checkUriPermission(folderUri: Uri) = withContext(Dispatchers.IO) {
        try {
            DocumentFile.fromSingleUri(context, folderUri)?.canWrite() ?: false
        } catch (e: SecurityException) {
            false // Нет разрешения
        } catch (e: IOException) {
            false // Другие ошибки (например, файл недоступен)
        }
    }
}
