package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.subjectList

sealed interface SubjectListUiAction {
    data class ShowErrorMessage(val message: String) : SubjectListUiAction
}
