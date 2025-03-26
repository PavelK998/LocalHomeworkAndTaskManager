package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.settingsScreen

sealed interface SettingsIntent {
    data object OnEditStagesClicked : SettingsIntent

    data object NavigateUp : SettingsIntent
}