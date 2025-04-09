package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.subjectList

import androidx.compose.runtime.Immutable
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.subjectList.uiModel.SubjectUiModel

@Immutable
data class SubjectListState(
    val toolbarTitle: String = "",
    val titleAddDialog: String = "",
    val newSubjectName: String = "",
    val newSubjectComment: String = "",
    val subjectNameForEdit: String = "",
    val subjectCommentForEdit: String = "",
    val subjectsList: List<SubjectUiModel> = emptyList(),
    val isScreenEmpty: Boolean = false,
    val isLoading: Boolean = true,
    val isAddSubjectAlertDialogOpened: Boolean = false,

    val isDeleteAlertDialogOpened: Boolean = false,
    val titleDeleteAlertDialog: String = "",
    val commentDeleteAlertDialog: String = "",

    val isImportAlertDialogOpened: Boolean = false,
    val titleImportAlertDialog: String = "",
    val commentImportAlertDialog: String = "",

    val isExportAlertDialogOpened: Boolean = false,
    val titleExportAlertDialog: String = "",
    val commentExportAlertDialog: String = "",

    val isVideoPlayerViewVisible: Boolean = true,
)