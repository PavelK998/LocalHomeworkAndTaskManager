package ru.pkstudio.localhomeworkandtaskmanager.main.domain.model

data class SubjectModel(
    val id: String = "",
    val subjectName: String,
    val comment: String,
    val homework: List<HomeworkModel> = emptyList()
)
