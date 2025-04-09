package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList

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
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toHomeworkModel
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toHomeworkUiModelList
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toListHomeworkModels
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
    private var isDisplayMethodChosen = false
    private var isBackButtonClicked = false
    private var stagesList = emptyList<StageModel>()

    fun parseArguments(subjectId: Long) {
        this.subjectId = subjectId
        getHomework(subjectId)
    }

    private val _uiState = MutableStateFlow(
        HomeworkListState(
            segmentedButtonOptions = listOf(
                list,
                kanban
            ),
            deleteDialogTitle = resourceManager.getString(R.string.delete_homework_dialog_title),
            deleteDialogDescription = resourceManager.getString(R.string.delete_homework_dialog_description)
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
                if (intent.index in _uiState.value.homeworkList.indices) {
                    checkCard(
                        index = intent.index,
                        isChecked = intent.isChecked
                    )
                    if (_uiState.value.homeworkList.all { !it.isChecked }) {
                        turnOffCardEditMode()
                    }
                    _uiState.update {
                        it.copy(
                            numberOfCheckedCards = _uiState.value.homeworkList.filter { homework ->
                                homework.isChecked
                            }.size
                        )
                    }
                }
            }

            is HomeworkListIntent.DeleteCards -> {
                _uiState.update {
                    it.copy(
                        isDeleteDialogOpen = true
                    )
                }
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
                viewModelScope.launch {
                    navigator.navigate(
                        Destination.DetailsHomeworkScreen(
                            intent.homeworkId,
                            intent.subjectId
                        )
                    )
                }
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
                if (intent.index in _uiState.value.homeworkList.indices) {
                    turnCardEditMode(index = intent.index)
                    _uiState.update {
                        it.copy(
                            numberOfCheckedCards = _uiState.value.homeworkList.filter { homework ->
                                homework.isChecked
                            }.size
                        )
                    }
                } else if (intent.index == -1) {
                    turnOffCardEditMode()
                }
            }

            is HomeworkListIntent.OnItemMoved -> {
                moveKanbanItem(
                    oldRowId = intent.oldRowId,
                    oldColumnId = intent.oldColumnId,
                    newRowId = intent.newRowId
                )
            }

            is HomeworkListIntent.NavigateToDetailsHomeworkFromKanban -> {
                if (
                    intent.rowIndex in _uiState.value.kanbanItemsList.indices
                    && intent.columnIndex in _uiState.value.kanbanItemsList[intent.rowIndex].columnItems.indices
                ) {
                    viewModelScope.launch {
                        navigator.navigate(
                            Destination.DetailsHomeworkScreen(
                                homeworkId =
                                _uiState.value.kanbanItemsList[intent.rowIndex].columnItems[intent.columnIndex].id,
                                subjectId = subjectId
                            )
                        )
                    }
                }
            }

            is HomeworkListIntent.CloseDeleteAlertDialog -> {
                _uiState.update {
                    it.copy(
                        isDeleteDialogOpen = false
                    )
                }
            }

            is HomeworkListIntent.ConfirmDeleteCards -> {
                deleteSelectedCards()
                _uiState.update {
                    it.copy(
                        isDeleteDialogOpen = false
                    )
                }
            }
        }
    }

    private fun deleteSelectedCards() {
        val listForDelete = _uiState.value.homeworkList.filter { it.isChecked }
        viewModelScope.execute(
            source = {
                homeworkRepository.deleteListHomework(listForDelete.toListHomeworkModels())
            },
            onSuccess = {
                _uiState.update {
                    it.copy(
                        isEditModeEnabled = false
                    )
                }
                getHomework(subjectId = subjectId)
            }
        )
    }


