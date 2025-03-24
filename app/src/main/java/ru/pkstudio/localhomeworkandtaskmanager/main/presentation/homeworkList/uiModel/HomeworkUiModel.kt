package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList.uiModel

data class HomeworkUiModel(
    val id: Long,
    val addDate: String,
    val name: String,
    val description: String,
    val startDate: String,
    val endDate: String,
    val stageName: String,
    val stageId: Long,
    val imageUrl: String,
    val isChecked: Boolean,
    val isCheckBoxVisible: Boolean
)
