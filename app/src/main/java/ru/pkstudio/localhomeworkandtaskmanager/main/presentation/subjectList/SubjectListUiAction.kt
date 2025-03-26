package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.subjectList

sealed interface SubjectListUiAction {

    data object OpenDrawer : SubjectListUiAction

    data object CloseDrawer : SubjectListUiAction

}