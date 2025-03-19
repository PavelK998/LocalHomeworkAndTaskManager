package ru.pkstudio.localhomeworkandtaskmanager.core.components.kanban

data class StageFromServer(
    val id: Int,
    val name: String,
    val deals: List<String>
)
