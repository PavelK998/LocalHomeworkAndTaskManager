package ru.pkstudio.localhomeworkandtaskmanager.core.data.manager

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.util.Base64
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import ru.pkstudio.localhomeworkandtaskmanager.core.domain.manager.ResourceManager
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import kotlin.math.roundToInt

class ResourceManagerImpl @Inject constructor(@ApplicationContext private val context: Context) :
    ResourceManager {
    override fun getString(id: Int): String = context.getString(id)
    override fun getString(id: Int, vararg args: Any): String = context.getString(id, *args)
    override fun getStringArray(id: Int): Array<String> = context.resources.getStringArray(id)
    override fun getDrawable(id: Int): Drawable? = ContextCompat.getDrawable(context, id)
    override fun getColor(id: Int): Int = context.getColor(id)
    override suspend fun parseBase64StringFromUri(uri: Uri) = withContext(Dispatchers.Default) {
        val contentResolver = context.contentResolver
        val bytes = contentResolver.openInputStream(uri).use {
            it?.readBytes()
        }
        bytes?.let {
            Base64.encodeToString(bytes, Base64.DEFAULT)
        }
    }

    override suspend fun parseBitmapFromUri(uri: Uri) = withContext(Dispatchers.IO) {
        try {
            val compressionTheshold = 200 * 1024L
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(uri)
            val bitmap = contentResolver.openInputStream(uri).use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
            ensureActive()
            withContext(Dispatchers.Default) {
                val compressFormat = when (mimeType) {
                    "image/png" -> Bitmap.CompressFormat.PNG
                    "image/jpeg" -> Bitmap.CompressFormat.JPEG
                    "image/webp" -> if (Build.VERSION.SDK_INT >= 30) {
                        Bitmap.CompressFormat.WEBP_LOSSLESS
                    } else {
                        Bitmap.CompressFormat.WEBP
                    }

                    else -> {
                        Bitmap.CompressFormat.JPEG
                    }
                }
                var outputBytes: ByteArray
                var quality = 90
                do {
                    ByteArrayOutputStream().use { outputStream ->
                        bitmap.compress(compressFormat, quality, outputStream)
                        outputBytes = outputStream.toByteArray()
                        quality -= (quality * 0.1).roundToInt()
                    }

                } while (
                    isActive &&
                    outputBytes.size > compressionTheshold &&
                    quality > 5 &&
                    compressFormat != Bitmap.CompressFormat.PNG
                )
                bitmap
            }

        } catch (e: Exception) {
            null
        }
    }
}