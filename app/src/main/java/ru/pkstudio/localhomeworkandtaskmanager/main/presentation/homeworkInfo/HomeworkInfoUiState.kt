package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkInfo

import android.net.Uri
import androidx.compose.ui.graphics.Color
import com.mohamedrejeb.richeditor.model.RichTextState
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.StageModel
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList.uiModel.HomeworkUiModel
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.subjectList.uiModel.SubjectUiModel
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

data class HomeworkInfoState(
    val homeworkUiModel: HomeworkUiModel? = null,
    val subjectUiModel: SubjectUiModel? = null,
    val nameRichTextState: RichTextState = RichTextState(),
    val descriptionRichTextState: RichTextState = RichTextState(),
    val isLoading: Boolean = true,
    val subjectNameText: String = "",
    val addDateText: String = "",
    val finishDateText: String = "",
    val isStageMenuOpened: Boolean = false,
    val isSettingsMenuOpened: Boolean = false,
    val isEditMode: Boolean = false,

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

    val stageList: List<StageModel> = emptyList(),
    val currentSelectedStage: StageModel? = null,
    val colorList: List<Color> = listOf(
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
    val currentColor: Color = importance1,
    val isColorPickerVisible: Boolean = false,
    val isStagePickerVisible: Boolean = false,
    val isTimePickerVisible: Boolean = false,
    val isDatePickerVisible: Boolean = false,
    val selectedFinishDate: String = "",
    val selectedFinishTime: String = "",
    val currentPagerPage: HomeworkInfoPagerState = HomeworkInfoPagerState.MAIN,
    val photoList: List<Uri> = emptyList(),
    val isPhotoOpened: Boolean = false,
    val whichPhotoShouldBeOpenedFirst: Int = 0,
    val isPhotoUiVisible: Boolean = false,
    val isDropDownMenuVisible: Boolean = false,
)
