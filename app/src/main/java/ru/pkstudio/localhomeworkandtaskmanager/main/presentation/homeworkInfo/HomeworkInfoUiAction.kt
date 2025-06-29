package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkInfo

sealed interface HomeworkInfoUiAction {

    data class ShowError(val text: String) : HomeworkInfoUiAction

    data object LaunchPhotoPicker : HomeworkInfoUiAction

    data object LaunchPathSelectorForSaveImages : HomeworkInfoUiAction

}