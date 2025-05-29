package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList

import androidx.compose.ui.graphics.Color

sealed interface HomeworkListIntent {
    data object NavigateToAddHomework : HomeworkListIntent

    data object NavigateToEditStages : HomeworkListIntent

    data class NavigateToDetailsHomework(
        val homeworkId: Long,
        val subjectId: Long,
    ) : HomeworkListIntent

    data class NavigateToDetailsHomeworkFromKanban(
        val rowIndex: Int,
        val columnIndex: Int,
    ) : HomeworkListIntent

    data class CheckCard(val index: Int, val isChecked: Boolean) : HomeworkListIntent

    data object ExpandMenu : HomeworkListIntent

    data object ShrinkMenu : HomeworkListIntent

    data object DeleteCards : HomeworkListIntent

    data object ConfirmDeleteCards : HomeworkListIntent

    data object CloseDeleteAlertDialog : HomeworkListIntent

    data object NavigateUp : HomeworkListIntent

    data class TurnEditMode(val index: Int) : HomeworkListIntent

    data class OnSegmentedButtonClick(val index: Int) : HomeworkListIntent

    data object TurnFabVisible : HomeworkListIntent

    data object TurnFabInvisible : HomeworkListIntent

    data class OnCardColorPaletteClicked(val modelIndex: Int) : HomeworkListIntent

    data class OnKanbanCardColorPaletteClicked(val modelId: Long) : HomeworkListIntent

    data object CloseCardColorPaletteDialog : HomeworkListIntent

    data class SelectColor(val color: Color) : HomeworkListIntent

    data class OnItemMoved(
        val oldRowId: Int,
        val oldColumnId: Int,
        val newRowId: Int
    ) : HomeworkListIntent

    data class DeleteItemFromKanban(
        val oldRowId: Int,
        val oldColumnId: Int,
    ) : HomeworkListIntent

    data class OnSortClick(val isSortDescendingImportance: Boolean) : HomeworkListIntent

    data object OnOpenBottomSheetClick : HomeworkListIntent

    data object CloseBottomSheet : HomeworkListIntent

}