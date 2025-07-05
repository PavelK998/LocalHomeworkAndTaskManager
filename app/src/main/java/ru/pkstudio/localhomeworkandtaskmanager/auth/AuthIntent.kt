package ru.pkstudio.localhomeworkandtaskmanager.auth

import androidx.lifecycle.Lifecycle

sealed interface AuthIntent {
    data class OnKeyboardClicked(val text: String) : AuthIntent

    data object OnThemeSelected : AuthIntent

    data object OnBackBtnClicked : AuthIntent

    data object SetDiaryUsage : AuthIntent

    data object SetTaskTrackerUsage : AuthIntent

    data class GetInitialData(val orientation: Int) : AuthIntent

    data class SendLifecycleInfo(val event: Lifecycle.Event) : AuthIntent

    data class SetThemeId(
        val themeId: Int,
        val isSystemInDarkMode: Boolean
    ) : AuthIntent
}