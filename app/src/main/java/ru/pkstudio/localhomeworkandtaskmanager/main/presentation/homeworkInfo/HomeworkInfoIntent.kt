package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkInfo

sealed interface HomeworkInfoIntent {
    data object NavigateUp: HomeworkInfoIntent

    data object OnStageSelectClick: HomeworkInfoIntent

    data object OnSettingsClicked: HomeworkInfoIntent

    data object CloseStageMenu: HomeworkInfoIntent

    data object CloseSettingsMenu: HomeworkInfoIntent

    data object OnDeleteBtnClick: HomeworkInfoIntent

    data object DeleteConfirm: HomeworkInfoIntent

    data object CloseDeleteAlertDialog: HomeworkInfoIntent

    data object OnEditClick: HomeworkInfoIntent

    data object ConfirmEditResult: HomeworkInfoIntent

    data object DismissEditMode: HomeworkInfoIntent

    data class OnMenuItemClick(val index: Int, val stageId: Long): HomeworkInfoIntent

    data class OnHomeworkEditNameChange(val text: String): HomeworkInfoIntent

    data class OnHomeworkEditDescriptionChange(val text: String): HomeworkInfoIntent

    data object UpdateConfirm: HomeworkInfoIntent

    data object UpdateDismiss: HomeworkInfoIntent

    data object CloseUpdateAlertDialog: HomeworkInfoIntent


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