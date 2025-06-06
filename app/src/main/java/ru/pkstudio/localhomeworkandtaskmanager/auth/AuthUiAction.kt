package ru.pkstudio.localhomeworkandtaskmanager.auth

sealed interface AuthUiAction {

    data object SetLightTheme : AuthUiAction

    data object SetDarkTheme : AuthUiAction

    data object ToggleDynamicColors : AuthUiAction

    data object FinishActivity : AuthUiAction

    data object SetSystemTheme : AuthUiAction

    data object GetInitialData : AuthUiAction

    data object SetUnspecifiedOrientation : AuthUiAction

}