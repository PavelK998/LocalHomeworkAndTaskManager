package ru.pkstudio.localhomeworkandtaskmanager.core.components.kanban

import androidx.compose.runtime.Immutable

@Immutable
data class KanbanItem<M, A>(
    val rowItem: M,
    val columnItems: List<A>,
)
