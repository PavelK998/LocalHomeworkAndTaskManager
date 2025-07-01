package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkInfo

import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamedrejeb.richeditor.model.RichTextState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.pkstudio.localhomeworkandtaskmanager.R
import ru.pkstudio.localhomeworkandtaskmanager.core.data.util.SingleSharedFlow
import ru.pkstudio.localhomeworkandtaskmanager.core.domain.manager.DeviceManager
import ru.pkstudio.localhomeworkandtaskmanager.core.domain.manager.ResourceManager
import ru.pkstudio.localhomeworkandtaskmanager.core.extensions.execute
import ru.pkstudio.localhomeworkandtaskmanager.core.navigation.Destination
import ru.pkstudio.localhomeworkandtaskmanager.core.navigation.Navigator
import ru.pkstudio.localhomeworkandtaskmanager.core.util.Constants
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toHomeworkModel
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toHomeworkUiModel
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toImportance
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toSubjectUiModel
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.HomeworkModel
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.StageModel
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository.FilesHandleRepository
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository.StageRepository
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository.SubjectsRepository
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class HomeworkInfoViewModel @Inject constructor(
    private val subjectsRepository: SubjectsRepository,
    private val stageRepository: StageRepository,
    private val resourceManager: ResourceManager,
    private val deviceManager: DeviceManager,
    private val filesHandleRepository: FilesHandleRepository,
    private val navigator: Navigator
) : ViewModel() {

    private val defaultNameState = RichTextState()
    private val defaultDescriptionState = RichTextState()
    private var defaultColor: Color? = null
    private var defaultStage: String = ""
    private var isUpdateClicked = false
    private var isDeleteClicked = false
    private var isNavigateToStageEdit = false
    private var isFontSizeSet = false
    private var isDateTimeSet = false
    private var localFinishDate: LocalDate? = null
    private var localFinishTime: LocalTime? = null
    private var folderString: String? = null


    fun parseArguments(homeworkId: String, subjectId: String) {
        _uiState.update {
            it.copy(
                homeworkId = homeworkId,
                subjectId = subjectId
            )
        }
        getInitialData(homeworkId = homeworkId, subjectId = subjectId)
        isNavigateToStageEdit = false
    }

    private val _uiAction = SingleSharedFlow<HomeworkInfoUiAction>()
    val uiAction = _uiAction.asSharedFlow()

    private val _uiState = MutableStateFlow(
        HomeworkInfoState(
            deleteDialogTitle = resourceManager.getString(R.string.delete_dialog_title),
            deleteDialogDescription = resourceManager.getString(R.string.delete_homework_dialog_description),
            updateDialogTitle = resourceManager.getString(R.string.update_info_dialog_title),
            updateDialogDescription = resourceManager.getString(R.string.update_info_dialog_comment),
        )
    )
    val uiState = _uiState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = HomeworkInfoState()
        )

    fun handleIntent(intent: HomeworkInfoIntent) {
        when (intent) {
            is HomeworkInfoIntent.OnStageSelectClick -> {
                if (_uiState.value.isStageMenuOpened) {
                    _uiState.update {
                        it.copy(
                            isStageMenuOpened = false
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isStageMenuOpened = true
                        )
                    }
                }
            }

            is HomeworkInfoIntent.OnHomeworkEditDescriptionChange -> {
                _uiState.update {
                    it.copy(
                        homeworkEditDescription = intent.text
                    )
                }
            }

            is HomeworkInfoIntent.OnHomeworkEditNameChange -> {
                _uiState.update {
                    it.copy(
                        homeworkEditName = intent.text
                    )
                }
            }

            is HomeworkInfoIntent.CloseDeleteAlertDialog -> {
                _uiState.update {
                    it.copy(
                        isDeleteDialogOpened = false
                    )
                }
            }

            is HomeworkInfoIntent.DeleteConfirm -> {
                if (!isDeleteClicked) {
                    isDeleteClicked = true
                    _uiState.value.homeworkUiModel?.let {
                        deleteHomework(it.toHomeworkModel())
                    }
                }

            }

            is HomeworkInfoIntent.OnDeleteBtnClick -> {
                isDeleteClicked = false
                _uiState.update {
                    it.copy(
                        isDeleteDialogOpened = true,
                        isSettingsMenuOpened = false
                    )
                }
            }

            is HomeworkInfoIntent.NavigateUp -> {
                if (
                    _uiState.value.nameRichTextState.annotatedString != defaultNameState.annotatedString
                    || _uiState.value.descriptionRichTextState.annotatedString != defaultDescriptionState.annotatedString
                    || _uiState.value.currentColor != defaultColor
                    || (_uiState.value.currentSelectedStage?.id ?: -1L) != defaultStage
                    || (isDateTimeSet)
                ) {
                    isUpdateClicked = false
                    _uiState.update {
                        it.copy(
                            isUpdateDialogOpened = true
                        )
                    }
                } else {
                    viewModelScope.launch {
                        navigator.navigateUp()
                    }
                }

            }

            is HomeworkInfoIntent.CloseSettingsMenu -> {
                _uiState.update {
                    it.copy(
                        isSettingsMenuOpened = false
                    )
                }
            }

            is HomeworkInfoIntent.OnSettingsClicked -> {
                _uiState.update {
                    it.copy(
                        isSettingsMenuOpened = true
                    )
                }
            }

            is HomeworkInfoIntent.ToggleNameBold -> {
                toggleBold(_uiState.value.nameRichTextState)
            }

            is HomeworkInfoIntent.ToggleNameItalic -> {
                toggleItalic(_uiState.value.nameRichTextState)
            }

            is HomeworkInfoIntent.ToggleNameLineThrough -> {
                toggleLineThrough(_uiState.value.nameRichTextState)
            }

            is HomeworkInfoIntent.DescriptionFontSizeChange -> {
                changeFontSize(
                    textState = _uiState.value.descriptionRichTextState,
                    fontSize = intent.font
                )
            }

            is HomeworkInfoIntent.NameFontSizeChange -> {
                changeFontSize(
                    textState = _uiState.value.nameRichTextState,
                    fontSize = intent.font
                )
            }

            is HomeworkInfoIntent.ToggleDescriptionExtraOptions -> {
                if (_uiState.value.isDescriptionExtraOptionsVisible) {
                    _uiState.update {
                        it.copy(
                            isDescriptionExtraOptionsVisible = false
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isDescriptionExtraOptionsVisible = true
                        )
                    }
                }
            }

            is HomeworkInfoIntent.ToggleNameExtraOptions -> {
                if (_uiState.value.isNameExtraOptionsVisible) {
                    _uiState.update {
                        it.copy(
                            isNameExtraOptionsVisible = false
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isNameExtraOptionsVisible = true
                        )
                    }
                }
            }

            is HomeworkInfoIntent.ToggleNameUnderline -> {
                toggleUnderlined(_uiState.value.nameRichTextState)
            }

            is HomeworkInfoIntent.ToggleDescriptionBold -> {
                toggleBold(_uiState.value.descriptionRichTextState)
            }

            is HomeworkInfoIntent.ToggleDescriptionItalic -> {
                toggleItalic(_uiState.value.descriptionRichTextState)
            }

            is HomeworkInfoIntent.ToggleDescriptionLineThrough -> {
                toggleLineThrough(_uiState.value.descriptionRichTextState)
            }

            is HomeworkInfoIntent.ToggleDescriptionUnderline -> {
                toggleUnderlined(_uiState.value.descriptionRichTextState)
            }

            is HomeworkInfoIntent.CloseDescriptionChangeCard -> {
                _uiState.update {
                    it.copy(
                        isDescriptionCardVisible = false
                    )
                }
            }

            is HomeworkInfoIntent.CloseNameChangeCard -> {
                _uiState.update {
                    it.copy(
                        isNameCardVisible = false
                    )
                }
            }

            is HomeworkInfoIntent.OnDescriptionChangeClick -> {
                _uiState.update {
                    it.copy(
                        isDescriptionCardVisible = true
                    )
                }
            }

            is HomeworkInfoIntent.OnNameChangeClick -> {
                _uiState.update {
                    it.copy(
                        isNameCardVisible = true
                    )
                }
            }

            is HomeworkInfoIntent.CloseUpdateAlertDialog -> {
                _uiState.update {
                    it.copy(
                        isUpdateDialogOpened = false
                    )
                }

            }

            is HomeworkInfoIntent.UpdateConfirm -> {
                if (!isUpdateClicked) {
                    isUpdateClicked = true
                    val endDate = if (localFinishDate != null && localFinishTime != null) {
                        LocalDateTime.of(
                            localFinishDate,
                            localFinishTime
                        )
                    } else null
                    _uiState.value.homeworkUiModel?.let {
                        updateHomework(
                            homeworkModel = it.toHomeworkModel(),
                            newName = _uiState.value.nameRichTextState.toHtml(),
                            newDescription = _uiState.value.descriptionRichTextState.toHtml(),
                            newImportanceColor = _uiState.value.currentColor,
                            newStageId = _uiState.value.currentSelectedStage?.id ?: "",
                            newStageName = _uiState.value.currentSelectedStage?.stageName ?: "",
                            endDate = endDate,
                            isFinished = _uiState.value.currentSelectedStage?.isFinishStage ?: false
                        )
                    }
                }
            }

            is HomeworkInfoIntent.UpdateDismiss -> {
                _uiState.update {
                    it.copy(
                        isUpdateDialogOpened = false
                    )
                }
                viewModelScope.launch {
                    navigator.navigateUp()
                }
            }

            is HomeworkInfoIntent.CloseImportanceColorDialog -> {
                _uiState.update {
                    it.copy(
                        isColorPickerVisible = false
                    )
                }
            }

            is HomeworkInfoIntent.CloseStagePickerDialog -> {
                _uiState.update {
                    it.copy(
                        isStagePickerVisible = false
                    )
                }
            }

            is HomeworkInfoIntent.OpenImportanceColorDialog -> {
                _uiState.update {
                    it.copy(
                        isColorPickerVisible = true
                    )
                }
            }

            is HomeworkInfoIntent.OpenStagePickerDialog -> {
                _uiState.update {
                    it.copy(
                        isStagePickerVisible = true
                    )
                }
            }

            is HomeworkInfoIntent.SelectImportanceColor -> {
                selectImportanceColor(intent.color)
            }

            is HomeworkInfoIntent.SelectStage -> {
                if (intent.index in _uiState.value.stageList.indices) {
                    selectStage(_uiState.value.stageList[intent.index])
                }

            }

            is HomeworkInfoIntent.NavigateToEditStages -> {
                if (!isNavigateToStageEdit) {
                    viewModelScope.launch {
                        navigator.navigate(Destination.StageEditScreen)
                    }
                    isNavigateToStageEdit = true
                }

            }

            is HomeworkInfoIntent.CloseDatePickerDialog -> {
                _uiState.update {
                    it.copy(
                        isDatePickerVisible = false
                    )
                }
            }

            is HomeworkInfoIntent.CloseTimePickerDialog -> {
                _uiState.update {
                    it.copy(
                        isTimePickerVisible = false
                    )
                }
            }

            is HomeworkInfoIntent.DatePicked -> {
                parseDate(intent.dateFromEpochMillis)
                _uiState.update {
                    it.copy(
                        isDatePickerVisible = false,
                        isTimePickerVisible = true
                    )
                }
            }

            is HomeworkInfoIntent.TimePicked -> {
                parseTime(intent.timeString)
            }

            is HomeworkInfoIntent.SelectDateTime -> {
                _uiState.update {
                    it.copy(
                        isDatePickerVisible = true
                    )
                }
            }

            is HomeworkInfoIntent.OnPhotoClicked -> {
                _uiState.update {
                    it.copy(
                        isPhotoUiVisible = true,
                        isPhotoOpened = true,
                        whichPhotoShouldBeOpenedFirst = intent.index
                    )
                }
            }

            is HomeworkInfoIntent.HandlePhotoUi -> {
                if (intent.isVisible) {
                    _uiState.update {
                        it.copy(
                            isPhotoUiVisible = false
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isPhotoUiVisible = true
                        )
                    }
                }
            }

            is HomeworkInfoIntent.ClosePhotoMode -> {
                _uiState.update {
                    it.copy(
                        isPhotoOpened = false,
                    )
                }
            }

            is HomeworkInfoIntent.DismissPhotoDropDownMenu -> {
                _uiState.update {
                    it.copy(
                        isDropDownMenuVisible = false
                    )
                }
            }

            is HomeworkInfoIntent.OnDeletePhotoClick -> {
                deletePhoto(intent.index)
            }

            is HomeworkInfoIntent.ExpandPhotoDropDownMenu -> {
                _uiState.update {
                    it.copy(
                        isDropDownMenuVisible = true
                    )
                }
            }

            is HomeworkInfoIntent.OnSelectMediaClick -> {
                viewModelScope.execute(
                    source = {
                        deviceManager.getFilePathUri()
                    },
                    onSuccess = { filePathString ->
                        if (filePathString.isNullOrBlank()) {
                            _uiState.update {
                                it.copy(
                                    isSelectFilePathDialogOpened = true
                                )
                            }
                        } else {
                            viewModelScope.launch {
                                val hasPermission =
                                    filesHandleRepository.checkUriPermission(folderUri = filePathString.toUri())
                                if (hasPermission) {
                                    _uiAction.tryEmit(HomeworkInfoUiAction.LaunchPhotoPicker)
                                    folderString = filePathString
                                } else {
                                    _uiState.update {
                                        it.copy(
                                            isSelectFilePathDialogOpened = true
                                        )
                                    }
                                }
                            }
                        }
                    }
                )
            }

            is HomeworkInfoIntent.OnFileExportPathSelected -> {
                viewModelScope.execute(
                    source = {
                        deviceManager.setFilePathUri(intent.uri.toString())
                    },
                    onSuccess = {
                        viewModelScope.launch {
                            val hasPermission =
                                filesHandleRepository.checkUriPermission(folderUri = intent.uri)
                            if (hasPermission) {
                                _uiAction.tryEmit(HomeworkInfoUiAction.LaunchPhotoPicker)
                                folderString = intent.uri.toString()
                            }
                        }
                    }
                )
            }

            is HomeworkInfoIntent.ConfirmPathSelect -> {
                _uiState.update {
                    it.copy(
                        isSelectFilePathDialogOpened = false
                    )
                }
                _uiAction.tryEmit(HomeworkInfoUiAction.LaunchPathSelectorForSaveImages)
            }

            is HomeworkInfoIntent.DismissPathSelectDialog -> {
                _uiState.update {
                    it.copy(
                        isSelectFilePathDialogOpened = false
                    )
                }
            }

            is HomeworkInfoIntent.OnMultiplyImagePicked -> {
                addPhotos(intent.listUri)
            }
        }
    }

    private fun addPhotos(listUri: List<Uri>) {
        if (listUri.isEmpty() || folderString.isNullOrBlank() || _uiState.value.homeworkUiModel == null) return
        viewModelScope.execute(
            source = {
                filesHandleRepository.uploadImageToUserFolderWithImageUriList(
                    folderUri = folderString!!.toUri(),
                    imageUriList = listUri
                )
            },
            onSuccess = { imageNames ->
                val names = _uiState.value.homeworkUiModel?.let {
                    it.imageNameList + imageNames
                } ?: imageNames
                updateImagesList(names)
                _uiState.update {
                    it.copy(
                        currentPagerPage = HomeworkInfoPagerState.MEDIA
                    )
                }
            },
            onError = {

            }
        )

    }


    private fun deletePhoto(index: Int) {
        if (_uiState.value.homeworkUiModel?.imageNameList?.indices?.contains(index) == true && !folderString.isNullOrBlank()) {
            _uiState.update {
                it.copy(
                    isDropDownMenuVisible = false
                )
            }
            viewModelScope.execute(
                source = {
                    filesHandleRepository.deleteImage(_uiState.value.homeworkUiModel?.imageNameList!![index])
                },
                onLoading = { isLoading ->
                    _uiState.update {
                        it.copy(
                            isLoading = isLoading
                        )
                    }
                },
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isPhotoOpened = false
                        )
                    }
                    val newImageList =
                        _uiState.value.homeworkUiModel?.imageNameList?.toMutableList()
                    Log.d("tertertertr", "deletePhoto: $newImageList")
                    newImageList?.removeAt(index)
                    Log.d("tertertertr", "after remove: $newImageList")
                    newImageList?.let {
                        updateImagesList(
                            imageNames = it
                        )
                    }
                },
                onError = {
                }
            )
        }
    }

    private fun updateImagesList(imageNames: List<String>) {
        _uiState.value.homeworkUiModel?.let { homeworkUiModel ->
            viewModelScope.execute(
                source = {
                    subjectsRepository.updateHomeworkInSubject(
                        subjectId = _uiState.value.subjectId,
                        homeworkModel = homeworkUiModel.toHomeworkModel().copy(
                            imageNameList = imageNames
                        )
                    )
                },
                onSuccess = {
                    _uiState.value.homeworkUiModel?.let {
                        findImages(imageNames)
                    }
//                    viewModelScope.execute(
//                        source = {
//                            homeworkRepository.getHomeworkById(homeworkId)
//                        },
//                        onSuccess = { homeworkModel ->
//                            val model = _uiState.value.homeworkUiModel?.copy(
//                                imageNameList = homeworkModel.imageNameList
//                            )
//                            _uiState.update {
//                                it.copy(
//                                    homeworkUiModel = model
//                                )
//                            }
//                            Log.d("hghfghfghfg", "updateImagesList: success")
//                            folderString?.let { folder ->
//                                if (folder.isNotBlank()) {
//                                    findImages(
//                                        folderUri = folder.toUri(),
//                                        imageNames
//                                    )
//                                }
//                            }
//                        },
//                        onError = {}
//                    )

                },
                onError = {

                }
            )
        }
    }

    private fun parseDate(dateFromEpochMillis: Long) {
        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val dateString = formatter.format(Date(dateFromEpochMillis))
        val localDate = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        _uiState.update {
            it.copy(
                selectedFinishDate = dateString
            )
        }
        localFinishDate = localDate
    }

    private fun parseTime(timeString: String) {
        val localTime =
            LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm"))
        _uiState.update {
            it.copy(
                selectedFinishTime = timeString
            )
        }
        localFinishTime = localTime
        isDateTimeSet = true
    }

    private fun toggleFontSize(nameFontSize: Float, descriptionFontSize: Float) {
        if (nameFontSize < Constants.MIN_FONT_VALUE) {
            _uiState.value.nameRichTextState.toggleSpanStyle(
                SpanStyle(fontSize = Constants.MIN_FONT_VALUE.roundToInt().sp)
            )
        }

        if (descriptionFontSize < Constants.MIN_FONT_VALUE) {
            _uiState.value.descriptionRichTextState.toggleSpanStyle(
                SpanStyle(fontSize = Constants.MIN_FONT_VALUE.roundToInt().sp)
            )
        }
    }

    private fun selectStage(model: StageModel) {
        _uiState.update {
            it.copy(
                currentSelectedStage = model,
                isStagePickerVisible = false
            )
        }
    }

    private fun selectImportanceColor(color: Color) {
        _uiState.update {
            it.copy(
                currentColor = color,
                isColorPickerVisible = false
            )
        }
    }

    private fun changeFontSize(textState: RichTextState, fontSize: Int) {
        if (textState.currentSpanStyle.fontSize != fontSize.sp) {
            textState.toggleSpanStyle(
                SpanStyle(fontSize = fontSize.sp)
            )
        }
    }

    private fun toggleBold(textState: RichTextState) {
        textState.toggleSpanStyle(
            SpanStyle(fontWeight = FontWeight.Bold)
        )
    }

    private fun toggleUnderlined(textState: RichTextState) {
        textState.toggleSpanStyle(
            SpanStyle(textDecoration = TextDecoration.Underline)
        )
    }

    private fun toggleItalic(textState: RichTextState) {
        textState.toggleSpanStyle(
            SpanStyle(fontStyle = FontStyle.Italic)
        )
    }

    private fun toggleLineThrough(textState: RichTextState) {
        textState.toggleSpanStyle(
            SpanStyle(textDecoration = TextDecoration.LineThrough)
        )
    }

    private fun deleteHomework(homeworkModel: HomeworkModel) {
        _uiState.update {
            it.copy(
                isLoading = true
            )
        }
        if (!folderString.isNullOrBlank()) {
            viewModelScope.execute(
                source = {
                    filesHandleRepository.deleteAllImages(namesList = homeworkModel.imageNameList)
                },
                onSuccess = {
                    viewModelScope.execute(
                        source = {
                            subjectsRepository.deleteHomeworkInSubject(
                                subjectId = _uiState.value.subjectId,
                                homeworkModel = homeworkModel
                            )
                        },
                        onSuccess = {
                            _uiState.update {
                                it.copy(
                                    isLoading = false
                                )
                            }
                            handleIntent(HomeworkInfoIntent.NavigateUp)
                        }
                    )
                }
            )
        } else {
            viewModelScope.execute(
                source = {
                    subjectsRepository.deleteHomeworkInSubject(
                        subjectId = _uiState.value.subjectId,
                        homeworkModel = homeworkModel
                    )
                },
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                    handleIntent(HomeworkInfoIntent.NavigateUp)
                }
            )
        }

    }

    private fun updateHomework(
        homeworkModel: HomeworkModel,
        newName: String,
        newDescription: String,
        newStageId: String,
        newStageName: String,
        endDate: LocalDateTime?,
        newImportanceColor: Color,
        isFinished: Boolean
    ) =
        viewModelScope.execute(
            source = {
                subjectsRepository.updateHomeworkInSubject(
                    subjectId = _uiState.value.subjectId,
                    homeworkModel = homeworkModel.copy(
                        name = newName,
                        description = newDescription,
                        stageId = newStageId,
                        stage = newStageName,
                        color = newImportanceColor.toArgb(),
                        importance = newImportanceColor.toImportance(),
                        endDate = endDate,
                        isFinished = isFinished
                    )
                )
            },
            onSuccess = {
                _uiState.update {
                    it.copy(
                        isUpdateDialogOpened = false
                    )
                }
                viewModelScope.launch {
                    navigator.navigateUp()
                }
            }
        )


    private fun getInitialData(homeworkId: String, subjectId: String) {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->

        }
        viewModelScope.launch(exceptionHandler) {
            _uiState.update {
                it.copy(
                    isLoading = true
                )
            }
            val folder = deviceManager.getFilePathUri()
            folderString = folder
            val subject = async {
                subjectsRepository.getSubjectById(subjectId)
            }
            val stages = async {
                stageRepository.getAllStagesSingleTime()
            }
            val stagesResult = stages.await()
            val subjectResult = subject.await()
            val homework = subjectResult.homework.find { it.id == homeworkId }?.toHomeworkUiModel()
            homework?.let { homeworkUiModel ->
                defaultNameState.setHtml(homeworkUiModel.name)
                defaultDescriptionState.setHtml(homeworkUiModel.description)
                defaultColor = Color(homeworkUiModel.color)
                defaultStage = homeworkUiModel.stageId
                _uiState.update {
                    it.copy(
                        nameRichTextState = _uiState.value.nameRichTextState.setHtml(homeworkUiModel.name),
                        descriptionRichTextState = _uiState.value.descriptionRichTextState.setHtml(
                            homeworkUiModel.description
                        ),
                        homeworkUiModel = homeworkUiModel,
                        addDateText = homeworkUiModel.addDate,
                        finishDateText = homeworkUiModel.endDate,
                        subjectUiModel = subjectResult.toSubjectUiModel(),
                        subjectNameText = subjectResult.subjectName,
                        stageList = stagesResult,
                        currentColor = Color(homeworkUiModel.color),
                        currentSelectedStage = stagesResult.find { it.id == homework.stageId },
                        isLoading = false
                    )
                }
            }
            if (!isFontSizeSet) {
                val nameFontSize =
                    if (_uiState.value.nameRichTextState.currentSpanStyle.fontSize.isSp) {
                        _uiState.value.nameRichTextState.currentSpanStyle.fontSize.value
                    } else {
                        0f
                    }
                val descriptionFontSize =
                    if (_uiState.value.descriptionRichTextState.currentSpanStyle.fontSize.isSp) {
                        _uiState.value.descriptionRichTextState.currentSpanStyle.fontSize.value
                    } else {
                        0f
                    }
                toggleFontSize(
                    nameFontSize = nameFontSize,
                    descriptionFontSize = descriptionFontSize
                )
                isFontSizeSet = true

                if (!folder.isNullOrBlank() && homework != null && homework.imageNameList.isNotEmpty()) {
                    findImages(
                        imageNames = homework.imageNameList
                    )
                }
                homeworkListener(subjectId, homeworkId)
            }
        }
    }

    private fun homeworkListener(subjectId: String, homeworkId: String) {
        viewModelScope.execute(
            source = {
                subjectsRepository.getSubjectByIdFlow(subjectId = subjectId)
            },
            onSuccess = { subjectFlow ->
                viewModelScope.launch {
                    subjectFlow.collect { subjectModel ->
                        _uiState.update {
                            it.copy(
                                homeworkUiModel = subjectModel.homework.find { it.id == homeworkId }
                                    ?.toHomeworkUiModel()
                            )
                        }
                    }
                }
            }
        )
    }

    private fun findImages(imageNames: List<String>) = viewModelScope.execute(
        source = {
            filesHandleRepository.findImagesInFolder(
                namesList = imageNames
            )
        },
        onSuccess = { bitmapList ->
            Log.d("asdasdasdasd", "findImages: $bitmapList")
            _uiState.update {
                it.copy(
                    photoBitmapList = bitmapList
                )
            }
        },
        onError = {
            Log.d("asdasdasdasd", "findImages error $it")
        }
    )
}