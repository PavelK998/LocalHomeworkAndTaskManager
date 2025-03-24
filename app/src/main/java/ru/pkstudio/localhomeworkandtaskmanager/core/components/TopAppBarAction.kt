package ru.pkstudio.localhomeworkandtaskmanager.core.components

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Immutable
data class TopAppBarAction(
    val image: ImageVector? = null,
    @DrawableRes val imageRes: Int = 0,
    val contentDescription: String,
    val action: () -> Unit,
    val tint: Color
)
