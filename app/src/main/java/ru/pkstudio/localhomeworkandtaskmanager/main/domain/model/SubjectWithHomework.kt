package ru.pkstudio.localhomeworkandtaskmanager.main.domain.model

data class SubjectWithHomework(
    val subject: SubjectModel,
    val homework: List<HomeworkModel>
)
