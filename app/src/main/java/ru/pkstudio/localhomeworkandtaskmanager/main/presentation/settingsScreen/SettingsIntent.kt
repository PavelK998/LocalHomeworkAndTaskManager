package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.settingsScreen

import android.net.Uri

sealed interface SettingsIntent {
    data object OnEditStagesClicked : SettingsIntent

    data object NavigateUp : SettingsIntent

    data object SetLightTheme : SettingsIntent

    data object SetDarkTheme : SettingsIntent

    data class SetSystemTheme(val isSystemInDarkMode: Boolean) : SettingsIntent

    data object SetDynamicColors : SettingsIntent

    data object SetMainScreen : SettingsIntent

    data object OnSetThemeClicked : SettingsIntent

    data object OnKanbanSettingsClicked : SettingsIntent

    data object OnChangePasswordClicked : SettingsIntent

    data class OnKeyboardClicked(val text: String) : SettingsIntent

    data object OnImportClicked: SettingsIntent

    data object ImportConfirmed: SettingsIntent

    data object OnExportClicked: SettingsIntent

    data object ExportConfirmed: SettingsIntent

    data object CloseImportDialog: SettingsIntent

    data object CloseExportDialog: SettingsIntent

    data class OnFileExportPathSelected(val uri: Uri): SettingsIntent

    data class OnFileImportPathSelected(val uri: Uri): SettingsIntent



}