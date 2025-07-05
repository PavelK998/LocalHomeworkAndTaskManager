package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.addHomework

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.graphics.Color
import com.mohamedrejeb.richeditor.model.RichTextState
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.StageModel
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

data class AddHomeworkState(
    val isLoading: Boolean = false,
    val title: String = "",
    val titleFontSize: Float = 17f,
    val descriptionFontSize: Float = 17f,
    val description: String = "",
    val subjectName: String = "",
    val subjectId: String = "",
    val image: Bitmap? = null,
    val imagesList: List<Pair<Long, Bitmap>> = emptyList(),
    val isNameCardVisible: Boolean = false,
    val isDescriptionCardVisible: Boolean = false,
    val isNameExtraOptionsVisible: Boolean = false,
    val isDescriptionExtraOptionsVisible: Boolean = false,
    val nameRichTextState: RichTextState = RichTextState(),
    val descriptionRichTextState: RichTextState = RichTextState(),
    val currentStage: String = "",
    val imagesUriList: List<Uri> = emptyList(),
    val currentColor: Color = importance1,
    val isColorPickerVisible: Boolean = false,
    val isStagePickerVisible: Boolean = false,
    val isDatePickerVisible: Boolean = false,
    val isTimePickerVisible: Boolean = false,
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
    val stageList: List<StageModel> = emptyList(),
    val currentSelectedStage: StageModel? = null,
    val selectedFinishDate: String = "",
    val selectedFinishTime: String = "",
)