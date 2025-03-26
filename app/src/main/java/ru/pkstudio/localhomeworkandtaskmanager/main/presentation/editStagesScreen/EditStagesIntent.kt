package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.editStagesScreen

import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.StageModel

sealed interface EditStagesIntent {

    data object NavigateUp : EditStagesIntent

    data class OnStageNameChange (val index: Int, val name: String) : EditStagesIntent

    data class OnAddStageBtmClick (val position: Int) : EditStagesIntent

    data class OnDeleteStageBtmClick (val stage: StageModel) : EditStagesIntent

}