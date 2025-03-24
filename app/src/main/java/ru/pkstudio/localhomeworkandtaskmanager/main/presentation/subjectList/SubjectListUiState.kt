package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.subjectList

import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.subjectList.uiModel.SubjectUiModel

data class SubjectListState(
    val paramOne: String = "default",
    val newSubjectName: String = "",
    val subjectNameForEdit: String = "",
    val subjectCommentForEdit: String = "",
    val subjectsList: List<SubjectUiModel> = emptyList(),
    val isScreenEmpty: Boolean = false,
    val isLoading: Boolean = true,
    val isAddSubjectAlertDialogOpened: Boolean = false,
)