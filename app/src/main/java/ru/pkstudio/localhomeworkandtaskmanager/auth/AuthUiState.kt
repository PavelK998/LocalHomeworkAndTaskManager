package ru.pkstudio.localhomeworkandtaskmanager.auth

data class AuthUiState(
    val text: String = "",
    val isCreatePin: Boolean = true,
    val isError: Boolean = false,
    val isSuccess: Boolean = false,
    val titleText: String = ""
)
