package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.editStagesScreen

sealed interface EditStageUiAction {
    data class ShowErrorMessage(val message: String): EditStageUiAction
}