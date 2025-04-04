package ru.pkstudio.localhomeworkandtaskmanager.main.data.repository

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import ru.pkstudio.localhomeworkandtaskmanager.main.data.local.AppDb
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository.ImportExportDbRepository
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.inject.Inject

class ImportExportDbRepositoryImpl @Inject constructor(
    private val context: Context,
    private val appDb: AppDb
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
            arrayOf(DocumentsContract.Document.COLUMN_DOCUMENT_ID, DocumentsContract.Document.COLUMN_DISPLAY_NAME),
            null,
            null,
            null
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                val displayName = cursor.getString(cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DISPLAY_NAME))
                if (displayName == fileName) {
                    val documentId = cursor.getString(cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DOCUMENT_ID))
                    return DocumentsContract.buildDocumentUriUsingTree(parentUri, documentId)
                }
            }
        }
        return null
    }

    override suspend fun exportDatabase(uri: Uri) {
        if (appDb.isOpen) {
            appDb.close()
        }
        val documentUri = DocumentsContract.buildDocumentUriUsingTree(
            uri,
            DocumentsContract.getTreeDocumentId(uri)
        )
        var targetFileUri = findFileInDirectory(documentUri, "homework.db")
        if (targetFileUri == null) {
            targetFileUri = DocumentsContract.createDocument(
                context.contentResolver,
                documentUri,
                "application/x-sqlite3",
                "homework.db"
            )
        }
        targetFileUri?.let { uri ->
            val databaseFile = context.getDatabasePath("homework.db")
            if(databaseFile.exists()) {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    FileInputStream(databaseFile).use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
        }
    }

        //android  < 10
//        if (appDb.isOpen){
//            appDb.close()
//        }
//        val databasePath = context.getDatabasePath("homework.db")
//        val externalStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
//        val appFolder = File(externalStorageDir, "MyHomeworkManager")
//        if (!appFolder.exists()) {
//            appFolder.mkdirs()
//        }
//        val targetFile = File(appFolder, "homework_backup.db")
//        databasePath.copyTo(targetFile, overwrite = true)
    }

    override suspend fun importDatabase(uri: Uri) {
        if (appDb.isOpen) {
            appDb.close()
        }
        val databasePath = context.getDatabasePath("homework.db")
        val file = uriToFile(uri)
        file?.copyTo(databasePath, overwrite = true)

        //android  < 10
//        if (appDb.isOpen) {
//            appDb.close()
//        }
//        val currentPath = context.getDatabasePath("homework.db")
//        val externalStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
//        val appDir = File(externalStorageDir, "MyHomeworkManager")
//        val dbFile = File(appDir, "homework_backup.db")
//        dbFile.copyTo(currentPath, overwrite = true)
    }
}