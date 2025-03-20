package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class HomeworkListViewModel : ViewModel() {

    private var hasLoadedInitialData = false

    private val _uiState = MutableStateFlow(HomeworkListState())
    val uiState = _uiState
        .onStart {
            if (!hasLoadedInitialData) {
                /** Load initial data here **/
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = HomeworkListState()
        )

    fun onAction(intent: HomeworkListIntent) {
        when (intent) {
            else -> {

            }
        }
    }

}