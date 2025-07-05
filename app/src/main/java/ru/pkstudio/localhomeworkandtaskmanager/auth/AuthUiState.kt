package ru.pkstudio.localhomeworkandtaskmanager.auth

import ru.pkstudio.localhomeworkandtaskmanager.auth.utils.AuthAction

data class AuthUiState(
    val text: String = "",
    val isCreatePin: Boolean = true,
    val isError: Boolean = false,
    val isSuccess: Boolean = false,
    val titleText: String = "",
    val isFirstLaunch: Boolean = false,
    val currentAuthAction: AuthAction = AuthAction.SET_PIN,
    val listUiThemes: List<String> = emptyList(),

    //greeting
    val isText1Visible: Boolean = false,
    val isText2Visible: Boolean = false,
    val isText3Visible: Boolean = false
)
