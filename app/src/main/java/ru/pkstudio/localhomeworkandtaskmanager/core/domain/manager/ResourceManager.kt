package ru.pkstudio.localhomeworkandtaskmanager.core.domain.manager

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

interface ResourceManager {
    fun getString(@StringRes id: Int): String

    fun getString(id: Int, vararg args: Any): String

    fun getStringArray(id: Int): Array<String>

    fun getDrawable(@DrawableRes id: Int): Drawable?

    fun getColor(@ColorRes id: Int): Int

    suspend fun parseBase64StringFromUri(uri: Uri): String?

    suspend fun parseBitmapFromUri(uri: Uri): Bitmap?
}