package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.editStagesScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.pkstudio.localhomeworkandtaskmanager.R
import ru.pkstudio.localhomeworkandtaskmanager.core.domain.manager.ResourceManager
import ru.pkstudio.localhomeworkandtaskmanager.core.extensions.execute
import ru.pkstudio.localhomeworkandtaskmanager.core.navigation.Navigator
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.EditStageResult
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.StageModel
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository.StageRepository
import javax.inject.Inject

@HiltViewModel
class EditStagesViewModel @Inject constructor(
    private val stageRepository: StageRepository,
    private val navigator: Navigator,
    private val resourceManager: ResourceManager
) : ViewModel() {

    private var isNavigateBtnClicked = false

    private val _renameFlow = MutableStateFlow(EditStageResult())

    private val _uiState = MutableStateFlow(EditStagesState())
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
                addStage(intent.position + 1)
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
                deleteStage(intent.stage)
            }

            is EditStagesIntent.NavigateUp -> {
                if (!isNavigateBtnClicked) {
                    viewModelScope.launch {
                        navigator.navigateUp()
                    }
                    isNavigateBtnClicked = true
                }
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun startObserveFlow() {
        viewModelScope.launch {
            _renameFlow
                .debounce(200)
                .distinctUntilChanged()
                .collect { editStageResult ->
                    if (editStageResult.index in _uiState.value.stagesList.indices){
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

    private fun addStage(
        position: Int
    ) = viewModelScope.execute(
        source = {
            stageRepository.insertStageToPosition(
                stage = StageModel(
                    stageName = resourceManager.getString(R.string.default_stage),
                    position = position
                )
            )
        }
    )

    private fun renameStage(stageModel: StageModel, newName: String) = viewModelScope.execute(
        source = {
            stageRepository.updateStage(
                stageModel.copy(
                    stageName = newName
                )
            )
        },
        onSuccess = {
            Log.d("gfdssdfsdfsd", "renameStage: success")
        },
        onError = {
            Log.d("gfdssdfsdfsd", "renameStage: error $it")
        }
    )

    private fun getStages() = viewModelScope.execute(
        source = {
            stageRepository.getAllStages()
        },
        onSuccess = { stageFlow ->
            viewModelScope.launch {
                stageFlow.collect { stages ->
                    _uiState.update {
                        it.copy(
                            stagesList = stages
                        )
                    }
                }
            }

        }
    )

}