package ru.pkstudio.localhomeworkandtaskmanager.main.domain.model

data class StageModel(
    val id: Long? = null,
    val color: Int,
    val stageName: String,
    val position: Int
)
