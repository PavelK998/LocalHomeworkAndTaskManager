package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.editStagesScreen

import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.pkstudio.localhomeworkandtaskmanager.R
import ru.pkstudio.localhomeworkandtaskmanager.core.data.util.SingleSharedFlow
import ru.pkstudio.localhomeworkandtaskmanager.core.domain.manager.ResourceManager
import ru.pkstudio.localhomeworkandtaskmanager.core.extensions.execute
import ru.pkstudio.localhomeworkandtaskmanager.core.navigation.Navigator
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.EditStageResult
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.StageModel
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository.HomeworkRepository
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository.StageRepository
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.stageVariant9
import javax.inject.Inject

@HiltViewModel
class EditStagesViewModel @Inject constructor(
    private val stageRepository: StageRepository,
    private val navigator: Navigator,
    private val resourceManager: ResourceManager,
    private val homeworkRepository: HomeworkRepository
) : ViewModel() {

    private var isNavigateBtnClicked = false

    private var stageIndexForDelete = -1

    private var stageIndexForUpdate = -1

    private val _renameFlow = MutableStateFlow(EditStageResult())

    private val _uiAction = SingleSharedFlow<EditStageUiAction>()
    val uiAction = _uiAction.asSharedFlow()

    private val _uiState = MutableStateFlow(
        EditStagesState(
            titleDeleteAlertDialog = resourceManager.getString(R.string.delete_dialog_title_stage),
        )
    )
    val uiState = _uiState
        .onStart {
            getStages()
            startObserveFlow()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = EditStagesState()
        )

    fun handleIntent(intent: EditStagesIntent) {
        when (intent) {
            is EditStagesIntent.OnAddStageBtmClick -> {
                if (intent.position == _uiState.value.stagesList.lastIndex){
                    if (_uiState.value.stagesList.size == 2) {
                        addStage(intent.position)
                    } else {
                        addStage(intent.position - 1)
                    }
                } else {
                    addStage(intent.position + 1)
                }

            }

            is EditStagesIntent.OnStageNameChange -> {
                _renameFlow.update {
                    EditStageResult(
                        newName = intent.name,
                        index = intent.index
                    )
                }
            }

            is EditStagesIntent.OnDeleteStageBtmClick -> {
                if (intent.index == 0 || intent.index == _uiState.value.stagesList.lastIndex) {
                    _uiAction.tryEmit(
                        EditStageUiAction.ShowErrorMessage(
                            resourceManager.getString(R.string.delete_single_stage)
                        )
                    )
                } else {
                    stageIndexForDelete = intent.index
                    _uiState.update {
                        it.copy(
                            isDeleteAlertDialogOpened = true
                        )
                    }
                }
            }

            is EditStagesIntent.NavigateUp -> {
                if (!isNavigateBtnClicked) {
                    viewModelScope.launch {
                        navigator.navigateUp()
                    }
                    isNavigateBtnClicked = true
                }
            }

            is EditStagesIntent.CloseDeleteDialog -> {
                _uiState.update {
                    it.copy(
                        isDeleteAlertDialogOpened = false
                    )
                }
            }

            is EditStagesIntent.ConfirmDeleteStage -> {
                if (stageIndexForDelete in _uiState.value.stagesList.indices) {
                    _uiState.update {
                        it.copy(
                            isDeleteAlertDialogOpened = false
                        )
                    }
                    changeStagesInHomework(
                        fromStageId = _uiState.value.stagesList[stageIndexForDelete].id ?: 0L,
                        targetStageId = _uiState.value.stagesList[0].id ?: 0L,
                        targetStageName = _uiState.value.stagesList[0].stageName
                    )
                }
            }

            is EditStagesIntent.CloseColorPickerDialog -> {
                stageIndexForUpdate = -1
                _uiState.update {
                    it.copy(
                        isColorAlertDialogOpened = false
                    )
                }
            }

            is EditStagesIntent.ConfirmColorChange -> {
                updateStageColor(intent.color.toArgb())
            }

            is EditStagesIntent.OnColorPaletteClicked -> {
                stageIndexForUpdate = intent.index
                _uiState.update {
                    it.copy(
                        isColorAlertDialogOpened = true
                    )
                }
            }
        }
    }

    private fun updateStageColor(color: Int) {
        if (stageIndexForUpdate in _uiState.value.stagesList.indices){
            viewModelScope.execute(
                source = {
                    stageRepository.updateStage(
                        stage = _uiState.value.stagesList[stageIndexForUpdate].copy(
                            color = color
                        )
                    )
                },
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isColorAlertDialogOpened = false
                        )
                    }
                }
            )
        }

    }

    @OptIn(FlowPreview::class)
    private fun startObserveFlow() {
        viewModelScope.launch {
            _renameFlow
                .debounce(200)
                .distinctUntilChanged()
                .collect { editStageResult ->
                    if (editStageResult.index in _uiState.value.stagesList.indices) {
                        renameStage(
                            stageModel = _uiState.value.stagesList[editStageResult.index],
                            newName = editStageResult.newName
                        )
                    }
                }
        }
    }

    private fun deleteStage(
        stage: StageModel
    ) = viewModelScope.execute(
        source = {
            stageRepository.deleteStageFromPosition(stage = stage)
        }
    )

    private fun changeStagesInHomework(
        fromStageId: Long,
        targetStageId: Long,
        targetStageName: String
    ) = viewModelScope.execute(
        source = {
            homeworkRepository.changeHomeworkStagesAfterDeleteStage(
                fromStageId = fromStageId,
                targetStageId = targetStageId,
                targetStageName = targetStageName
            )
        },
        onSuccess = {
            deleteStage(_uiState.value.stagesList[stageIndexForDelete])
        }

    )

    private fun addStage(
        position: Int
    ) = viewModelScope.execute(
        source = {
            stageRepository.insertStageToPosition(
                stage = StageModel(
                    stageName = resourceManager.getString(R.string.default_added_stage),
                    position = position,
                    color = stageVariant9.toArgb()
                )
            )
        },
        onSuccess = {
            _uiState.update {
                it.copy(
                    isColorAlertDialogOpened = false
                )
            }
        }
    )

    private fun renameStage(stageModel: StageModel, newName: String) = viewModelScope.execute(
        source = {
            stageRepository.updateStage(
                stageModel.copy(
                    stageName = newName
                )
            )
        }
    )

    private fun getStages() = viewModelScope.execute(
        source = {
            stageRepository.getAllStages()
        },
        onSuccess = { stageFlow ->
            viewModelScope.launch {
                stageFlow.collect { stages ->
                    if (stages.isNotEmpty()) {
                        _uiState.update {
                            it.copy(
                                commentDeleteAlertDialog = buildString {
                                    append(resourceManager.getString(R.string.comment_subjects_delete_dialog_stage))
                                    append(
                                        stages[0].stageName
                                    )
                                }
                            )
                        }
                    }
                    _uiState.update {
                        it.copy(
                            stagesList = stages,
                        )
                    }
                }
            }
        }
    )
}