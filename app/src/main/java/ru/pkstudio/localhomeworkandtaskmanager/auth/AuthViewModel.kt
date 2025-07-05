package ru.pkstudio.localhomeworkandtaskmanager.auth

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.pkstudio.localhomeworkandtaskmanager.R
import ru.pkstudio.localhomeworkandtaskmanager.auth.utils.AuthAction
import ru.pkstudio.localhomeworkandtaskmanager.auth.utils.CurrentAuthGreetingAction
import ru.pkstudio.localhomeworkandtaskmanager.core.data.util.SingleSharedFlow
import ru.pkstudio.localhomeworkandtaskmanager.core.domain.manager.DeviceManager
import ru.pkstudio.localhomeworkandtaskmanager.core.domain.manager.ResourceManager
import ru.pkstudio.localhomeworkandtaskmanager.core.navigation.Destination
import ru.pkstudio.localhomeworkandtaskmanager.core.navigation.Navigator
import ru.pkstudio.localhomeworkandtaskmanager.core.util.Constants
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val deviceManager: DeviceManager,
    private val resourceManager: ResourceManager,
    private val navigator: Navigator,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AuthUiState(
            listUiThemes = listOf(
                resourceManager.getString(R.string.enable_system_theme),
                resourceManager.getString(R.string.enable_light_theme),
                resourceManager.getString(R.string.enable_dark_theme),
            )
        )
    )
    val uiState = _uiState
        .onStart {
            _uiAction.tryEmit(AuthUiAction.GetInitialData)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = AuthUiState()
        )

    private val _uiAction = SingleSharedFlow<AuthUiAction>()
    val uiAction = _uiAction.asSharedFlow()

    private var firstEnteredPin = ""

    fun handleIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.OnKeyboardClicked -> {
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
                                if (_uiState.value.isCreatePin) {
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
                                            viewModelScope.launch {
                                                deviceManager.setPinCode(pinCode = newString)
                                                pinSuccess()
                                            }
                                        } else {
                                            pinError()
                                        }
                                    }
                                } else {
                                    viewModelScope.launch {
                                        if (validatePinCode(newString)) {
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

            is AuthIntent.SetDiaryUsage -> {
                viewModelScope.launch {
                    deviceManager.setUsage(Constants.DIARY.ordinal)
                    selectThemeAction()
                }
            }

            is AuthIntent.SetTaskTrackerUsage -> {
                viewModelScope.launch {
                    deviceManager.setUsage(Constants.TASK_TRACKER.ordinal)
                    selectThemeAction()
                }
            }

            is AuthIntent.SetThemeId -> {
                if (intent.themeId in _uiState.value.listUiThemes.indices) {
                    viewModelScope.launch {
                        when (_uiState.value.listUiThemes[intent.themeId]) {
                            resourceManager.getString(R.string.enable_system_theme) -> {
                                delay(100)
                                _uiAction.tryEmit(
                                    AuthUiAction.SetSystemTheme
                                )
                            }

                            resourceManager.getString(R.string.enable_light_theme) -> {
                                delay(100)
                                _uiAction.tryEmit(AuthUiAction.SetLightTheme)
                            }

                            resourceManager.getString(R.string.enable_dark_theme) -> {
                                delay(100)
                                _uiAction.tryEmit(AuthUiAction.SetDarkTheme)
                            }
                        }
                    }

                }

            }

            is AuthIntent.OnThemeSelected -> {
                checkPinCode()
            }

            is AuthIntent.OnBackBtnClicked -> {
                when (_uiState.value.currentAuthAction) {
                    AuthAction.SELECT_THEME -> {
                        selectUsageAction()
                    }

                    else -> {
                        _uiAction.tryEmit(AuthUiAction.FinishActivity)
                    }
                }
            }

            is AuthIntent.GetInitialData -> {
                isFirstLaunch()
            }

            is AuthIntent.SendLifecycleInfo -> {
                if (_uiState.value.isFirstLaunch){
                    when (intent.event) {
                        Lifecycle.Event.ON_RESUME -> {
                            if (job != null){
                                job?.cancel()
                                if (!job!!.isActive){
                                    job = null
                                    startOrResumeGreeting()
                                }
                            }
                        }

                        Lifecycle.Event.ON_PAUSE -> {
                            stopGreeting()
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun pinSuccess() {
        _uiState.update {
            it.copy(
                isSuccess = true
            )
        }
        viewModelScope.launch {
            delay(500)
            navigator.navigate(
                destination = Destination.MainGraph,
                navOptions = {
                    popUpTo<Destination.AuthGraph> {
                        inclusive = true
                    }
                }
            )
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


    private fun isFirstLaunch() = viewModelScope.launch {
        val isFirstLaunch = deviceManager.getIsFirstLaunch()
        val lastAuthAction = deviceManager.getLastAuthAction()
        _uiState.update {
            it.copy(
                isFirstLaunch = isFirstLaunch
            )
        }
        if (isFirstLaunch) {
            startOrResumeGreeting()
        } else {
            when (lastAuthAction) {
                AuthAction.SELECT_USAGE.ordinal -> {
                    selectUsageAction()
                }

                AuthAction.SELECT_THEME.ordinal -> {
                    selectThemeAction()
                }

                else -> {
                    checkPinCode()
                }
            }
        }
    }


    private var job: Job? = null
    private var isGreetingRunning = false
    private var previousGreetingAction = CurrentAuthGreetingAction.START
    private val currentGreetingAction = MutableStateFlow(CurrentAuthGreetingAction.START)
    private fun startOrResumeGreeting() {
        Log.d("fgghfghdfhfgh", "startOrResumeGreeting: ${currentGreetingAction.value}")
        isGreetingRunning = true
        job = viewModelScope.launch {
            val allStates = CurrentAuthGreetingAction.entries

            val startIndex = allStates.indexOf(currentGreetingAction.value)
            for (i in startIndex until allStates.size) {
                delay(1500)
                when (allStates[i]) {
                    CurrentAuthGreetingAction.START -> {
                        Log.d("fgghfghdfhfgh", "CurrentAuthGreetingAction.START")
                        _uiState.update {
                            it.copy(
                                isText1Visible = true
                            )
                        }
                        currentGreetingAction.value = CurrentAuthGreetingAction.TEXT_1
                        previousGreetingAction = CurrentAuthGreetingAction.START
                    }

                    CurrentAuthGreetingAction.TEXT_1 -> {
                        Log.d("fgghfghdfhfgh", "CurrentAuthGreetingAction.TEXT_1")
                        _uiState.update {
                            it.copy(
                                isText1Visible = false
                            )
                        }
                        delay(700)
                        _uiState.update {
                            it.copy(
                                isText2Visible = true
                            )
                        }
                        currentGreetingAction.value = CurrentAuthGreetingAction.TEXT_2
                        previousGreetingAction = CurrentAuthGreetingAction.TEXT_1
                    }


                    CurrentAuthGreetingAction.TEXT_2 -> {
                        Log.d("fgghfghdfhfgh", "CCurrentAuthGreetingAction.TEXT_2")
                        _uiState.update {
                            it.copy(
                                isText2Visible = false
                            )
                        }
                        delay(700)
                        _uiState.update {
                            it.copy(
                                isText3Visible = true
                            )
                        }
                        currentGreetingAction.value = CurrentAuthGreetingAction.TEXT_3
                        previousGreetingAction = CurrentAuthGreetingAction.TEXT_2
                    }

                    CurrentAuthGreetingAction.TEXT_3 -> {
                        Log.d("fgghfghdfhfgh", "CCurrentAuthGreetingAction.TEXT_3")
                        _uiState.update {
                            it.copy(
                                isText2Visible = false
                            )
                        }
                        delay(700)
                        _uiState.update {
                            it.copy(
                                isText3Visible = true
                            )
                        }
                        currentGreetingAction.value = CurrentAuthGreetingAction.END
                        previousGreetingAction = CurrentAuthGreetingAction.TEXT_3
                    }

                    CurrentAuthGreetingAction.END -> {
                        Log.d("fgghfghdfhfgh", "CCurrentAuthGreetingAction.END")
                        _uiState.update {
                            it.copy(
                                isText3Visible = false
                            )
                        }
                        delay(700)
                        selectUsageAction()
                        previousGreetingAction = CurrentAuthGreetingAction.END
                    }

                    CurrentAuthGreetingAction.STOP -> {
                        Log.d("fgghfghdfhfgh", "CCurrentAuthGreetingAction.STOP")
                        isGreetingRunning = false
                        break
                    }
                }
            }
        }
    }

    private fun stopGreeting() {
        isGreetingRunning = false
        job?.cancel()
    }

    private fun checkPinCode() = viewModelScope.launch {
        deviceManager.setLastAuthAction(AuthAction.SET_PIN.ordinal)
        val pin = deviceManager.getPinCode()
        if (pin.isEmpty()) {
            _uiState.update {
                it.copy(
                    currentAuthAction = AuthAction.SET_PIN,
                    isCreatePin = true,
                    titleText = resourceManager.getString(R.string.create_password)
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    currentAuthAction = AuthAction.SET_PIN,
                    isCreatePin = false,
                    titleText = resourceManager.getString(R.string.enter_password)
                )
            }
        }
    }

    private fun selectThemeAction() = viewModelScope.launch {
        deviceManager.setLastAuthAction(AuthAction.SELECT_THEME.ordinal)
        _uiState.update {
            it.copy(
                currentAuthAction = AuthAction.SELECT_THEME
            )
        }
    }

    private fun selectUsageAction() = viewModelScope.launch {
        deviceManager.setIsFirstLaunch(false)
        deviceManager.setLastAuthAction(AuthAction.SELECT_USAGE.ordinal)
        _uiState.update {
            it.copy(
                currentAuthAction = AuthAction.SELECT_USAGE,
                isFirstLaunch = false
            )
        }
    }

    private suspend fun validatePinCode(pin: String): Boolean {
        return deviceManager.getPinCode() == pin
    }

    override fun onCleared() {
        super.onCleared()
        stopGreeting()
    }
}