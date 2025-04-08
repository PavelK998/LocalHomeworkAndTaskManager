package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.addHomework

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
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
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.HomeworkModel
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository.HomeworkRepository
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository.StageRepository
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class AddHomeworkViewModel @Inject constructor(
    private val navigator: Navigator,
    private val homeworkRepository: HomeworkRepository,
    private val stageRepository: StageRepository,
    private val resourceManager: ResourceManager
) : ViewModel() {


    private var subjectId: Long = 0

    private var stageId = 0L
    private var stageName = ""

    fun parseArguments(subjectId: Long) {
        this.subjectId = subjectId
    }

    private val _uiState = MutableStateFlow(AddHomeworkState())
    val uiState = _uiState
        .onStart {
            getStages()
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
                navigateUp()
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
                    if (_uiState.value.title.isNotEmpty()) {
                        addHomework(subjectId = subjectId)
                    } else {
                        _uiAction.tryEmit(
                            AddHomeworkUIAction.ShowError(resourceManager.getString(R.string.empty_homework_title))
                        )
                    }

                }
            }
        }
    }

    private fun getStages() = viewModelScope.execute(
        source = {
            stageRepository.getAllStages()
        },
        onSuccess = { stageFlow ->
            viewModelScope.launch {
                stageFlow.collect{ stageList ->
                    if (stageList.isNotEmpty()) {
                        stageId = stageList[0].id?: 0L
                        stageName = stageList[0].stageName
                    }
                }
            }

        }
    )

    private fun addHomework(subjectId: Long) {
        viewModelScope.execute(
            source = {
                homeworkRepository.insertHomework(
                    HomeworkModel(
                        id = null,
                        subjectId = subjectId,
                        addDate = LocalDateTime.now(),
                        name = _uiState.value.title,
                        stage = stageName,
                        description = _uiState.value.description,
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

    private fun navigateUp(){
        viewModelScope.launch {
            navigator.navigateUp()
        }
    }
}