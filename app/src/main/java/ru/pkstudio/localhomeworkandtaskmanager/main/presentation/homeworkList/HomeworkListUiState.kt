package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList

import ru.pkstudio.localhomeworkandtaskmanager.core.components.kanban.KanbanItem
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.StageModel
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList.uiModel.HomeworkUiModel
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList.uiModel.StageUiModel

data class HomeworkListState(
    val homeworkList: List<HomeworkUiModel> = emptyList(),
    val stageUiModelList: List<StageModel> = emptyList(),
    val subjectName: String = "",
    val subjectId: String = "",
    val isEditModeEnabled: Boolean = false,
    val isFABVisible: Boolean = true,
    val isDropDownMenuVisible: Boolean = false,
    val kanbanItemsList: List<KanbanItem<StageUiModel, HomeworkUiModel>> = emptyList(),
    val isLoading: Boolean = true,
    val isKanbanScreenVisible: Boolean = false,
    val segmentedButtonOptions: List<String> = emptyList(),
    val segmentedButtonSelectedIndex: Int = 0,
    val isScreenEmpty: Boolean = false,
    val numberOfCheckedCards: Int = 0,

    val isDeleteDialogOpen: Boolean = false,
    val deleteDialogTitle: String = "",
    val deleteDialogDescription: String = "",
)