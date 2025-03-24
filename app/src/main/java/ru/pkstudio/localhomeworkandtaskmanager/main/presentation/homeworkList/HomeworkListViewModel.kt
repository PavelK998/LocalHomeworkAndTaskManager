package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.pkstudio.localhomeworkandtaskmanager.R
import ru.pkstudio.localhomeworkandtaskmanager.core.components.kanban.KanbanItem
import ru.pkstudio.localhomeworkandtaskmanager.core.domain.manager.DeviceManager
import ru.pkstudio.localhomeworkandtaskmanager.core.domain.manager.ResourceManager
import ru.pkstudio.localhomeworkandtaskmanager.core.extensions.execute
import ru.pkstudio.localhomeworkandtaskmanager.core.navigation.Destination
import ru.pkstudio.localhomeworkandtaskmanager.core.navigation.Navigator
import ru.pkstudio.localhomeworkandtaskmanager.core.util.Constants
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toHomeworkUiModelList
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.StageModel
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository.HomeworkRepository
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository.StageRepository
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList.uiModel.StageUiModel
import javax.inject.Inject

@HiltViewModel
class HomeworkListViewModel @Inject constructor(
    private val navigator: Navigator,
    private val resourceManager: ResourceManager,
    private val homeworkRepository: HomeworkRepository,
    private val stageRepository: StageRepository,
    private val deviceManager: DeviceManager,
) : ViewModel() {
    private val kanban = resourceManager.getString(R.string.kanban)
    private val list = resourceManager.getString(R.string.list)

    private var subjectId = 0L
    private var stagesList = emptyList<StageModel>()
    private var isDisplayMethodChosen = false

    fun parseArguments(subjectId: Long) {
        this.subjectId = subjectId
        Log.d("fdgdfgdfgdfgfdgdf", "parseArguments: $subjectId")
        getHomework(subjectId)
        getStage()
    }

    private val _uiState = MutableStateFlow(
        HomeworkListState(
            segmentedButtonOptions = listOf(
                list,
                kanban
            )
        )
    )
    val uiState = _uiState
        .onStart {

        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = HomeworkListState(
                segmentedButtonOptions = listOf(

                )
            )
        )

    fun handleIntent(intent: HomeworkListIntent) {
        when (intent) {
            is HomeworkListIntent.CheckCard -> {

            }
            is HomeworkListIntent.DeleteCards -> {

            }
            is HomeworkListIntent.ExpandMenu -> {

            }
            is HomeworkListIntent.NavigateToAddHomework -> {
                viewModelScope.launch {
                    navigator.navigate(
                        Destination.HomeworkAddScreen(
                            subjectId = subjectId
                        )
                    )
                }
            }
            is HomeworkListIntent.NavigateToDetailsHomework -> {

            }
            is HomeworkListIntent.NavigateUp -> {
                navigateUp()
            }
            is HomeworkListIntent.OnSegmentedButtonClick -> {
                if (intent.index in _uiState.value.segmentedButtonOptions.indices) {
                    when (_uiState.value.segmentedButtonOptions[intent.index]) {
                        list -> {
                            _uiState.update {
                                it.copy(
                                    segmentedButtonSelectedIndex = intent.index,
                                    isKanbanScreenVisible = false
                                )
                            }

                            setDisplayMethod(Constants.LIST.ordinal)
                        }
                        kanban -> {
                            _uiState.update {
                                it.copy(
                                    segmentedButtonSelectedIndex = intent.index,
                                    isKanbanScreenVisible = true
                                )
                            }
                            setDisplayMethod(Constants.KANBAN.ordinal)
                        }
                    }
                }
            }
            is HomeworkListIntent.ShrinkMenu -> {

            }
            is HomeworkListIntent.TurnEditMode -> {

            }
        }
    }

    private fun getDisplayMethod() {
        deviceManager.getSelectedDisplayMethod().let { displayMethod ->
            when (displayMethod) {
                Constants.LIST.ordinal -> {
                    _uiState.update {
                        it.copy(
                            segmentedButtonSelectedIndex = _uiState.value.segmentedButtonOptions.indexOf(list),
                            isKanbanScreenVisible = false
                        )
                    }
                }

                Constants.KANBAN.ordinal -> {
                    Log.d("dfgdfgdfgfgdf", "getDisplayMethod: ${_uiState.value.kanbanItemsList}")
                    _uiState.update {
                        it.copy(
                            segmentedButtonSelectedIndex = _uiState.value.segmentedButtonOptions.indexOf(kanban),
                            isKanbanScreenVisible = true
                        )
                    }
                }
            }
        }
    }

    private fun setDisplayMethod(displayMethod: Int) {
        deviceManager.setSelectedDisplayMethod(displayMethod)
    }

    private fun getStage() = viewModelScope.execute(
        source = {
            stageRepository.getAllStages()
        },
        onSuccess = { stageFlow ->
            viewModelScope.launch {
                stageFlow.collect { stageList ->
                    stagesList = stageList
                }
            }
            if (!isDisplayMethodChosen) {
                getDisplayMethod()
                isDisplayMethodChosen = true
            }
        }
    )

    private fun getHomework(subjectId: Long) {
        viewModelScope.execute(
            source = {
                homeworkRepository.getHomeworkWithSubjectById(
                    subjectId = subjectId
                )
            },
            onSuccess = { subjectWithHomework ->
                if (subjectWithHomework.homework.isEmpty()) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isScreenEmpty = true
                        )
                    }
                } else {
                    _uiState.update {
                        val stageUiModelList = stagesList.map { stage ->
                            StageUiModel(
                                id = stage.id?: 0L,
                                stageName = stage.stageName,
                                itemsCount = subjectWithHomework.homework.filter { homework ->
                                    homework.stageId == stage.id
                                }.size.toString()
                            )
                        }
                        it.copy(
                            kanbanItemsList = stageUiModelList.map { stage ->
                                KanbanItem(
                                    rowItem = stage,
                                    columnItems = subjectWithHomework.homework.toHomeworkUiModelList()
                                )
                            },
                            homeworkList = subjectWithHomework.homework.toHomeworkUiModelList(),
                            isLoading = false,
                            isScreenEmpty = false
                        )
                    }
                }
            }
        )
    }

    private fun navigateUp(){
        viewModelScope.launch {
            navigator.navigateUp()
        }
    }
}