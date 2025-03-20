package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.subjectList.uiModel

data class SubjectUiModel(
    val id: Long,
    val subjectName: String,
    val comment: String = "",
    val isRevealed: Boolean = false,
    val isEditModeEnabled: Boolean = false
)