package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.editStagesScreen

sealed interface EditStagesIntent {

    data object NavigateUp : EditStagesIntent

    data class OnStageNameChange (val index: Int, val name: String) : EditStagesIntent

    data class OnAddStageBtmClick (val position: Int) : EditStagesIntent

    data class OnDeleteStageBtmClick (val index: Int) : EditStagesIntent

    data object ConfirmDeleteStage : EditStagesIntent

    data object CloseDeleteDialog : EditStagesIntent

}