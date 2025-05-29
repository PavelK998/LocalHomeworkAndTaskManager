package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.subjectList

import androidx.compose.ui.graphics.toArgb
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
import ru.pkstudio.localhomeworkandtaskmanager.core.domain.manager.DeviceManager
import ru.pkstudio.localhomeworkandtaskmanager.core.domain.manager.ResourceManager
import ru.pkstudio.localhomeworkandtaskmanager.core.extensions.execute
import ru.pkstudio.localhomeworkandtaskmanager.core.navigation.Destination
import ru.pkstudio.localhomeworkandtaskmanager.core.navigation.Navigator
import ru.pkstudio.localhomeworkandtaskmanager.core.util.Constants
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toSubjectModel
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toSubjectUiModel
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.StageModel
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.SubjectModel
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository.ImportExportDbRepository
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository.StageRepository
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository.SubjectsRepository
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.stageVariant10
import javax.inject.Inject

@HiltViewModel
class SubjectListViewModel @Inject constructor(
    private val subjectsRepository: SubjectsRepository,
    private val navigator: Navigator,
    private val resourceManager: ResourceManager,
    private val deviceManager: DeviceManager,
    private val stageRepository: StageRepository,
    private val importExportDbRepository: ImportExportDbRepository,
) : ViewModel() {

    private var indexItemForDelete = -1



    private val _uiAction = SingleSharedFlow<SubjectListUiAction>()
    val uiAction = _uiAction.asSharedFlow()

    private val _uiState = MutableStateFlow(
        SubjectListState(
            titleDeleteAlertDialog = resourceManager.getString(R.string.delete_dialog_title),
            commentDeleteAlertDialog = resourceManager.getString(R.string.comment_subjects_delete_dialog),
        )
    )
    val uiState = _uiState
        .onStart {
            checkUsage()
            checkStage()
            getData()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = SubjectListState()
        )

    fun handleIntent(intent: SubjectListIntent) {
        when (intent) {
            is SubjectListIntent.AddSubject -> {
                addSubject()
            }

            is SubjectListIntent.ChangeNameSubject -> {
                _uiState.update {
                    it.copy(
                        newSubjectName = intent.text
                    )
                }
            }

            is SubjectListIntent.CloseAddSubject -> {
                _uiState.update {
                    it.copy(
                        isAddSubjectAlertDialogOpened = false
                    )
                }
            }

            is SubjectListIntent.ConfirmDeleteSubject -> {
                if (indexItemForDelete != -1) {
                    deleteSubject(indexItemForDelete)
                }

            }

            is SubjectListIntent.CloseDeleteDialog -> {
                _uiState.update {
                    it.copy(
                        isDeleteAlertDialogOpened = false
                    )
                }
            }

            is SubjectListIntent.DeleteSubject -> {
                indexItemForDelete = intent.index
                _uiState.update {
                    it.copy(
                        isDeleteAlertDialogOpened = true
                    )
                }
            }

            is SubjectListIntent.EditSubject -> {
                updateModel(intent.index)
            }

            is SubjectListIntent.LogOutClicked -> {

            }

            is SubjectListIntent.NavigateToHomeworkScreen -> {
                viewModelScope.launch {
                    navigator.navigate(
                        destination = Destination.HomeworkListScreen(
                            subjectId = intent.subjectId,
                        )
                    )
                }
            }

            is SubjectListIntent.OnEditCommentChanged -> {
                _uiState.update {
                    it.copy(
                        subjectCommentForEdit = intent.text
                    )
                }
            }

            is SubjectListIntent.OnEditTitleChanged -> {
                _uiState.update {
                    it.copy(
                        subjectNameForEdit = intent.text
                    )
                }
            }

            is SubjectListIntent.OnRevealCardOptionsMenuClicked -> {
                revealModel(isRevealed = intent.isRevealed, index = intent.index)
            }

            is SubjectListIntent.OpenAddSubject -> {
                _uiState.update {
                    it.copy(
                        isAddSubjectAlertDialogOpened = true
                    )
                }
            }

            is SubjectListIntent.TurnEditModeOff -> {
                turnOffEditModeForModel(intent.index)
            }

            is SubjectListIntent.TurnEditModeOn -> {
                turnOnEditModeForModel(intent.index)
            }

            is SubjectListIntent.OnSettingClicked -> {
                viewModelScope.launch {
                    navigator.navigate(Destination.SettingsScreen)
                }
            }

            is SubjectListIntent.ChangeCommentSubject -> {
                _uiState.update {
                    it.copy(
                        newSubjectComment = intent.text
                    )
                }
            }

        }
    }

    private fun checkUsage() {
        when(deviceManager.getUsage()) {
            Constants.TASK_TRACKER.ordinal -> {
                _uiState.update {
                    it.copy(
                        toolbarTitle = resourceManager.getString(R.string.categories),
                        titleDeleteAlertDialog = resourceManager.getString(R.string.delete_dialog_title_category),
                        titleAddDialog = resourceManager.getString(R.string.add_new_category)
                    )
                }
            }

            Constants.DIARY.ordinal -> {
                _uiState.update {
                    it.copy(
                        toolbarTitle = resourceManager.getString(R.string.subjects),
                        titleDeleteAlertDialog = resourceManager.getString(R.string.delete_dialog_title),
                        titleAddDialog = resourceManager.getString(R.string.add_new_subject)
                    )
                }
            }
        }
    }
    private fun checkStage() {
        viewModelScope.execute(
            source = {
                stageRepository.getAllStagesSingleTime()
            },
            onSuccess = { stageModelList ->
                if (stageModelList.isEmpty()) {
                    createStage()
                }
            }
        )
    }

    private fun createStage() {
        viewModelScope.execute(
            source = {
                stageRepository.insertStage(
                    stage = StageModel(
                        stageName = resourceManager.getString(R.string.default_stage),
                        position = 0,
                        color = stageVariant10.toArgb()
                    )
                )
            }
        )
    }

    private fun updateModel(index: Int) {
        val subjectsList = _uiState.value.subjectsList.toMutableList()
        if (index in subjectsList.indices) {
            viewModelScope.execute(
                source = {
                    subjectsRepository.updateSubject(
                        subject = subjectsList[index].toSubjectModel().copy(
                            subjectName = _uiState.value.subjectNameForEdit,
                            comment = _uiState.value.subjectCommentForEdit
                        )
                    )
                },
                onSuccess = {
                    subjectsList[index] = subjectsList[index].copy(isEditModeEnabled = false)
                    _uiState.update {
                        it.copy(
                            subjectsList = subjectsList,
                            subjectNameForEdit = "",
                            subjectCommentForEdit = ""
                        )
                    }
                }
            )
        }

    }

    private fun revealModel(isRevealed: Boolean, index: Int) {
        val subjectsList = _uiState.value.subjectsList.toMutableList()
        if (index in subjectsList.indices) {
            if (isRevealed) {
                subjectsList.map {
                    it.copy(
                        isRevealed = false
                    )
                }
                subjectsList[index] = subjectsList[index].copy(isRevealed = true)
                _uiState.update {
                    it.copy(
                        subjectsList = subjectsList
                    )
                }
            } else {
                subjectsList[index] = subjectsList[index].copy(isRevealed = false)
                _uiState.update {
                    it.copy(
                        subjectsList = subjectsList
                    )
                }
            }
        }
    }

    private fun turnOnEditModeForModel(index: Int) {
        if (index in _uiState.value.subjectsList.indices) {
            val listSubjects = _uiState.value.subjectsList.toMutableList()
            listSubjects.map {
                it.copy(
                    isEditModeEnabled = false,
                    isRevealed = false
                )
            }
            listSubjects[index] = listSubjects[index].copy(
                isEditModeEnabled = true,
                isRevealed = false
            )
            _uiState.update {
                it.copy(
                    subjectsList = listSubjects,
                    subjectNameForEdit = listSubjects[index].subjectName,
                    subjectCommentForEdit = listSubjects[index].comment
                )
            }
        }
    }

    private fun turnOffEditModeForModel(index: Int) {
        if (index in _uiState.value.subjectsList.indices) {
            val listSubjects = _uiState.value.subjectsList.toMutableList()
            listSubjects[index] = listSubjects[index].copy(isEditModeEnabled = false)
            _uiState.update {
                it.copy(
                    subjectsList = listSubjects,
                    subjectNameForEdit = "",
                    subjectCommentForEdit = ""
                )
            }
        }
    }

    private fun deleteSubject(index: Int) {
        if (index in _uiState.value.subjectsList.indices) {
            val modelForDelete = _uiState.value.subjectsList[index].toSubjectModel()
            viewModelScope.execute(
                source = {
                    subjectsRepository.deleteSubject(
                        subject = modelForDelete
                    )
                },
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isDeleteAlertDialogOpened = false
                        )
                    }
                },
                onError = {}
            )
        }
    }

    private fun addSubject() {
        if (_uiState.value.newSubjectName.isNotBlank()) {
            viewModelScope.execute(
                source = {
                    subjectsRepository.insertSubject(
                        subject = SubjectModel(
                            id = null,
                            subjectName = _uiState.value.newSubjectName,
                            comment = _uiState.value.newSubjectComment
                        )
                    )
                },
                onSuccess = {

                    _uiState.update {
                        it.copy(
                            isAddSubjectAlertDialogOpened = false,
                            newSubjectName = "",
                            newSubjectComment = ""
                        )
                    }
                },
                onError = {

                }
            )
        } else {
            _uiAction.tryEmit(
                SubjectListUiAction.ShowErrorMessage(
                    resourceManager.getString(
                        R.string.subject_name_empty
                    )
                )
            )
        }

    }

    private fun getData() {
        viewModelScope.launch {
            subjectsRepository.getAllSubjects().collect { subjects ->
                if (subjects.isEmpty()) {
                    _uiState.update {
                        it.copy(
                            isScreenEmpty = true,
                            isLoading = false
                        )
                    }
                } else {
                    if (_uiState.value.isScreenEmpty) {
                        _uiState.update {
                            it.copy(
                                isScreenEmpty = false
                            )
                        }
                    }
                    _uiState.update {
                        it.copy(
                            subjectsList = subjects.map { subject ->
                                subject.toSubjectUiModel()
                            },
                            isLoading = false
                        )
                    }
                }
            }
        }
    }
}