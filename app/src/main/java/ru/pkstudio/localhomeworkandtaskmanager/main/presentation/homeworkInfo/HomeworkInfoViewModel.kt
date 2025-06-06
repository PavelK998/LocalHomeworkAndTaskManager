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
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository.HomeworkRepository
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
    private val homeworkRepository: HomeworkRepository,
    private val subjectsRepository: SubjectsRepository,
    private val stageRepository: StageRepository,
    private val resourceManager: ResourceManager,
    private val deviceManager: DeviceManager,
    private val filesHandleRepository: FilesHandleRepository,
    private val navigator: Navigator
) : ViewModel() {
    private var homeworkId: Long = 0L

    private val defaultNameState = RichTextState()
    private val defaultDescriptionState = RichTextState()
    private var defaultColor: Color? = null
    private var defaultStage: Long = 0L
    private var isUpdateClicked = false
    private var isDeleteClicked = false
    private var isNavigateToStageEdit = false
    private var isFontSizeSet = false
    private var isDateTimeSet = false
    private var localFinishDate: LocalDate? = null
    private var localFinishTime: LocalTime? = null
    private var folderString: String? = null
    private var imageNamesList: MutableList<String> = mutableListOf()


    fun parseArguments(homeworkId: Long, subjectId: Long) {
        this.homeworkId = homeworkId
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
                            newStageId = _uiState.value.currentSelectedStage?.id ?: 0L,
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

            is HomeworkInfoIntent.OnAttachFileClicked -> {
                _uiAction.tryEmit(HomeworkInfoUiAction.LaunchPhotoPicker)
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
            }
        )

    }


    private fun deletePhoto(index: Int) {
        if (index in imageNamesList.indices && !folderString.isNullOrBlank()) {
            viewModelScope.execute(
                source = {
                    filesHandleRepository.deleteImageInUserFolder(
                        folderUri = folderString!!.toUri(),
                        imageName = imageNamesList[index]
                    )
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
                    imageNamesList.removeAt(index)
                    updateImagesList(
                        imageNames = imageNamesList
                    )
                }
            )
        }
    }

    private fun updateImagesList(imageNames: List<String>) {
        Log.d("hghfghfghfg", "names size in update: ${imageNames.size}")
        _uiState.value.homeworkUiModel?.let { homeworkUiModel ->
            viewModelScope.execute(
                source = {
                    homeworkRepository.updateHomework(
                        homework = homeworkUiModel.toHomeworkModel().copy(
                            imageNameList = imageNames
                        )
                    )
                },
                onSuccess = {
                    Log.d("hghfghfghfg", "updateImagesList: success")
                    folderString?.let { folder ->
                        if (folder.isNotBlank()) {
                            findImages(
                                folderUri = folder.toUri(),
                                imageNames
                            )
                        }
                    }
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
            Log.d("nvbnbvvbnbn", "min value")
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
        Log.d("vbnbvnvbnvbnvb", " state size: ${textState.currentSpanStyle.fontSize} ")
        Log.d("vbnbvnvbnvbnvb", " font: $fontSize ")
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
        folderString?.let { folder ->
            if (folder.isNotBlank()) {
                viewModelScope.execute(
                    source = {
                        filesHandleRepository.deleteAllImagesInUserFolder(
                            folderUri = folder.toUri(),
                            namesList = homeworkModel.imageNameList
                        )
                    },
                    onSuccess = {
                        viewModelScope.execute(
                            source = {
                                homeworkRepository.deleteHomework(homeworkModel)
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
            }
        }


    }

    private fun updateHomework(
        homeworkModel: HomeworkModel,
        newName: String,
        newDescription: String,
        newStageId: Long,
        newStageName: String,
        endDate: LocalDateTime?,
        newImportanceColor: Color,
        isFinished: Boolean
    ) =
        viewModelScope.execute(
            source = {
                Log.d("gdfhfghfgh", "color:$newImportanceColor")
                Log.d("gdfhfghfgh", "stage:$newStageName")
                homeworkRepository.updateHomework(
                    homework = homeworkModel.copy(
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


    private fun getInitialData(homeworkId: Long, subjectId: Long) {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            Log.d("jdgfhfghfgfghjhg", "getInitialData error: $throwable")
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
            val homework = async {
                homeworkRepository.getHomeworkById(homeworkId)
            }
            val stages = async {
                stageRepository.getAllStagesSingleTime()
            }

            val homeworkResult = homework.await().toHomeworkUiModel()
            val stagesResult = stages.await()
            val subjectResult = subject.await().toSubjectUiModel()
            Log.d("nbcvnvbnbvn", "getInitialData: stages $stagesResult")
            defaultNameState.setHtml(homeworkResult.name)
            defaultDescriptionState.setHtml(homeworkResult.description)
            defaultColor = Color(homeworkResult.color)
            defaultStage = homeworkResult.stageId
            _uiState.update {
                it.copy(
                    nameRichTextState = _uiState.value.nameRichTextState.setHtml(homeworkResult.name),
                    descriptionRichTextState = _uiState.value.descriptionRichTextState.setHtml(
                        homeworkResult.description
                    ),
                    homeworkUiModel = homeworkResult,
                    addDateText = homeworkResult.addDate,
                    finishDateText = homeworkResult.endDate,
                    subjectUiModel = subjectResult,
                    subjectNameText = subjectResult.subjectName,
                    stageList = stagesResult,
                    currentColor = Color(homeworkResult.color),
                    currentSelectedStage = if (stagesResult.isNotEmpty()) {
                        stagesResult.find { stage -> stage.id == homeworkResult.stageId }
                    } else null,
                    isLoading = false
                )
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
                imageNamesList.addAll(homeworkResult.imageNameList)
                if (!folder.isNullOrBlank() && homeworkResult.imageNameList.isNotEmpty()) {
                    findImages(
                        folderUri = folder.toUri(),
                        imageNames = homeworkResult.imageNameList
                    )
                }
            }
        }
    }

    private fun findImages(folderUri: Uri, imageNames: List<String>) = viewModelScope.execute(
        source = {
            filesHandleRepository.findImagesInUserFolder(
                folderUri = folderUri,
                namesList = imageNames
            )
        },
        onSuccess = { uriList ->
            _uiState.update {
                it.copy(
                    photoList = uriList
                )
            }
        },
        onError = {
            Log.d("nbnbvnvbnvbn", "findImageSInUserFolder: error $it")
        }
    )
}