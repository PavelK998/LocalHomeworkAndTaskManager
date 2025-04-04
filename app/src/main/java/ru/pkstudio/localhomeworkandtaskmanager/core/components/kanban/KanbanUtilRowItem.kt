package ru.pkstudio.localhomeworkandtaskmanager.core.components.kanban

data class KanbanUtilRowItem <M,A>(
    val id: Long,
    val rowItem: M,
    val items : List<Item<A>>,
)

data class Item<A>(
    val id: Long,
    val item: A
)
