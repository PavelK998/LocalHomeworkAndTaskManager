package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.addHomework

import android.net.Uri
import androidx.compose.ui.graphics.Color

sealed interface AddHomeworkIntent {
    data class OnTitleHomeworkChange(val text:String): AddHomeworkIntent

    data class OnDescriptionHomeworkChange(val text:String): AddHomeworkIntent

    data class OnImagePicked(val uri: Uri): AddHomeworkIntent

    data class OnMultiplyImagePicked(val listUri: List<Uri>): AddHomeworkIntent

    data class OnDeleteImage(val index: Int): AddHomeworkIntent

    data object NavigateUp: AddHomeworkIntent

    data object OnNameChangeClick: AddHomeworkIntent

    data object CloseNameChangeCard: AddHomeworkIntent

    data object CloseDescriptionChangeCard: AddHomeworkIntent

    data object OnDescriptionChangeClick: AddHomeworkIntent

    data object Save: AddHomeworkIntent

    data object ToggleNameBold: AddHomeworkIntent

    data object ToggleNameItalic: AddHomeworkIntent

    data object ToggleNameLineThrough: AddHomeworkIntent

    data object ToggleNameUnderline: AddHomeworkIntent

    data object ToggleNameExtraOptions: AddHomeworkIntent

    data object ToggleDescriptionBold: AddHomeworkIntent

    data object ToggleDescriptionItalic: AddHomeworkIntent

    data object ToggleDescriptionLineThrough: AddHomeworkIntent

    data object ToggleDescriptionUnderline: AddHomeworkIntent

    data object ToggleDescriptionExtraOptions: AddHomeworkIntent

    data class NameFontSizeChange(val font: Int): AddHomeworkIntent

    data class DescriptionFontSizeChange(val font: Int): AddHomeworkIntent

    data object OpenImportanceColorDialog: AddHomeworkIntent

    data object CloseImportanceColorDialog: AddHomeworkIntent

    data object OpenStagePickerDialog: AddHomeworkIntent

    data object CloseStagePickerDialog: AddHomeworkIntent

    data object NavigateToEditStages: AddHomeworkIntent

    data class SelectStage(val index: Int): AddHomeworkIntent

    data class SelectImportanceColor(val color: Color): AddHomeworkIntent

    data object SelectDateTime: AddHomeworkIntent

    data object OpenDatePickerDialog: AddHomeworkIntent

    data object CloseDatePickerDialog: AddHomeworkIntent

    data object OpenTimePickerDialog: AddHomeworkIntent

    data object CloseTimePickerDialog: AddHomeworkIntent

    data class DatePicked(val dateFromEpochMillis: Long): AddHomeworkIntent

    data class TimePicked(val timeString: String): AddHomeworkIntent





}