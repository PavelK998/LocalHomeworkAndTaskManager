package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.editStagesScreen

import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.StageModel

data class EditStagesState(
    val paramOne: String = "default",
    val stagesList: List<StageModel> = emptyList(),
)