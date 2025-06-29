package ru.pkstudio.localhomeworkandtaskmanager.main.data.repository

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository.ImportExportDbRepository
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.inject.Inject

class ImportExportDbRepositoryImpl @Inject constructor(
    private val context: Context,
) : ImportExportDbRepository {

    private fun uriToFile(uri: Uri): File? {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val fileName = getFileName(context, uri) ?: "temp_file"
            val file = File(context.cacheDir, fileName)
            inputStream?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            return file
        } catch (e: Exception) {
            return null
        }
    }

    private fun getFileName(context: Context, uri: Uri): String? {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        return cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            it.moveToFirst()
            it.getString(nameIndex)
        }
    }

    private fun findFileInDirectory(parentUri: Uri, fileName: String): Uri? {
        val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(
            parentUri,
            DocumentsContract.getTreeDocumentId(parentUri)
        )
        context.contentResolver.query(
            childrenUri,
            arrayOf(
                DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                DocumentsContract.Document.COLUMN_DISPLAY_NAME
            ),
            null,
            null,
            null
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                val displayName =
                    cursor.getString(cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DISPLAY_NAME))
                if (displayName == fileName) {
                    val documentId =
                        cursor.getString(cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DOCUMENT_ID))
                    return DocumentsContract.buildDocumentUriUsingTree(parentUri, documentId)
                }
            }
        }
        return null
    }

    override suspend fun exportDatabase(uri: Uri) {
//        if (appDb.isOpen) {
//            appDb.close()
//        }

        val folder = DocumentFile.fromTreeUri(context, uri)
            ?: throw IllegalArgumentException("Invalid folder Uri: $uri")
        Log.d("uytutyuytuty", "exportDatabase: ${folder.name}")
        if (!folder.isDirectory) {
            throw IllegalArgumentException("Uri does not point to a directory: $uri")
        }
        var targetFile = folder.findFile("homework.db")
        Log.d("uytutyuytuty", "exportDatabase: ${targetFile}")
        if (targetFile == null) {
            targetFile = folder.createFile("application/x-sqlite3", "homework.db")
                ?: throw IllegalStateException("Failed to create file homework.db")
        }

        val mimeType = targetFile.uri.let { context.contentResolver.getType(it) }
        if (mimeType != "application/x-sqlite3") {
            println("Warning: MIME type is $mimeType instead of application/x-sqlite3")
        }

        // Копируем данные базы данных в новый файл
        targetFile.uri.let { targetUri ->
            val databaseFile = context.getDatabasePath("homework.db")
            if (databaseFile.exists()) {
                context.contentResolver.openOutputStream(targetUri)?.use { outputStream ->
                    FileInputStream(databaseFile).use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                } ?: throw IllegalStateException("Failed to open output stream for $targetUri")
            } else {
                throw IllegalStateException("Database file does not exist")
            }
        }
    }

    override suspend fun importDatabase(uri: Uri) {
//        if (appDb.isOpen) {
//            appDb.close()
//        }
        val databasePath = context.getDatabasePath("homework.db")
        val file = uriToFile(uri)
        file?.copyTo(databasePath, overwrite = true)
    }
}