package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.addHomework

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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onStart
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
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toImportance
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.HomeworkModel
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.StageModel
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository.FilesHandleRepository
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository.HomeworkRepository
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository.StageRepository
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
class AddHomeworkViewModel @Inject constructor(
    private val navigator: Navigator,
    private val homeworkRepository: HomeworkRepository,
    private val stageRepository: StageRepository,
    private val resourceManager: ResourceManager,
    private val deviceManager: DeviceManager,
    private val filesHandleRepository: FilesHandleRepository,
) : ViewModel() {


    private var subjectId: Long = 0
    private var isFontSizeSet = false

    private var stageId = 0L
    private var stageName = ""
    private var folderUri = ""
    private var isNavigateUp = false
    private var isNavigateToEditStages = false
    private var localFinishDate: LocalDate? = null
    private var localFinishTime: LocalTime? = null


    fun parseArguments(subjectId: Long) {
        this.subjectId = subjectId
    }

    private val _uiState = MutableStateFlow(AddHomeworkState())
    val uiState = _uiState
        .onStart {
            isNavigateToEditStages = false
            getStages()
            if (!isFontSizeSet) {
                toggleFontSize(
                    nameFontSize = 24,
                    descriptionFontSize = 16
                )
                isFontSizeSet = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = AddHomeworkState()
        )

    private val _uiAction = SingleSharedFlow<AddHomeworkUIAction>()
    val uiAction = _uiAction.asSharedFlow()

    fun handleIntent(intent: AddHomeworkIntent) {
        when (intent) {
            is AddHomeworkIntent.NavigateUp -> {
                if (!isNavigateUp) {
                    isNavigateUp = true
                    navigateUp()
                }

            }

            is AddHomeworkIntent.OnDescriptionHomeworkChange -> {
                _uiState.update {
                    it.copy(
                        description = intent.text
                    )
                }
            }

            is AddHomeworkIntent.OnImagePicked -> {

            }

            is AddHomeworkIntent.OnMultiplyImagePicked -> {
                    _uiState.update {
                        it.copy(
                            imagesUriList = intent.listUri
                        )
                    }
                //parseMultiplyImageToListBitmap(listUri = intent.listUri)
            }

            is AddHomeworkIntent.OnTitleHomeworkChange -> {
                _uiState.update {
                    it.copy(
                        title = intent.text
                    )
                }
            }

            is AddHomeworkIntent.Save -> {
                if (subjectId != 0L) {
                    if (_uiState.value.nameRichTextState.annotatedString.text.isNotBlank()) {
                        addHomework(
                            subjectId = subjectId,
                            folderUri = folderUri
                        )
                    } else {
                        _uiAction.tryEmit(
                            AddHomeworkUIAction.ShowError(resourceManager.getString(R.string.empty_homework_title))
                        )
                    }

                }
            }

            is AddHomeworkIntent.OnDescriptionChangeClick -> {
                _uiState.update {
                    it.copy(
                        isDescriptionCardVisible = true
                    )
                }
            }

            is AddHomeworkIntent.OnNameChangeClick -> {
                _uiState.update {
                    it.copy(
                        isNameCardVisible = true
                    )
                }
            }

            is AddHomeworkIntent.CloseDescriptionChangeCard -> {
                _uiState.update {
                    it.copy(
                        isDescriptionCardVisible = false
                    )
                }
            }

            is AddHomeworkIntent.CloseNameChangeCard -> {
                _uiState.update {
                    it.copy(
                        isNameCardVisible = false
                    )
                }
            }

            is AddHomeworkIntent.OnDeleteImage -> {
                deleteImage(intent.index)
            }

            is AddHomeworkIntent.ToggleNameBold -> {
                toggleBold(_uiState.value.nameRichTextState)
            }

            is AddHomeworkIntent.ToggleNameItalic -> {
                toggleItalic(_uiState.value.nameRichTextState)
            }

            is AddHomeworkIntent.ToggleNameLineThrough -> {
                toggleLineThrough(_uiState.value.nameRichTextState)
            }

            is AddHomeworkIntent.DescriptionFontSizeChange -> {
                _uiState.value.descriptionRichTextState.toggleSpanStyle(
                    SpanStyle(fontSize = intent.font.sp)
                )
            }

            is AddHomeworkIntent.NameFontSizeChange -> {
                changeFontSize(
                    textState = _uiState.value.nameRichTextState,
                    fontSize = intent.font
                )
            }

            is AddHomeworkIntent.ToggleDescriptionExtraOptions -> {
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

            is AddHomeworkIntent.ToggleNameExtraOptions -> {
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

            is AddHomeworkIntent.ToggleNameUnderline -> {
                toggleUnderlined(_uiState.value.nameRichTextState)
            }

            is AddHomeworkIntent.ToggleDescriptionBold -> {
                toggleBold(_uiState.value.descriptionRichTextState)
            }

            is AddHomeworkIntent.ToggleDescriptionItalic -> {
                toggleItalic(_uiState.value.descriptionRichTextState)
            }

            is AddHomeworkIntent.ToggleDescriptionLineThrough -> {
                toggleLineThrough(_uiState.value.descriptionRichTextState)
            }

            is AddHomeworkIntent.ToggleDescriptionUnderline -> {
                toggleUnderlined(_uiState.value.descriptionRichTextState)
            }

            is AddHomeworkIntent.CloseImportanceColorDialog -> {
                _uiState.update {
                    it.copy(
                        isColorPickerVisible = false
                    )
                }
            }

            is AddHomeworkIntent.CloseStagePickerDialog -> {
                _uiState.update {
                    it.copy(
                        isStagePickerVisible = false
                    )
                }
            }

            is AddHomeworkIntent.OpenImportanceColorDialog -> {
                _uiState.update {
                    it.copy(
                        isColorPickerVisible = true
                    )
                }
            }

            is AddHomeworkIntent.OpenStagePickerDialog -> {
                _uiState.update {
                    it.copy(
                        isStagePickerVisible = true
                    )
                }
            }

            is AddHomeworkIntent.SelectImportanceColor -> {
                selectImportanceColor(intent.color)
            }

            is AddHomeworkIntent.SelectStage -> {
                if (intent.index in _uiState.value.stageList.indices) {
                    selectStage(_uiState.value.stageList[intent.index])
                }

            }

            is AddHomeworkIntent.NavigateToEditStages -> {
                if (!isNavigateToEditStages) {
                    viewModelScope.launch {
                        _uiState.update {
                            it.copy(
                                isStagePickerVisible = false
                            )
                        }
                        delay(200)
                        isNavigateToEditStages = true
                        navigator.navigate(
                            Destination.StageEditScreen
                        )
                    }
                }
            }

            is AddHomeworkIntent.CloseDatePickerDialog -> {
                _uiState.update {
                    it.copy(
                        isDatePickerVisible = false
                    )
                }
            }

            is AddHomeworkIntent.CloseTimePickerDialog -> {
                _uiState.update {
                    it.copy(
                        isTimePickerVisible = false
                    )
                }
            }

            is AddHomeworkIntent.DatePicked -> {
                parseDate(intent.dateFromEpochMillis)
                _uiState.update {
                    it.copy(
                        isDatePickerVisible = false,
                        isTimePickerVisible = true
                    )
                }
            }

            is AddHomeworkIntent.OpenDatePickerDialog -> {
                _uiState.update {
                    it.copy(
                        isDatePickerVisible = true
                    )
                }
            }

            is AddHomeworkIntent.OpenTimePickerDialog -> {
                _uiState.update {
                    it.copy(
                        isTimePickerVisible = true
                    )
                }
            }

            is AddHomeworkIntent.TimePicked -> {
                parseTime(intent.timeString)
            }

            is AddHomeworkIntent.SelectDateTime -> {
                _uiState.update {
                    it.copy(
                        isDatePickerVisible = true
                    )
                }
            }

            is AddHomeworkIntent.OnSelectMediaClick -> {
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
                                    _uiAction.tryEmit(AddHomeworkUIAction.LaunchPhotoPicker)
                                    folderUri = filePathString
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

            is AddHomeworkIntent.OnFileExportPathSelected -> {
                viewModelScope.execute(
                    source = {
                        deviceManager.setFilePathUri(intent.uri.toString())
                    },
                    onSuccess = {
                        _uiAction.tryEmit(AddHomeworkUIAction.LaunchPhotoPicker)
                    }
                )
            }

            is AddHomeworkIntent.ConfirmPathSelect -> {
                _uiState.update {
                    it.copy(
                        isSelectFilePathDialogOpened = false
                    )
                }
                _uiAction.tryEmit(AddHomeworkUIAction.LaunchPathSelectorForSaveImages)
            }

            is AddHomeworkIntent.DismissPathSelectDialog -> {
                _uiState.update {
                    it.copy(
                        isSelectFilePathDialogOpened = false
                    )
                }
            }
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

    private fun deleteImage(index: Int) {
        val imagesList = _uiState.value.imagesList.toMutableList()
        if (index in imagesList.indices) {
            imagesList.removeAt(index)
            _uiState.update {
                it.copy(
                    imagesList = imagesList
                )
            }
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

    private fun getStages() = viewModelScope.execute(
        source = {
            stageRepository.getAllStages()
        },
        onSuccess = { stageFlow ->

            viewModelScope.launch {
                stageFlow.collect { stageList ->
                    Log.d("tryrtyrtyrtyrt", "getStages: $stageList")
                    _uiState.update {
                        it.copy(
                            stageList = stageList
                        )
                    }
                    if (stageList.isNotEmpty()) {
                        _uiState.update {
                            it.copy(
                                currentSelectedStage = stageList[0]
                            )
                        }
                        stageId = stageList[0].id ?: 0L
                        stageName = stageList[0].stageName
                    }
                }
            }
        }
    )

    private fun toggleFontSize(nameFontSize: Int, descriptionFontSize: Int) {
        if (nameFontSize < Constants.MIN_FONT_VALUE) {
            _uiState.value.nameRichTextState.toggleSpanStyle(
                SpanStyle(fontSize = Constants.MIN_FONT_VALUE.roundToInt().sp)
            )
        } else {
            _uiState.value.nameRichTextState.toggleSpanStyle(
                SpanStyle(fontSize = nameFontSize.sp)
            )
        }

        if (descriptionFontSize < Constants.MIN_FONT_VALUE) {
            _uiState.value.descriptionRichTextState.toggleSpanStyle(
                SpanStyle(fontSize = Constants.MIN_FONT_VALUE.roundToInt().sp)
            )
        } else {
            _uiState.value.descriptionRichTextState.toggleSpanStyle(
                SpanStyle(fontSize = descriptionFontSize.sp)
            )
        }

    }

    private fun addHomework(subjectId: Long, folderUri: String) {
        val endDate = if (localFinishDate != null && localFinishTime != null) {
            LocalDateTime.of(
                localFinishDate,
                localFinishTime
            )
        } else null
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true
                )
            }
            Log.d("tyutryutyutyrtu", "addHomework is finished: ${_uiState.value.currentSelectedStage}")
            viewModelScope.execute(
                source = {
                    homeworkRepository.insertHomework(
                        HomeworkModel(
                            id = null,
                            color = _uiState.value.currentColor.toArgb(),
                            importance = _uiState.value.currentColor.toImportance(),
                            subjectId = subjectId,
                            addDate = LocalDateTime.now(),
                            name = _uiState.value.nameRichTextState.toHtml(),
                            stage = _uiState.value.currentSelectedStage?.stageName ?: "",
                            description = _uiState.value.descriptionRichTextState.toHtml(),
                            startDate = null,
                            endDate = endDate,
                            imageNameList = emptyList(),
                            stageId = _uiState.value.currentSelectedStage?.id ?: 0L,
                            isFinished = _uiState.value.currentSelectedStage?.isFinishStage ?: false
                        )
                    )
                },
                onSuccess = { id ->
                    Log.d("gfgdfgdfgdfgdfg", "addHomework: name id $id")
                    if (folderUri.isNotBlank() && _uiState.value.imagesUriList.isNotEmpty()) {
                        viewModelScope.execute(
                            source = {
                                filesHandleRepository.uploadImageToUserFolderWithImageUriList(
                                    folderUri = folderUri.toUri(),
                                    imageUriList = _uiState.value.imagesUriList
                                )
                            },
                            onSuccess = { imageNamesList ->
                                updateHomeworkWithImagesList(
                                    homeworkId = id,
                                    imageNamesList = imageNamesList
                                )
                                Log.d("fgdhgghgfh", "imagesList: $imageNamesList")
                            },
                            onError = {
                                Log.d("fgdhgghgfh", "addHomework: $it")
                            }
                        )
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false
                            )
                        }
                        navigateUp()
                    }
                },
                onError = {
                    _uiState.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                }
            )
        }

    }

    private fun updateHomeworkWithImagesList(homeworkId: Long, imageNamesList: List<String>){
        viewModelScope.execute(
            source = {
                homeworkRepository.getHomeworkById(homeworkId)
            },
            onSuccess = { homework ->
                viewModelScope.execute(
                    source = {
                        homeworkRepository.updateHomework(
                            homework.copy(
                                imageNameList = imageNamesList
                            )
                        )
                    },
                    onSuccess = {
                        _uiState.update {
                            it.copy(
                                isLoading = false
                            )
                        }
                        navigateUp()
                    }
                )
            },
            onError = {

            }
        )
    }

    private fun navigateUp() {
        viewModelScope.launch {
            navigator.navigateUp()
        }
    }
}