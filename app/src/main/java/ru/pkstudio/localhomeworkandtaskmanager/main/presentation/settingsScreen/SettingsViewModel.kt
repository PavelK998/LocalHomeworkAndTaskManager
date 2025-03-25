package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.settingsScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.pkstudio.localhomeworkandtaskmanager.core.navigation.Destination
import ru.pkstudio.localhomeworkandtaskmanager.core.navigation.Navigator
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val navigator: Navigator
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val _uiState = MutableStateFlow(SettingsState())
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
            initialValue = SettingsState()
        )

    fun handleIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.OnEditStagesClicked -> {
                viewModelScope.launch {
                    navigator.navigate(Destination.StageEditScreen)
                }
            }
        }
    }

}