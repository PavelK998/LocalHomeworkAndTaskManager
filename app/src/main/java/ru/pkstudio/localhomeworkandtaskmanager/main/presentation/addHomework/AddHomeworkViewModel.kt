package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.addHomework

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamedrejeb.richeditor.model.RichTextState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.pkstudio.localhomeworkandtaskmanager.R
import ru.pkstudio.localhomeworkandtaskmanager.core.data.util.SingleSharedFlow
import ru.pkstudio.localhomeworkandtaskmanager.core.domain.manager.ResourceManager
import ru.pkstudio.localhomeworkandtaskmanager.core.extensions.execute
import ru.pkstudio.localhomeworkandtaskmanager.core.navigation.Navigator
import ru.pkstudio.localhomeworkandtaskmanager.core.util.Constants
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.HomeworkModel
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository.HomeworkRepository
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository.StageRepository
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlin.random.Random

@HiltViewModel
class AddHomeworkViewModel @Inject constructor(
    private val navigator: Navigator,
    private val homeworkRepository: HomeworkRepository,
    private val stageRepository: StageRepository,
    private val resourceManager: ResourceManager
) : ViewModel() {


    private var subjectId: Long = 0
    private var isFontSizeSet = false

    private var stageId = 0L
    private var stageName = ""
    private var isNavigateUp = false


    fun parseArguments(subjectId: Long) {
        this.subjectId = subjectId
    }

    private val _uiState = MutableStateFlow(AddHomeworkState())
    val uiState = _uiState
        .onStart {
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
                parseMultiplyImageToListBitmap(listUri = intent.listUri)
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
                        addHomework(subjectId = subjectId)
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

    private fun parseMultiplyImageToListBitmap(listUri: List<Uri>) =
        viewModelScope.launch(
            Dispatchers.Default
        ) {
            val bitmapList = mutableListOf<Deferred<Pair<Long, Bitmap>>>()
            val listIds = mutableListOf<Long>()
            repeat(listUri.size) {
                var id: Long
                do {
                    id = Random.nextLong()
                } while (listIds.any { it == id })
                listIds.add(id)
            }

            listUri.forEachIndexed { index, uri ->
                val result = async {

                    val bitmap = resourceManager.parseBitmapFromUri(uri)!!
                    Pair(
                        first = listIds[index],
                        second = bitmap
                    )

                }
                bitmapList.add(result)
            }

            _uiState.update {
                it.copy(
                    imagesList = bitmapList.awaitAll()
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
                    if (stageList.isNotEmpty()) {
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

    private fun addHomework(subjectId: Long) {
        viewModelScope.execute(
            source = {
                homeworkRepository.insertHomework(
                    HomeworkModel(
                        id = null,
                        subjectId = subjectId,
                        addDate = LocalDateTime.now(),
                        name = _uiState.value.nameRichTextState.toHtml(),
                        stage = stageName,
                        description = _uiState.value.descriptionRichTextState.toHtml(),
                        startDate = null,
                        endDate = null,
                        imageUrl = "",
                        stageId = stageId
                    )
                )
            },
            onSuccess = {
                navigateUp()
            }
        )
    }

    private fun navigateUp() {
        viewModelScope.launch {
            navigator.navigateUp()
        }
    }
}