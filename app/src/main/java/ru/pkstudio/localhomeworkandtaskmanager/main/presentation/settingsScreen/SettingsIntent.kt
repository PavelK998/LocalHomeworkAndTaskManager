package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.settingsScreen

sealed interface SettingsIntent {
    data object OnEditStagesClicked : SettingsIntent

    data object NavigateUp : SettingsIntent

    data object SetLightTheme : SettingsIntent

    data object SetDarkTheme : SettingsIntent

    data class SetSystemTheme(val isSystemInDarkMode: Boolean) : SettingsIntent

    data object SetDynamicColors : SettingsIntent

}