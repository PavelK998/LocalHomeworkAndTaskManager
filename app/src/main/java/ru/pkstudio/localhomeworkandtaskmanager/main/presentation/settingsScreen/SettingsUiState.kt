package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.settingsScreen

data class SettingsState(
    val isDarkTheme: Boolean = false,
    val isDarkThemeBtnEnabled: Boolean = false,
    val isLightTheme: Boolean = false,
    val isLightThemeBtnEnabled: Boolean = false,
    val isSystemTheme: Boolean = false,
    val isDynamicColor: Boolean = false,
    val isDynamicColorAvailable: Boolean = false,
)