package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.addHomework

import android.net.Uri

sealed interface AddHomeworkIntent {
    data class OnTitleHomeworkChange(val text:String): AddHomeworkIntent

    data class OnDescriptionHomeworkChange(val text:String): AddHomeworkIntent

    data class OnImagePicked(val uri: Uri): AddHomeworkIntent

    data class OnMultiplyImagePicked(val listUri: List<Uri>): AddHomeworkIntent

    data object NavigateUp: AddHomeworkIntent

    data object Save: AddHomeworkIntent

}