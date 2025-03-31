package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.settingsScreen

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.pkstudio.localhomeworkandtaskmanager.core.domain.manager.DeviceManager
import ru.pkstudio.localhomeworkandtaskmanager.core.navigation.Destination
import ru.pkstudio.localhomeworkandtaskmanager.core.navigation.Navigator
import ru.pkstudio.localhomeworkandtaskmanager.core.util.ThemeConstants
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val navigator: Navigator,
    deviceManager: DeviceManager
) : ViewModel() {

    private var isNavigateBtnClicked = false

    private val _uiState = MutableStateFlow(
        SettingsState(
            isDynamicColorAvailable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
        )
    )
    val uiState = _uiState
        .onStart {
            val themeId = deviceManager.getTheme()
            when(themeId) {
                ThemeConstants.SYSTEM_DEFAULTS.ordinal -> {
                    _uiState.update {
                        it.copy(
                            isDarkTheme = false,
                            isDarkThemeBtnEnabled = false,
                            isLightTheme = false,
                            isLightThemeBtnEnabled = false,
                            isSystemTheme = true,
                            isDynamicColor = false
                        )
                    }
                }
                ThemeConstants.DYNAMIC_COLORS.ordinal -> {
                    _uiState.update {
                        it.copy(
                            isDarkTheme = false,
                            isDarkThemeBtnEnabled = true,
                            isLightTheme = false,
                            isLightThemeBtnEnabled = true,
                            isSystemTheme = false,
                            isDynamicColor = true
                        )
                    }
                }
                ThemeConstants.LIGHT_THEME.ordinal -> {
                    _uiState.update {
                        it.copy(
                            isDarkTheme = false,
                            isLightTheme = true,
                            isLightThemeBtnEnabled = true,
                            isDarkThemeBtnEnabled = true,
                            isSystemTheme = false,
                            isDynamicColor = false
                        )
                    }
                }
                ThemeConstants.DARK_THEME.ordinal -> {
                    _uiState.update {
                        it.copy(
                            isDarkTheme = true,
                            isLightTheme = false,
                            isLightThemeBtnEnabled = true,
                            isDarkThemeBtnEnabled = true,
                            isSystemTheme = false,
                            isDynamicColor = false
                        )
                    }
                }
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

            is SettingsIntent.NavigateUp -> {
                if (!isNavigateBtnClicked) {
                    viewModelScope.launch {
                        navigator.navigateUp()
                    }
                    isNavigateBtnClicked = true
                }
            }

            is SettingsIntent.SetDarkTheme -> {
                if (!_uiState.value.isDarkTheme){
                    _uiState.update {
                        it.copy(
                            isDarkTheme = true,
                            isLightTheme = false,
                            isSystemTheme = false,
                            isDynamicColor = false
                        )
                    }
                }

            }
            is SettingsIntent.SetDynamicColors -> {
                if (!_uiState.value.isDynamicColor){
                    _uiState.update {
                        it.copy(
                            isDarkTheme = false,
                            isDarkThemeBtnEnabled = true,
                            isLightTheme = false,
                            isLightThemeBtnEnabled = true,
                            isSystemTheme = false,
                            isDynamicColor = true
                        )
                    }
                }
            }
            is SettingsIntent.SetLightTheme -> {
                if (!_uiState.value.isLightTheme){
                    _uiState.update {
                        it.copy(
                            isDarkTheme = false,
                            isLightTheme = true,
                            isSystemTheme = false,
                            isDynamicColor = false
                        )
                    }
                }
            }
            is SettingsIntent.SetSystemTheme -> {
                if (_uiState.value.isSystemTheme) {
                    val darkTheme = intent.isSystemInDarkMode
                    val lightTheme = !intent.isSystemInDarkMode
                    _uiState.update {
                        it.copy(
                            isDarkTheme = darkTheme,
                            isDarkThemeBtnEnabled = true,
                            isLightTheme = lightTheme,
                            isLightThemeBtnEnabled = true,
                            isSystemTheme = false,
                            isDynamicColor = false
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isDarkTheme = false,
                            isDarkThemeBtnEnabled = false,
                            isLightTheme = false,
                            isLightThemeBtnEnabled = false,
                            isSystemTheme = true,
                            isDynamicColor = false
                        )
                    }
                }

            }
        }
    }
}