package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList

import android.util.Log
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
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toHomeworkUiModelList
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toImportance
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toListHomeworkModels
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.HomeworkModel
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

    private var selectedCardToChangeColor = -1

    private var isBackButtonClicked = false

    private var selectedHomeworkForDelete: HomeworkModel? = null


    fun parseArguments(subjectId: Long) {
        this.subjectId = subjectId
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
                            turnOffCardEditMode()
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

    private fun checkFilterActions() {
        val importanceFilter = deviceManager.getFilterImportance()
        if (importanceFilter == -1) {
            deviceManager.setFilterImportance(Constants.FILTER_IMPORTANCE_DESCENDING.ordinal)
        }

        if (importanceFilter == Constants.FILTER_IMPORTANCE_DESCENDING.ordinal) {
            Log.d("ytutyuytutyu", "FILTER_IMPORTANCE_DESCENDING ")
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
//        val newList = homeworkList.sortedWith(
//            compareBy<HomeworkUiModel> { homework ->
//                if (sortByDescendingImportance) -homework.importance else homework.importance
//            }.thenBy { homework ->
//                if (sortByDescendingId) -homework.id else homework.id
//            }
//        )
//        val kanbanItemsList = _uiState.value.kanbanItemsList.map {
//            it.copy(
//                columnItems = it.columnItems.sortedWith(
//                    compareBy<HomeworkUiModel> { homework ->
//                        if (sortByDescendingImportance) -homework.importance else homework.importance
//                    }.thenBy { homework ->
//                        if (sortByDescendingId) -homework.id else homework.id
//                    }
//                )
//            )
//        }
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
                homeworkRepository.updateHomework(
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
            }
        )

    private fun deleteSingleHomework(homeworkModel: HomeworkModel) = viewModelScope.execute(
        source = {
            homeworkRepository.deleteHomework(homeworkModel)
        },
        onSuccess = {
            selectedHomeworkForDelete = null
        }
    )

    private fun checkUsage() {
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
                homeworkRepository.deleteListHomework(listForDelete.toListHomeworkModels())
            },
            onSuccess = {
                _uiState.update {
                    it.copy(
                        isEditModeEnabled = false
                    )
                }
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

    private fun getHomework(
        subjectId: Long,
        sortByDescendingImportance: Boolean,
    ) {
        viewModelScope.execute(
            source = {
                homeworkRepository.getHomeworkWithSubjectById(
                    subjectId = subjectId
                )
            },
            onSuccess = { subjectWithHomeworkFlow ->
                viewModelScope.launch {
                    subjectWithHomeworkFlow.collect { subjectWithHomework ->
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
                                        color = stage.color,
                                        itemsCount = subjectWithHomework.homework.filter { homework ->
                                            homework.stageId == stage.id
                                        }.size.toString()
                                    )
                                }
                                Log.d("gfdgdfgdfg", "getHomework: ${subjectWithHomework.homework}")
                                _uiState.update {
                                    it.copy(
                                        subjectName = subjectWithHomework.subject.subjectName,
                                        kanbanItemsList = stageUiModelList.map { stage ->
                                            KanbanItem(
                                                rowItem = stage,
                                                columnItems = if (sortByDescendingImportance) {
                                                    subjectWithHomework.homework.filter { homeworkModel ->
                                                        homeworkModel.stageId == stage.id
                                                    }.toHomeworkUiModelList()
                                                        .sortedByDescending { homeworkModel ->
                                                            homeworkModel.importance
                                                        }
                                                } else {
                                                    subjectWithHomework.homework.filter { homeworkModel ->
                                                        homeworkModel.stageId == stage.id
                                                    }.toHomeworkUiModelList()
                                                        .sortedByDescending { homeworkModel ->
                                                            homeworkModel.id
                                                        }
                                                },
                                            )

                                        },
                                        homeworkList = if (sortByDescendingImportance) {
                                            subjectWithHomework.homework.toHomeworkUiModelList()
                                                .sortedByDescending { homeworkModel ->
                                                    homeworkModel.importance
                                                }
                                        } else {
                                            subjectWithHomework.homework.toHomeworkUiModelList()
                                                .sortedByDescending { homeworkModel ->
                                                    homeworkModel.id
                                                }
                                        },
                                        isLoading = false,
                                        isScreenEmpty = false
                                    )
                                }
                                getDisplayMethod()
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
                )

                viewModelScope.execute(
                    source = {
                        homeworkRepository.updateHomework(
                            homework = homeworkModel.toHomeworkModel()
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