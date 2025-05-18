package ru.pkstudio.localhomeworkandtaskmanager.main.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.DocumentsContract
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
                    val documentName = "IMG_${LocalDateTime.now()}.jpg"
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
