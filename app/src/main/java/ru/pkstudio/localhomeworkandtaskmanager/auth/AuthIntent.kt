package ru.pkstudio.localhomeworkandtaskmanager.auth

sealed interface AuthIntent {
    data class OnKeyboardClicked(val text: String) : AuthIntent

    data object OnThemeSelected : AuthIntent

    data object OnBackBtnClicked : AuthIntent


    data object SetLightTheme : AuthIntent

    data object SetDarkTheme : AuthIntent

    data object SetSystemTheme : AuthIntent

    data object SetDynamicColors : AuthIntent

    data object SetDiaryUsage : AuthIntent

    data object SetTaskTrackerUsage : AuthIntent

    data class GetInitialData(val orientation: Int) : AuthIntent

    data class SetThemeId(
        val themeId: Int,
        val isSystemInDarkMode: Boolean
    ) : AuthIntent
}