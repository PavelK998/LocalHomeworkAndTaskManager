package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.subjectList

sealed interface SubjectListIntent {

    data class NavigateToHomeworkScreen(
        val subjectNane: String,
        val subjectId: Long
    ): SubjectListIntent

    data object OpenAddSubject: SubjectListIntent

    data object LogOutClicked: SubjectListIntent

    data object CloseAddSubject: SubjectListIntent


    data class ChangeNameSubject(val text: String): SubjectListIntent

    data object AddSubject: SubjectListIntent

    data class TurnEditModeOn(val index: Int): SubjectListIntent

    data class OnRevealCardOptionsMenuClicked(val index: Int, val isRevealed: Boolean): SubjectListIntent


    data class TurnEditModeOff(val index: Int) : SubjectListIntent

    data class DeleteSubject(val index: Int): SubjectListIntent

    data class EditSubject(val index: Int): SubjectListIntent

    data class OnEditTitleChanged(val text: String): SubjectListIntent

    data class OnEditCommentChanged(val text: String): SubjectListIntent


    data object OnSettingClicked: SubjectListIntent

}