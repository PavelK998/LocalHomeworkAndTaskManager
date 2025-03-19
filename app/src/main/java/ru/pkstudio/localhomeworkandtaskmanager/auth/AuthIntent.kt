package ru.pkstudio.localhomeworkandtaskmanager.auth

sealed interface AuthIntent {
    data class OnKeyboardClicked(val text: String) : AuthIntent
}