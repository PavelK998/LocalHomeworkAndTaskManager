package ru.pkstudio.localhomeworkandtaskmanager.main.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class HomeworkModel(
    val id: Long?,
    val color: Int,
    val importance: Int,
    val subjectId: Long,
    val addDate: LocalDateTime?,
    val name: String,
    val stage: String,
    val stageId: Long,
    val description: String,
    val startDate: LocalDate?,
    val endDate: LocalDateTime?,
    val imageNameList: List<String>
)
