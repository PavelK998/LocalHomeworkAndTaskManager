package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.addHomework

import android.graphics.Bitmap
import com.mohamedrejeb.richeditor.model.RichTextState

data class AddHomeworkState(
    val title: String = "",
    val titleFontSize: Float = 17f,
    val descriptionFontSize: Float = 17f,
    val description: String = "",
    val subjectName: String = "",
    val subjectId: String = "",
    val image: Bitmap? = null,
    val imagesList: List<Pair<Long, Bitmap>> = emptyList(),
    val isNameCardVisible: Boolean = false,
    val isDescriptionCardVisible: Boolean = false,
    val isNameExtraOptionsVisible: Boolean = false,
    val isDescriptionExtraOptionsVisible: Boolean = false,
    val nameRichTextState: RichTextState = RichTextState(),
    val descriptionRichTextState: RichTextState = RichTextState(),
)