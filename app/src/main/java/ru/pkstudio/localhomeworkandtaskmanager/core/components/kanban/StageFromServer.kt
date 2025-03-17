package ru.pakarpichev.homeworktool.core.presentation.components.kanban

data class StageFromServer(
    val id: Int,
    val name: String,
    val deals: List<String>
)
