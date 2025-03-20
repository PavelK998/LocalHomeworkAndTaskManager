package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.addHomework

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class AddHomeworkViewModel : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(AddHomeworkState())
    val state = _state
        .onStart {

        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = AddHomeworkState()
        )

    fun handleIntent(intent: AddHomeworkIntent) {
        when (intent) {
            else -> {

            }
        }
    }

}