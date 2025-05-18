package ru.pkstudio.localhomeworkandtaskmanager.main.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
                    val documentName = "IMG_${timeStamp}.png"
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

    override suspend fun findImagesInUserFolder(
        folderUri: Uri,
        namesList: List<String>
    ): List<Bitmap> = withContext(Dispatchers.IO) {
        val listBitmap: MutableList<Bitmap> = mutableListOf()
        val folder = DocumentFile.fromTreeUri(context, folderUri)
            ?: throw IllegalArgumentException("Invalid folder Uri: $folderUri")
        Log.d("nbnbvnvbnvbn", "folder name: ${folder.name}")
        if (!folder.isDirectory) {
            throw IllegalArgumentException("Uri does not point to a directory: $folderUri")
        }
        namesList.forEach { displayName ->
            Log.d("nbnbvnvbnvbn", "default file name: $displayName")
            val documentFile= folder.findFile(displayName)
            Log.d("nbnbvnvbnvbn", "findImageSInUserFolder: $documentFile")
            documentFile?.let {
                if (it.type == "image/png") {
                    context.contentResolver.openInputStream(documentFile.uri)?.use { inputStream ->
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        listBitmap.add(bitmap)
                    }
                }

            }
        }
        listBitmap
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
