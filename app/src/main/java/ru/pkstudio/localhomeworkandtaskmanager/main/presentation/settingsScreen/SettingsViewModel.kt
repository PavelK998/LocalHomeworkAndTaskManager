package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.settingsScreen

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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
import ru.pkstudio.localhomeworkandtaskmanager.core.util.ThemeConstants
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository.FilesHandleRepository
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository.ImportExportDbRepository
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val navigator: Navigator,
    private val deviceManager: DeviceManager,
    private val resourceManager: ResourceManager,
    private val importExportDbRepository: ImportExportDbRepository,
    private val filesHandleRepository: FilesHandleRepository,
) : ViewModel() {

    private var isNavigateBtnClicked = false
    private var isNavigateToEditStagesClicked = false
    private var firstEnteredPin = ""

    private val _uiAction = SingleSharedFlow<SettingsUIAction>()
    val uiAction = _uiAction.asSharedFlow()

    private val _uiState = MutableStateFlow(
        SettingsState(
            isDynamicColorAvailable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S,
            currentScreen = SETTINGS_MAIN,
            toolbarTitle = resourceManager.getString(R.string.settings),
            titleImportAlertDialog = resourceManager.getString(R.string.import_database_dialog_title),
            commentImportAlertDialog = resourceManager.getString(R.string.import_database_dialog_description),
            titleExportAlertDialog = resourceManager.getString(R.string.export_database_dialog_title),
            commentExportAlertDialog = resourceManager.getString(R.string.export_database_dialog_description)
        )
    )
    val uiState = _uiState
        .onStart {
            val themeId = deviceManager.getTheme()
            val isDynamicColor = deviceManager.getDynamicColors()
            if (isDynamicColor) {
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
            when(themeId) {
                ThemeConstants.SYSTEM_DEFAULTS.ordinal -> {
                    _uiState.update {
                        it.copy(
                            isDarkTheme = false,
                            isDarkThemeBtnEnabled = false,
                            isLightTheme = false,
                            isLightThemeBtnEnabled = false,
                            isSystemTheme = true,
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
                _uiAction.tryEmit(SettingsUIAction.SetDarkTheme)
                if (!_uiState.value.isDarkTheme){
                    _uiState.update {
                        it.copy(
                            isDarkTheme = true,
                            isLightTheme = false,
                            isSystemTheme = false,
                        )
                    }
                }

            }
            is SettingsIntent.SetDynamicColors -> {
                _uiAction.tryEmit(SettingsUIAction.SetDynamicColors)
                if (!_uiState.value.isDynamicColor){
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
            }
            is SettingsIntent.SetLightTheme -> {
                _uiAction.tryEmit(SettingsUIAction.SetLightTheme)
                if (!_uiState.value.isLightTheme){
                    _uiState.update {
                        it.copy(
                            isDarkTheme = false,
                            isLightTheme = true,
                            isSystemTheme = false,
                        )
                    }
                }
            }
            is SettingsIntent.SetSystemTheme -> {
                _uiAction.tryEmit(SettingsUIAction.SetSystemTheme(isSystemInDarkMode = intent.isSystemInDarkMode))
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
                        )
                    }
                }
            }

            is SettingsIntent.OnChangePasswordClicked -> {
                _uiState.update {
                    it.copy(
                        toolbarTitle = "",
                        currentScreen = PASSWORD_CHANGE_SCREEN,
                        titleText = if (_uiState.value.shouldEnterPassword) {
                            resourceManager.getString(R.string.enter_current_password)
                        } else {
                            resourceManager.getString(R.string.create_password)
                        }
                    )
                }
            }

            is SettingsIntent.OnKanbanSettingsClicked -> {
                if (!isNavigateToEditStagesClicked) {
                    viewModelScope.launch {
                        navigator.navigate(
                            Destination.StageEditScreen
                        )
                        delay(300)
                        isNavigateToEditStagesClicked = false
                    }
                    isNavigateToEditStagesClicked = true
                }
            }

            is SettingsIntent.OnSetThemeClicked -> {
                _uiState.update {
                    it.copy(
                        currentScreen = THEME_SCREEN,
                        toolbarTitle = resourceManager.getString(R.string.change_theme_toolbar_title)
                    )
                }
            }

            is SettingsIntent.SetMainScreen -> {
                _uiState.update {
                    it.copy(
                        currentScreen = SETTINGS_MAIN,
                        toolbarTitle = resourceManager.getString(R.string.settings)
                    )
                }
            }

            is SettingsIntent.OnKeyboardClicked -> {
                when (intent.text) {
                    "-" -> {
                        if (_uiState.value.text.isNotBlank()) {
                            val newString = buildString {
                                append(_uiState.value.text)
                                deleteCharAt(_uiState.value.text.length - 1)
                            }
                            _uiState.update {
                                it.copy(
                                    text = newString
                                )
                            }
                        }
                        if (_uiState.value.isError) {
                            _uiState.update {
                                it.copy(
                                    isError = false
                                )
                            }
                        }
                    }

                    "C" -> {
                        _uiState.update {
                            it.copy(
                                text = ""
                            )
                        }
                        if (_uiState.value.isError) {
                            _uiState.update {
                                it.copy(
                                    isError = false
                                )
                            }
                        }
                    }

                    else -> {
                        if (_uiState.value.text.length <= 3) {
                            val newString = _uiState.value.text.plus(intent.text)
                            _uiState.update {
                                it.copy(
                                    text = newString
                                )
                            }
                            if (newString.length == 4) {
                                if (newString == deviceManager.getPinCode() && _uiState.value.shouldEnterPassword) {
                                    viewModelScope.launch {
                                        _uiState.update {
                                            it.copy(
                                                isSuccess = true
                                            )
                                        }
                                        delay(200)
                                        _uiState.update {
                                            it.copy(
                                                titleText = resourceManager.getString(R.string.create_password),
                                                isSuccess = false,
                                                text = "",
                                                shouldEnterPassword = false
                                            )
                                        }
                                    }
                                } else if (newString != deviceManager.getPinCode() && _uiState.value.shouldEnterPassword) {
                                    pinError()
                                } else {
                                    if (firstEnteredPin.isBlank()) {
                                        firstEnteredPin = newString
                                        _uiState.update {
                                            it.copy(
                                                titleText = resourceManager.getString(R.string.repeat_password),
                                                text = ""
                                            )
                                        }
                                    } else {
                                        val isValid = _uiState.value.text == firstEnteredPin
                                        if (isValid) {
                                            deviceManager.setPinCode(pinCode = newString)
                                            pinSuccess()
                                        } else {
                                            pinError()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            is SettingsIntent.OnExportClicked -> {
                _uiState.update {
                    it.copy(
                        isExportAlertDialogOpened = true
                    )
                }
            }

            is SettingsIntent.OnImportClicked -> {
                _uiState.update {
                    it.copy(
                        isImportAlertDialogOpened = true
                    )
                }
            }

            is SettingsIntent.OnFileExportPathSelected -> {
                viewModelScope.execute(
                    source = {
                        deviceManager.setFilePathUri(intent.uri.toString())
                    },
                    onSuccess = {
                        exportDb(intent.uri)
                    }
                )
            }

            is SettingsIntent.OnFileImportPathSelected -> {
                importDb(intent.uri)
            }

            is SettingsIntent.CloseImportDialog -> {
                _uiState.update {
                    it.copy(
                        isImportAlertDialogOpened = false
                    )
                }
            }

            is SettingsIntent.ImportConfirmed -> {
                _uiState.update {
                    it.copy(
                        isImportAlertDialogOpened = false
                    )
                }
                _uiAction.tryEmit(SettingsUIAction.SelectDatabaseFile)
            }

            is SettingsIntent.CloseExportDialog -> {
                _uiState.update {
                    it.copy(
                        isExportAlertDialogOpened = false
                    )
                }
            }

            is SettingsIntent.ExportConfirmed -> {
                viewModelScope.execute(
                    source = {
                        deviceManager.getFilePathUri()
                    },
                    onSuccess = { filePath ->
                        if (!filePath.isNullOrEmpty()) {
                            viewModelScope.execute(
                                source = {
                                    filesHandleRepository.checkUriPermission(filePath.toUri())
                                },
                                onSuccess = {
                                    exportDb(filePath.toUri())
                                },
                                onError = {
                                    _uiAction.tryEmit(SettingsUIAction.OpenDocumentTree)
                                }
                            )
                        } else {
                            _uiAction.tryEmit(SettingsUIAction.OpenDocumentTree)
                        }







                    }
                )
            }
        }
    }

    private fun exportDb(uri: Uri) = viewModelScope.execute(
        source = {
            Log.d("rtytrytryrtytryrt", "exportDb: $uri")
            importExportDbRepository.exportDatabase(uri)
        },
        onSuccess = {
            _uiAction.tryEmit(SettingsUIAction.RestartApp)

        },
        onError = {
            Log.d("rtytrytryrtytryrt", "exportDb error: $it")
        }
    )

    private fun importDb(uri: Uri) = viewModelScope.execute(
        source = {
            importExportDbRepository.importDatabase(uri)
        },
        onSuccess = {
            _uiAction.tryEmit(SettingsUIAction.RestartApp)

        },
        onError = {

        }
    )

    private fun pinSuccess() {
        _uiState.update {
            it.copy(
                isSuccess = true
            )
        }
        viewModelScope.launch {
            delay(500)
            _uiAction.tryEmit(SettingsUIAction.ShowError(resourceManager.getString(R.string.password_changed)))
            handleIntent(SettingsIntent.SetMainScreen)
            _uiState.update {
                it.copy(
                    isSuccess = false,
                    shouldEnterPassword = true
                )
            }
        }
    }

    private fun pinError() {
        _uiState.update {
            it.copy(
                isError = true
            )
        }
        deviceManager.startMicroVibrate()
    }
    companion object {
        const val SETTINGS_MAIN = "settings_main"
        const val THEME_SCREEN = "theme"
        const val PASSWORD_CHANGE_SCREEN = "change_password"
    }
}