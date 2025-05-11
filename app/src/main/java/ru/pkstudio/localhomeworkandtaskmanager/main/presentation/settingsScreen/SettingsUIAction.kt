package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.settingsScreen

sealed interface SettingsUIAction {

    data class ShowError(val message: String) : SettingsUIAction

    data object SetLightTheme : SettingsUIAction

    data object SetDarkTheme : SettingsUIAction

    data class SetSystemTheme(val isSystemInDarkMode: Boolean) : SettingsUIAction

    data object SetDynamicColors : SettingsUIAction

}