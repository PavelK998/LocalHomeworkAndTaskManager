package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.addHomework

import android.graphics.Bitmap

data class AddHomeworkState(
    val title: String = "",
    val description: String = "",
    val subjectName: String = "",
    val subjectId: String = "",
    val image: Bitmap? = null,
    val imagesList: List<Pair<String, Bitmap>> = emptyList()
)