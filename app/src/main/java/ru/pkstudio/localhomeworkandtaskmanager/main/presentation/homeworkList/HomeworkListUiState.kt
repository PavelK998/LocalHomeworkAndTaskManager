package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList

data class HomeworkListState(
    //val homeworkList: List<HomeworkUiModel> = emptyList(),
    val subjectName: String = "",
    val subjectId: String = "",
    val isEditModeEnabled: Boolean = false,
    val isDropDownMenuVisible: Boolean = false,
    //val kanbanItemsList: List<KanbanItem<StageUIModel, HomeworkUiModel>> = emptyList(),
    val isLoading: Boolean = true,
    val isKanbanScreenVisible: Boolean = true,
   // val segmentedButtonOptions: List<String>,
    val segmentedButtonSelectedIndex: Int = 0,
    val isScreenEmpty: Boolean = false,
    val numberOfCheckedCards: Int = 0
)