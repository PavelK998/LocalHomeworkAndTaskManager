package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList.uiModel

data class StageUiModel(
    val id: String,
    val color: Int,
    val stageName: String,
    val itemsCount: String,
    val isFinishStage: Boolean = false,
)