//    {
//        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
//
//        }
//        viewModelScope.launch(exceptionHandler) {
//            val deferredResultList = mutableListOf<Deferred<Unit>>()
//            val listForDelete = _uiState.value.homeworkList.filter { it.isChecked }
//            listForDelete.forEach { homeworkUiModel ->
//                val deferredResult = async {
//                    homeworkRepository.deleteHomework(homeworkUiModel.toHomeworkModel())
//                }
//                deferredResultList.add(deferredResult)
//            }
//            deferredResultList.awaitAll()
//        }
//    }

    private fun turnCardEditMode(index: Int) {
        val list = _uiState.value.homeworkList.map {
            it.copy(
                isCheckBoxVisible = true
            )
        }.toMutableList()
        val item = list[index].copy(
            isChecked = true
        )
        list[index] = item
        _uiState.update {
            it.copy(
                homeworkList = list,
                isEditModeEnabled = true
            )
        }
    }

    private fun turnOffCardEditMode() {
        val list = _uiState.value.homeworkList.map {
            it.copy(
                isCheckBoxVisible = false,
                isChecked = false
            )
        }.toMutableList()
        _uiState.update {
            it.copy(
                homeworkList = list,
                isEditModeEnabled = false
            )
        }
    }

    private fun checkCard(index: Int, isChecked: Boolean) {
        val list = _uiState.value.homeworkList.toMutableList()
        val item = list[index].copy(
            isChecked = !isChecked
        )
        list[index] = item
        _uiState.update {
            it.copy(
                homeworkList = list
            )
        }
    }

    private fun getDisplayMethod() {
        deviceManager.getSelectedDisplayMethod().let { displayMethod ->
            when (displayMethod) {
                Constants.LIST.ordinal -> {
                    _uiState.update {
                        it.copy(
                            segmentedButtonSelectedIndex = _uiState.value.segmentedButtonOptions.indexOf(
                                list
                            ),
                            isKanbanScreenVisible = false
                        )
                    }
                }

                Constants.KANBAN.ordinal -> {
                    _uiState.update {
                        it.copy(
                            segmentedButtonSelectedIndex = _uiState.value.segmentedButtonOptions.indexOf(
                                kanban
                            ),
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
                            subjectName = subjectWithHomework.subject.subjectName,
                            isLoading = false,
                            isScreenEmpty = true
                        )
                    }
                } else {
                    viewModelScope.launch {
                        val stages = stageRepository.getAllStagesSingleTime()
                        val stageUiModelList = stages.map { stage ->
                            StageUiModel(
                                id = stage.id ?: 0L,
                                stageName = stage.stageName,
                                itemsCount = subjectWithHomework.homework.filter { homework ->
                                    homework.stageId == stage.id
                                }.size.toString()
                            )
                        }
                        _uiState.update {
                            it.copy(
                                subjectName = subjectWithHomework.subject.subjectName,
                                kanbanItemsList = stageUiModelList.map { stage ->
                                    KanbanItem(
                                        rowItem = stage,
                                        columnItems = subjectWithHomework.homework.filter { homeworkModel ->
                                            homeworkModel.stageId == stage.id
                                        }.toHomeworkUiModelList()
                                    )
                                },
                                homeworkList = subjectWithHomework.homework.toHomeworkUiModelList(),
                                isLoading = false,
                                isScreenEmpty = false
                            )
                        }
                        getDisplayMethod()
                    }
                }
            }
        )
    }

    private fun moveKanbanItem(
        oldRowId: Int,
        oldColumnId: Int,
        newRowId: Int
    ) {
        val kanbanItems = _uiState.value.kanbanItemsList.toMutableList()
        if (oldRowId in kanbanItems.indices && newRowId in kanbanItems.indices) {
            if (oldColumnId in kanbanItems[oldRowId].columnItems.indices) {
                val homeworkModel = kanbanItems[oldRowId].columnItems[oldColumnId].copy(
                    stageId = kanbanItems[newRowId].rowItem.id,
                    stageName = kanbanItems[newRowId].rowItem.stageName,
                )

                viewModelScope.execute(
                    source = {
                        homeworkRepository.updateHomework(
                            homework = homeworkModel.toHomeworkModel()
                        )
                    },
                    onSuccess = {
                        getHomework(subjectId = subjectId)
                    }
                )
            }


        }

    }

    private fun navigateUp() {
        if (!isBackButtonClicked) {
            viewModelScope.launch {
                navigator.navigateUp()
                isBackButtonClicked = true
            }
        }
    }
}