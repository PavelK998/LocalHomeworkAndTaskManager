package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList

import androidx.compose.ui.graphics.Color
import ru.pkstudio.localhomeworkandtaskmanager.core.components.kanban.KanbanItem
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.StageModel
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList.uiModel.HomeworkUiModel
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList.uiModel.StageUiModel
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.importance1
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.importance10
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.importance2
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.importance3
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.importance4
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.importance5
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.importance6
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.importance7
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.importance8
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.importance9

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

    val isSortBottomSheetOpen: Boolean = false,
    val isSortAdd: Boolean = false,
    val isSortImportance: Boolean = false,
    val isSortAddAscending: Boolean = false,
    val isSortImportanceAscending: Boolean = false,


    val isColorPaletteDialogOpen: Boolean = false,


    val colorList: List<Color> =  listOf(
        importance1,
        importance2,
        importance3,
        importance4,
        importance5,
        importance6,
        importance7,
        importance8,
        importance9,
        importance10,
    ),
)