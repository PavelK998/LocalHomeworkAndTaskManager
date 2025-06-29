package ru.pkstudio.localhomeworkandtaskmanager.main.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class HomeworkModel(
    val id: String,
    val color: Int,
    val importance: Int,
    val addDate: LocalDateTime?,
    val name: String,
    val stage: String,
    val stageId: String,
    val description: String,
    val startDate: LocalDate?,
    val endDate: LocalDateTime?,
    val isFinished: Boolean,
    val imageNameList: List<String>
)
