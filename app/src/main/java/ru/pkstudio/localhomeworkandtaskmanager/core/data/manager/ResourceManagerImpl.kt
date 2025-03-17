package ru.pakarpichev.homeworktool.core.data.manager

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Base64
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.pakarpichev.homeworktool.core.domain.manager.ResourceManager
import javax.inject.Inject

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
}