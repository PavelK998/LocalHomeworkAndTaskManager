package ru.pkstudio.localhomeworkandtaskmanager.core.components.kanban

import androidx.compose.runtime.Composable

data class KanbanUtilRowItem <T>(
    val id: Int,
    val header : @Composable () -> Unit = {},
    val footer : @Composable () -> Unit = {},
    val items : List<Item<T>>,
    val itemFiller : @Composable (T) -> Unit = {},
)

data class Item<T>(
    val id: Int,
    val item: T
)
