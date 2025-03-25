package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList

sealed interface HomeworkListIntent {
    data class NavigateToAddHomework(val subjectId: String) : HomeworkListIntent

    data class NavigateToDetailsHomework(val homeworkName: String) : HomeworkListIntent

    data class CheckCard(val index: Int, val isChecked: Boolean) : HomeworkListIntent

    data object ExpandMenu : HomeworkListIntent

    data object ShrinkMenu : HomeworkListIntent

    data object DeleteCards : HomeworkListIntent

    data object NavigateUp : HomeworkListIntent

    data object TurnEditMode : HomeworkListIntent

    data class OnSegmentedButtonClick(val index: Int) : HomeworkListIntent

    data class OnItemMoved(
        val oldRowId: Int,
        val oldColumnId: Int,
        val newRowId: Int
    ) : HomeworkListIntent
}