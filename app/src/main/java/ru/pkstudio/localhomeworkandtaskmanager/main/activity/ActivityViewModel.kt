package ru.pkstudio.localhomeworkandtaskmanager.main.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import ru.pkstudio.localhomeworkandtaskmanager.core.domain.manager.DeviceManager
import ru.pkstudio.localhomeworkandtaskmanager.core.util.ThemeConstants
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val deviceManager: DeviceManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ActivityState())

    val uiState = _uiState
        .onStart {
            val themeId = getThemeId()
            val isDynamicColors = deviceManager.getDynamicColors()
            if (isDynamicColors) {
                _uiState.update {
                    it.copy(
                        isDynamicColor = true
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isDynamicColor = false
                    )
                }
            }

            when (themeId) {
                ThemeConstants.SYSTEM_DEFAULTS.ordinal -> {
                    _uiState.update {
                        it.copy(
                            isDarkTheme = false,
                            isSystemTheme = true,
                            isDynamicColor = false,
                            isReady = true
                        )
                    }
                }
                ThemeConstants.LIGHT_THEME.ordinal -> {
                    _uiState.update {
                        it.copy(
                            isDarkTheme = false,
                            isReady = true
                        )
                    }
                }
                ThemeConstants.DARK_THEME.ordinal -> {
                    _uiState.update {
                        it.copy(
                            isDarkTheme = true,
                            isReady = true
                        )
                    }
                }
                else -> {
                    deviceManager.setTheme(ThemeConstants.SYSTEM_DEFAULTS.ordinal)
                    _uiState.update {
                        it.copy(
                            isDarkTheme = false,
                            isSystemTheme = true,
                            isDynamicColor = false,
                            isReady = true
                        )
                    }
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ActivityState()
        )

    private fun getThemeId():Int {
        return deviceManager.getTheme()
    }


    fun toggleDarkTheme() {
        deviceManager.setTheme(ThemeConstants.DARK_THEME.ordinal)
        _uiState.update {
            it.copy(
                isDarkTheme = true,
            )
        }
    }

    fun toggleLightTheme() {
        deviceManager.setTheme(ThemeConstants.LIGHT_THEME.ordinal)
        _uiState.update {
            it.copy(
                isDarkTheme = false
            )
        }
    }

    fun toggleDynamicColors() {
        if (_uiState.value.isDynamicColor){
            deviceManager.setDynamicColors(false)
            _uiState.update {
                it.copy(
                    isDynamicColor = false
                )
            }
        } else {
            deviceManager.setDynamicColors(true)
            _uiState.update {
                it.copy(
                    isDynamicColor = true
                )
            }
        }
    }

    fun toggleSystemTheme(isSystemInDarkMode: Boolean) {
        if (_uiState.value.isSystemTheme) {
            _uiState.update {
                it.copy(
                    isDarkTheme = isSystemInDarkMode,
                    isSystemTheme = false,
                )
            }
        } else {
            deviceManager.setTheme(ThemeConstants.SYSTEM_DEFAULTS.ordinal)
            _uiState.update {
                it.copy(
                    isDarkTheme = false,
                    isSystemTheme = true,
                )
            }
        }
    }
}