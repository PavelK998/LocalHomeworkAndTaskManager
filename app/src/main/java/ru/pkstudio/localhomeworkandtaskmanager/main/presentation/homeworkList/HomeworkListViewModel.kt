package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
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
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toHomeworkModelList
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toImportance
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toListHomeworkUiModel
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.HomeworkModel
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository.StageRepository
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository.SubjectsRepository
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList.uiModel.StageUiModel
import javax.inject.Inject

@HiltViewModel
class HomeworkListViewModel @Inject constructor(
    private val navigator: Navigator,
    private val resourceManager: ResourceManager,
    private val subjectsRepository: SubjectsRepository,
    private val stageRepository: StageRepository,
    private val deviceManager: DeviceManager,
) : ViewModel() {
    private val kanban = resourceManager.getString(R.string.kanban)
    private val list = resourceManager.getString(R.string.list)

    private var selectedCardToChangeColor = -1

    private var isBackButtonClicked = false

    private var selectedHomeworkForDelete: HomeworkModel? = null


    fun parseArguments(subjectId: String) {
        _uiState.update {
            it.copy(
                subjectId = subjectId
            )
        }
        checkFilterActions()
        getHomework(
            subjectId = subjectId,
            sortByDescendingImportance = _uiState.value.isSortImportance
        )
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
            checkUsage()
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

            is HomeworkListIntent.NavigateToAddHomework -> {
                viewModelScope.launch {
                    navigator.navigate(
                        Destination.HomeworkAddScreen(
                            subjectId = _uiState.value.subjectId
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
                            viewModelScope.launch {
                                _uiState.update {
                                    it.copy(
                                        segmentedButtonSelectedIndex = intent.index,
                                        isKanbanScreenVisible = false
                                    )
                                }

                                setDisplayMethod(Constants.LIST.ordinal)
                            }
                        }

                        kanban -> {
                            viewModelScope.launch {
                                _uiState.update {
                                    it.copy(
                                        segmentedButtonSelectedIndex = intent.index,
                                        isKanbanScreenVisible = true
                                    )
                                }
                                turnOffCardEditMode()
                                setDisplayMethod(Constants.KANBAN.ordinal)
                            }
                        }
                    }
                }
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
                                subjectId = _uiState.value.subjectId
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
                if (_uiState.value.isEditModeEnabled) {
                    deleteSelectedCards()
                } else {
                    selectedHomeworkForDelete?.let {
                        deleteSingleHomework(homeworkModel = it)
                    }
                }
                _uiState.update {
                    it.copy(
                        isDeleteDialogOpen = false
                    )
                }
            }

            is HomeworkListIntent.NavigateToEditStages -> {
                viewModelScope.launch {
                    navigator.navigate(Destination.StageEditScreen)
                }
            }

            is HomeworkListIntent.DeleteItemFromKanban -> {
                if (
                    intent.oldRowId in _uiState.value.kanbanItemsList.indices
                    && intent.oldColumnId in _uiState.value.kanbanItemsList[intent.oldRowId].columnItems.indices
                ) {
                    selectedHomeworkForDelete =
                        _uiState.value.kanbanItemsList[intent.oldRowId].columnItems[intent.oldColumnId].toHomeworkModel()
                    _uiState.update {
                        it.copy(
                            isDeleteDialogOpen = true
                        )
                    }
                }

            }

            is HomeworkListIntent.TurnFabInvisible -> {
                _uiState.update {
                    it.copy(
                        isFABVisible = false
                    )
                }
            }

            is HomeworkListIntent.TurnFabVisible -> {
                _uiState.update {
                    it.copy(
                        isFABVisible = true
                    )
                }
            }

            is HomeworkListIntent.OnCardColorPaletteClicked -> {
                selectedCardToChangeColor = intent.modelIndex
                _uiState.update {
                    it.copy(
                        isColorPaletteDialogOpen = true
                    )
                }
            }

            is HomeworkListIntent.CloseCardColorPaletteDialog -> {
                _uiState.update {
                    it.copy(
                        isColorPaletteDialogOpen = false
                    )
                }
            }

            is HomeworkListIntent.SelectColor -> {
                if (selectedCardToChangeColor in _uiState.value.homeworkList.indices) {
                    updateCardColor(
                        color = intent.color,
                        homeworkModel = _uiState.value.homeworkList[selectedCardToChangeColor].toHomeworkModel()
                    )
                }
            }

            is HomeworkListIntent.OnOpenBottomSheetClick -> {
                _uiState.update {
                    it.copy(
                        isSortBottomSheetOpen = true
                    )
                }
            }

            is HomeworkListIntent.CloseBottomSheet -> {
                _uiState.update {
                    it.copy(
                        isSortBottomSheetOpen = false
                    )
                }
            }

            is HomeworkListIntent.OnKanbanCardColorPaletteClicked -> {
                val model = _uiState.value.homeworkList.find { it.id == intent.modelId }
                if (model != null) {
                    selectedCardToChangeColor = _uiState.value.homeworkList.indexOf(model)
                    _uiState.update {
                        it.copy(
                            isColorPaletteDialogOpen = true
                        )
                    }
                }
            }

            is HomeworkListIntent.OnSortClick -> {
                viewModelScope.launch {
                    if (intent.isSortDescendingImportance) {
                        deviceManager.setFilterImportance(Constants.FILTER_IMPORTANCE_DESCENDING.ordinal)
                        _uiState.update {
                            it.copy(
                                isSortImportance = true
                            )
                        }
                        sortHomework(
                            sortByDescendingImportance = _uiState.value.isSortImportance,
                        )
                    } else {
                        deviceManager.setFilterImportance(Constants.FILTER_IMPORTANCE_ASCENDING.ordinal)
                        _uiState.update {
                            it.copy(
                                isSortImportance = false
                            )
                        }
                        sortHomework(
                            sortByDescendingImportance = _uiState.value.isSortImportance,
                        )

                    }
                }
            }
        }
    }

    private fun checkFilterActions() = viewModelScope.launch {
        val importanceFilter = deviceManager.getFilterImportance()
        if (importanceFilter == -1) {
            deviceManager.setFilterImportance(Constants.FILTER_IMPORTANCE_DESCENDING.ordinal)
        }

        if (importanceFilter == Constants.FILTER_IMPORTANCE_DESCENDING.ordinal) {
            _uiState.update {
                it.copy(
                    isSortImportance = true
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    isSortImportance = false
                )
            }
        }
    }

    private fun sortHomework(
        sortByDescendingImportance: Boolean,
    ) {
        val homeworkList = _uiState.value.homeworkList
        val newList = if (sortByDescendingImportance) {
            homeworkList.sortedByDescending {
                it.importance
            }
        } else {
            homeworkList.sortedByDescending {
                it.id
            }
        }
        val kanbanItemsList = _uiState.value.kanbanItemsList.map {
            it.copy(
                columnItems = if (sortByDescendingImportance) {
                    it.columnItems.sortedByDescending { homeworkModel ->
                        homeworkModel.importance
                    }
                } else {
                    it.columnItems.sortedByDescending { homeworkModel ->
                        homeworkModel.id
                    }
                }
            )
        }
        _uiState.update {
            it.copy(
                homeworkList = newList,
                kanbanItemsList = kanbanItemsList
            )
        }
    }

    private fun updateCardColor(color: Color, homeworkModel: HomeworkModel) =
        viewModelScope.execute(
            source = {
                subjectsRepository.updateHomeworkInSubject(
                    subjectId = _uiState.value.subjectId,
                    homeworkModel.copy(
                        color = color.toArgb(),
                        importance = color.toImportance()
                    )
                )
            },
            onSuccess = {
                _uiState.update {
                    it.copy(
                        isColorPaletteDialogOpen = false
                    )
                }
            },
            onError = {

            }
        )

    private fun deleteSingleHomework(homeworkModel: HomeworkModel) = viewModelScope.execute(
        source = {
            subjectsRepository.updateHomeworkInSubject(
                subjectId = _uiState.value.subjectId,
                homeworkModel = homeworkModel
            )
        },
        onSuccess = {
            selectedHomeworkForDelete = null
        },
        onError = {
        }
    )

    private fun checkUsage() = viewModelScope.launch {
        when (deviceManager.getUsage()) {
            Constants.TASK_TRACKER.ordinal -> {
                _uiState.update {
                    it.copy(
                        deleteDialogDescription = resourceManager.getString(R.string.delete_task_dialog_description)
                    )
                }
            }

            Constants.DIARY.ordinal -> {
                _uiState.update {
                    it.copy(
                        deleteDialogDescription = resourceManager.getString(R.string.delete_homework_dialog_description)
                    )
                }
            }
        }
    }

    private fun deleteSelectedCards() {
        val listForDelete = _uiState.value.homeworkList.filter { it.isChecked }
        viewModelScope.execute(
            source = {
                subjectsRepository.deleteHomeworkListInSubject(
                    subjectId = _uiState.value.subjectId,
                    homeworkModelList = listForDelete.toHomeworkModelList()
                )

            },
            onSuccess = {
                _uiState.update {
                    it.copy(
                        isEditModeEnabled = false
                    )
                }
            },
            onError = {

            }
        )
    }

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

    private fun getDisplayMethod() = viewModelScope.launch {
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

    private suspend fun setDisplayMethod(displayMethod: Int) {
        deviceManager.setSelectedDisplayMethod(displayMethod)
    }

    private fun getHomework(
        subjectId: String,
        sortByDescendingImportance: Boolean,
    ) {
        viewModelScope.execute(
            source = {
                subjectsRepository.getSubjectByIdFlow(subjectId)
            },
            onSuccess = { subjectFlow ->
                viewModelScope.launch {
                    subjectFlow.collect { subject ->
                        if (subject.homework.isNotEmpty()) {
                            val stages = stageRepository.getAllStagesSingleTime()
                            val homeworkUiModels = subject.homework.toListHomeworkUiModel()
                            val sortedHomework = if (sortByDescendingImportance) {
                                homeworkUiModels.sortedByDescending { it.importance }
                            } else {
                                homeworkUiModels.sortedByDescending { it.id }
                            }
                            val homeworkByStage = sortedHomework.groupBy { it.stageId }
                            val stageUiModelList = stages.map { stage ->
                                StageUiModel(
                                    id = stage.id,
                                    stageName = stage.stageName,
                                    color = stage.color,
                                    itemsCount = (homeworkByStage[stage.id]?.size ?: 0).toString(),
                                    isFinishStage = stage.isFinishStage
                                )
                            }
                            _uiState.update {
                                it.copy(
                                    subjectName = subject.subjectName,
                                    kanbanItemsList = stageUiModelList.map { stage ->
                                        KanbanItem(
                                            rowItem = stage,
                                            columnItems = homeworkByStage[stage.id] ?: emptyList()

                                        )
                                    },
                                    homeworkList = sortedHomework,
                                    isLoading = false,
                                    isScreenEmpty = false
                                )
                            }
                            getDisplayMethod()

                        } else {
                            _uiState.update {
                                it.copy(
                                    subjectName = subject.subjectName,
                                    isLoading = false,
                                    isScreenEmpty = true
                                )
                            }
                        }

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
                    isFinished = kanbanItems[newRowId].rowItem.isFinishStage
                )
                viewModelScope.execute(
                    source = {
                        subjectsRepository.updateHomeworkInSubject(
                            subjectId = _uiState.value.subjectId,
                            homeworkModel = homeworkModel.toHomeworkModel()
                        )
                    },
                    onSuccess = {

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