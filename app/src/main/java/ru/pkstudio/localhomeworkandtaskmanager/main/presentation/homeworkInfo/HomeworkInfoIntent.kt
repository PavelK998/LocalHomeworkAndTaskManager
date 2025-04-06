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
}