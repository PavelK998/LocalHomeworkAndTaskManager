package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkInfo

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.pkstudio.localhomeworkandtaskmanager.R
import ru.pkstudio.localhomeworkandtaskmanager.core.domain.manager.ResourceManager
import ru.pkstudio.localhomeworkandtaskmanager.core.extensions.execute
import ru.pkstudio.localhomeworkandtaskmanager.core.navigation.Navigator
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toHomeworkModel
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toHomeworkUiModel
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toSubjectUiModel
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.HomeworkModel
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository.HomeworkRepository
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository.StageRepository
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository.SubjectsRepository
import javax.inject.Inject

@HiltViewModel
class HomeworkInfoViewModel @Inject constructor(
    private val homeworkRepository: HomeworkRepository,
    private val subjectsRepository: SubjectsRepository,
    private val stageRepository: StageRepository,
    private val resourceManager: ResourceManager,
    private val navigator: Navigator
) : ViewModel() {
    private var homeworkId: Long = 0L

    fun parseArguments(homeworkId: Long, subjectId: Long) {
        this.homeworkId = homeworkId
        getInitialData(homeworkId = homeworkId, subjectId = subjectId)
    }

    private val _uiState = MutableStateFlow(
        HomeworkInfoState(
            deleteDialogTitle = resourceManager.getString(R.string.delete_dialog_title),
            deleteDialogDescription = resourceManager.getString(R.string.delete_homework_dialog_description),
        )
    )
    val uiState = _uiState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = HomeworkInfoState()
        )

    fun handleIntent(intent: HomeworkInfoIntent) {
        when (intent) {
            is HomeworkInfoIntent.OnStageSelectClick -> {
                if (_uiState.value.isStageMenuOpened) {
                    _uiState.update {
                        it.copy(
                            isStageMenuOpened = false
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isStageMenuOpened = true
                        )
                    }
                }
            }

            is HomeworkInfoIntent.CloseStageMenu -> {
                _uiState.update {
                    it.copy(
                        isStageMenuOpened = false
                    )
                }
            }

            is HomeworkInfoIntent.OnMenuItemClick -> {
                if (
                    intent.index in _uiState.value.stagesList.indices
                    && _uiState.value.homeworkUiModel != null
                ) {
                    changeStage(
                        homeworkModel = _uiState.value.homeworkUiModel!!.toHomeworkModel(),
                        newStageId = intent.stageId
                    )
                }
            }

            is HomeworkInfoIntent.OnHomeworkEditDescriptionChange -> {
                _uiState.update {
                    it.copy(
                        homeworkEditDescription = intent.text
                    )
                }
            }

            is HomeworkInfoIntent.OnHomeworkEditNameChange -> {
                _uiState.update {
                    it.copy(
                        homeworkEditName = intent.text
                    )
                }
            }

            is HomeworkInfoIntent.CloseDeleteAlertDialog -> {
                _uiState.update {
                    it.copy(
                        isDeleteDialogOpened = false
                    )
                }
            }

            is HomeworkInfoIntent.DeleteConfirm -> {
                _uiState.value.homeworkUiModel?.let {
                    deleteHomework(it.toHomeworkModel())
                }
            }

            is HomeworkInfoIntent.OnDeleteBtnClick -> {
                _uiState.update {
                    it.copy(
                        isDeleteDialogOpened = true,
                        isSettingsMenuOpened = false
                    )
                }
            }

            is HomeworkInfoIntent.NavigateUp -> {
                viewModelScope.launch {
                    navigator.navigateUp()
                }
            }

            is HomeworkInfoIntent.CloseSettingsMenu -> {
                _uiState.update {
                    it.copy(
                        isSettingsMenuOpened = false
                    )
                }
            }

            is HomeworkInfoIntent.OnSettingsClicked -> {
                _uiState.update {
                    it.copy(
                        isSettingsMenuOpened = true
                    )
                }
            }

            is HomeworkInfoIntent.OnEditClick -> {
                _uiState.update {
                    it.copy(
                        isEditMode = true,
                        isSettingsMenuOpened = false,
                        homeworkEditName = _uiState.value.homeworkUiModel?.name ?: "",
                        homeworkEditDescription = _uiState.value.homeworkUiModel?.description ?: ""
                    )
                }
            }

            is HomeworkInfoIntent.DismissEditMode -> {
                _uiState.update {
                    it.copy(
                        isEditMode = false
                    )
                }
            }

            is HomeworkInfoIntent.ConfirmEditResult -> {
                _uiState.value.homeworkUiModel?.let {
                    updateHomework(
                        homeworkModel = it.toHomeworkModel(),
                        newName = _uiState.value.homeworkEditName.trimEnd(),
                        newDescription = _uiState.value.homeworkEditDescription.trimEnd()
                    )
                }
            }
        }
    }

    private fun deleteHomework(homeworkModel: HomeworkModel) = viewModelScope.execute(
        source = {
            homeworkRepository.deleteHomework(homeworkModel)
        },
        onSuccess = {
            handleIntent(HomeworkInfoIntent.NavigateUp)
        }
    )

    private fun updateHomework(homeworkModel: HomeworkModel, newName: String, newDescription: String) =
        viewModelScope.execute(
            source = {
                homeworkRepository.updateHomework(
                    homework = homeworkModel.copy(
                        name = newName,
                        description = newDescription
                    )
                )
            },
            onSuccess = {
                getHomework(homeworkId = homeworkId)
                Log.d("gdfgdfgdfgdf", "update: success")
            }
        )

    private fun changeStage(homeworkModel: HomeworkModel, newStageId: Long) =
        viewModelScope.execute(
            source = {
                homeworkRepository.updateHomework(
                    homework = homeworkModel.copy(
                        stageId = newStageId
                    )
                )
            },
            onSuccess = {
                _uiState.update {
                    it.copy(
                        currentSelectStageName = _uiState.value.stagesList.find { stage ->
                            stage.id == newStageId
                        }?.stageName ?: ""
                    )
                }
                Log.d("gdfgdfgdfgdf", "changeStage: success")
            }
        )

    private fun getHomework(homeworkId: Long) {
        _uiState.update {
            it.copy(
                isLoading = true
            )
        }
        viewModelScope.execute(
            source = {
                homeworkRepository.getHomeworkById(homeworkId)
            },
            onSuccess = { homeworkModel ->
                _uiState.update {
                    it.copy(
                        homeworkUiModel = homeworkModel.toHomeworkUiModel(),
                        isLoading = false
                    )
                }
                handleIntent(HomeworkInfoIntent.DismissEditMode)
            }
        )
    }
    private fun getInitialData(homeworkId: Long, subjectId: Long) {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            Log.d("jdgfhfghfgfghjhg", "getInitialData error: $throwable")
        }
        viewModelScope.launch(exceptionHandler) {
            _uiState.update {
                it.copy(
                    isLoading = true
                )
            }
            val subject = async {
                subjectsRepository.getSubjectById(subjectId)
            }
            val homework = async {
                homeworkRepository.getHomeworkById(homeworkId)
            }
            val stages = async {
                stageRepository.getAllStagesSingleTime()
            }

            val homeworkResult = homework.await().toHomeworkUiModel()
            val stagesResult = stages.await()
            val currentStageName = if (stagesResult.isNotEmpty()) {
                stagesResult.find { stageModel ->
                    stageModel.id == homeworkResult.stageId
                }?.stageName
            } else {
                ""
            }
            val subjectResult = subject.await().toSubjectUiModel()
            Log.d("nbcvnvbnbvn", "getInitialData: stages $stagesResult")

            _uiState.update {
                it.copy(
                    homeworkUiModel = homeworkResult,
                    addDateText = "${resourceManager.getString(R.string.add_date)} ${homeworkResult.addDate}",
                    currentSelectStageName = currentStageName ?: "",
                    subjectUiModel = subjectResult,
                    subjectNameText = "${resourceManager.getString(R.string.subject_name)} ${subjectResult.subjectName}",
                    stagesList = stagesResult,
                    isLoading = false
                )
            }
        }
    }
}