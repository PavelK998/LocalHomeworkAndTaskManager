package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkInfo

import com.mohamedrejeb.richeditor.model.RichTextState
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.StageModel
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList.uiModel.HomeworkUiModel
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.subjectList.uiModel.SubjectUiModel

data class HomeworkInfoState(
    val homeworkUiModel: HomeworkUiModel? = null,
    val subjectUiModel: SubjectUiModel? = null,
    val nameRichTextState: RichTextState = RichTextState(),
    val descriptionRichTextState: RichTextState = RichTextState(),
    val isLoading: Boolean = true,
    val subjectNameText: String = "",
    val currentSelectStageName: String = "",
    val addDateText: String = "",
    val isStageMenuOpened: Boolean = false,
    val isSettingsMenuOpened: Boolean = false,
    val isEditMode: Boolean = false,
    val stagesList: List<StageModel> = emptyList(),

    val homeworkEditName: String = "",
    val homeworkEditDescription: String = "",

    val isDeleteDialogOpened: Boolean = false,
    val deleteDialogTitle: String = "",
    val deleteDialogDescription: String = "",

    val isUpdateDialogOpened: Boolean = false,
    val updateDialogTitle: String = "",
    val updateDialogDescription: String = "",


    val isNameCardVisible: Boolean = false,
    val isDescriptionCardVisible: Boolean = false,
    val isNameExtraOptionsVisible: Boolean = false,
    val isDescriptionExtraOptionsVisible: Boolean = false,

    )