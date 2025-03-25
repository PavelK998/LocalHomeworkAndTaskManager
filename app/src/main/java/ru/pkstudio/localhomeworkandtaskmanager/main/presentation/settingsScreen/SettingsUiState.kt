package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.settingsScreen

data class SettingsState(
    val paramOne: String = "default",
    val paramTwo: List<String> = emptyList(),
)