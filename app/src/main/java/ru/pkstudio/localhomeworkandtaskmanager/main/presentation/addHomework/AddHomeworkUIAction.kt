package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.addHomework

sealed interface AddHomeworkUIAction {
    data class ShowError(val text: String) : AddHomeworkUIAction

    data object LaunchPhotoPicker : AddHomeworkUIAction
}