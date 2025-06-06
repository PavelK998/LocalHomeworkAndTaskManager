package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList.uiModel

data class HomeworkUiModel(
    val id: Long,
    val color: Int,
    val importance: Int,
    val addDate: String,
    val name: String,
    val description: String,
    val subjectId: Long,
    val startDate: String,
    val endDate: String,
    val stageName: String,
    val stageId: Long,
    val imageNameList: List<String>,
    val isChecked: Boolean,
    val isFinished: Boolean,
    val isCheckBoxVisible: Boolean
)
