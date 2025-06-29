package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.settingsScreen

data class SettingsState(
    val isDarkTheme: Boolean = false,
    val isDarkThemeBtnEnabled: Boolean = false,
    val isLightTheme: Boolean = false,
    val isLightThemeBtnEnabled: Boolean = false,
    val isSystemTheme: Boolean = false,
    val isDynamicColor: Boolean = false,
    val isDynamicColorAvailable: Boolean = false,
    val currentScreen: String = "",
    val toolbarTitle: String = "",
    val text: String = "",
    val isCreatePin: Boolean = true,
    val isError: Boolean = false,
    val isSuccess: Boolean = false,
    val titleText: String = "",
    val shouldEnterPassword: Boolean = true,
    val isExportAlertDialogOpened: Boolean = false,
    val isImportAlertDialogOpened: Boolean = false,

    val titleImportAlertDialog: String = "",
    val commentImportAlertDialog: String = "",

    val titleExportAlertDialog: String = "",
    val commentExportAlertDialog: String = "",

)