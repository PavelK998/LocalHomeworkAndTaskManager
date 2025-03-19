package ru.pkstudio.localhomeworkandtaskmanager.core.components.kanban

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable

@Immutable
data class KanbanRowItem <T>(
    val header : @Composable () -> Unit = {},
    val footer : @Composable () -> Unit = {},
    val items : List<T>,
    val itemFiller : @Composable (T) -> Unit = {},
)
