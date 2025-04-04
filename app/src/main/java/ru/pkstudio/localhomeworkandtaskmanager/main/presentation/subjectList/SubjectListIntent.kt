package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.subjectList

import android.net.Uri

sealed interface SubjectListIntent {

    data class NavigateToHomeworkScreen(
        val subjectNane: String,
        val subjectId: Long
    ): SubjectListIntent

    data object OpenAddSubject: SubjectListIntent

    data object CloseDeleteDialog: SubjectListIntent

    data object CloseImportDialog: SubjectListIntent

    data object LogOutClicked: SubjectListIntent

    data object CloseAddSubject: SubjectListIntent

    data object OpenDrawer: SubjectListIntent

    data object CloseDrawer: SubjectListIntent


    data class ChangeNameSubject(val text: String): SubjectListIntent

    data class ChangeCommentSubject(val text: String): SubjectListIntent

    data object AddSubject: SubjectListIntent

    data class TurnEditModeOn(val index: Int): SubjectListIntent

    data class OnRevealCardOptionsMenuClicked(val index: Int, val isRevealed: Boolean): SubjectListIntent


    data class TurnEditModeOff(val index: Int) : SubjectListIntent

    data object ConfirmDeleteSubject: SubjectListIntent

    data class DeleteSubject(val index: Int): SubjectListIntent

    data class EditSubject(val index: Int): SubjectListIntent

    data class OnEditTitleChanged(val text: String): SubjectListIntent

    data class OnEditCommentChanged(val text: String): SubjectListIntent


    data object OnSettingClicked: SubjectListIntent

    data object OnImportClicked: SubjectListIntent

    data object ImportConfirmed: SubjectListIntent

    data object OnExportClicked: SubjectListIntent

    data class OnFileExportPathSelected(val uri: Uri): SubjectListIntent

    data class OnFileImportPathSelected(val uri: Uri): SubjectListIntent

}