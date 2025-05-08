package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkInfo

import androidx.compose.ui.graphics.Color

sealed interface HomeworkInfoIntent {
    data object NavigateUp: HomeworkInfoIntent

    data object OnStageSelectClick: HomeworkInfoIntent

    data object OnSettingsClicked: HomeworkInfoIntent

    data object CloseSettingsMenu: HomeworkInfoIntent

    data object OnDeleteBtnClick: HomeworkInfoIntent

    data object DeleteConfirm: HomeworkInfoIntent

    data object CloseDeleteAlertDialog: HomeworkInfoIntent

    data class OnHomeworkEditNameChange(val text: String): HomeworkInfoIntent

    data class OnHomeworkEditDescriptionChange(val text: String): HomeworkInfoIntent

    data object UpdateConfirm: HomeworkInfoIntent

    data object UpdateDismiss: HomeworkInfoIntent

    data object CloseUpdateAlertDialog: HomeworkInfoIntent

    data object OpenImportanceColorDialog: HomeworkInfoIntent

    data object CloseImportanceColorDialog: HomeworkInfoIntent

    data object OpenStagePickerDialog: HomeworkInfoIntent

    data object CloseStagePickerDialog: HomeworkInfoIntent


    data class SelectStage(val index: Int): HomeworkInfoIntent

    data class SelectImportanceColor(val color: Color): HomeworkInfoIntent


    //edit text
    data object ToggleNameBold: HomeworkInfoIntent

    data object ToggleNameItalic: HomeworkInfoIntent

    data object ToggleNameLineThrough: HomeworkInfoIntent

    data object ToggleNameUnderline: HomeworkInfoIntent

    data object ToggleNameExtraOptions: HomeworkInfoIntent

    data object ToggleDescriptionBold: HomeworkInfoIntent

    data object ToggleDescriptionItalic: HomeworkInfoIntent

    data object ToggleDescriptionLineThrough: HomeworkInfoIntent

    data object ToggleDescriptionUnderline: HomeworkInfoIntent

    data object ToggleDescriptionExtraOptions: HomeworkInfoIntent

    data class NameFontSizeChange(val font: Int): HomeworkInfoIntent

    data class DescriptionFontSizeChange(val font: Int): HomeworkInfoIntent

    data object OnNameChangeClick: HomeworkInfoIntent

    data object CloseNameChangeCard: HomeworkInfoIntent

    data object CloseDescriptionChangeCard: HomeworkInfoIntent

    data object OnDescriptionChangeClick: HomeworkInfoIntent
}