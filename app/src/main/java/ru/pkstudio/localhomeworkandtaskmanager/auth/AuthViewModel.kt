package ru.pkstudio.localhomeworkandtaskmanager.auth

import android.content.res.Configuration
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
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
import ru.pkstudio.localhomeworkandtaskmanager.auth.utils.AuthAction
import ru.pkstudio.localhomeworkandtaskmanager.core.data.util.SingleSharedFlow
import ru.pkstudio.localhomeworkandtaskmanager.core.domain.manager.DeviceManager
import ru.pkstudio.localhomeworkandtaskmanager.core.domain.manager.ResourceManager
import ru.pkstudio.localhomeworkandtaskmanager.core.domain.manager.VideoPlayerRepository
import ru.pkstudio.localhomeworkandtaskmanager.core.navigation.Destination
import ru.pkstudio.localhomeworkandtaskmanager.core.navigation.Navigator
import ru.pkstudio.localhomeworkandtaskmanager.core.util.Constants
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val deviceManager: DeviceManager,
    private val resourceManager: ResourceManager,
    private val navigator: Navigator,
    private val videoPlayerRepository: VideoPlayerRepository
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

    private val videoListener by lazy {
        object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                if (playbackState == Player.STATE_ENDED) {
                    deviceManager.setIsFirstLaunch(false)
                    selectUsageAction()
                    _uiState.update {
                        it.copy(
                            isFirstLaunch = false,
                        )
                    }
                }
            }
        }
    }

    private val _uiAction = SingleSharedFlow<AuthUiAction>()
    val uiAction = _uiAction.asSharedFlow()

    private var firstEnteredPin = ""

    val player = videoPlayerRepository.currentPlayer

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
                                            deviceManager.setPinCode(pinCode = newString)
                                            pinSuccess()
                                        } else {
                                            pinError()
                                        }
                                    }
                                } else {
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

            is AuthIntent.SetDarkTheme -> {

            }

            is AuthIntent.SetDynamicColors -> {

            }

            is AuthIntent.SetLightTheme -> {

            }

            is AuthIntent.SetSystemTheme -> {

            }

            is AuthIntent.SetDiaryUsage -> {
                deviceManager.setUsage(Constants.DIARY.ordinal)
                selectThemeAction()

            }

            is AuthIntent.SetTaskTrackerUsage -> {
                deviceManager.setUsage(Constants.TASK_TRACKER.ordinal)
                selectThemeAction()
            }

            is AuthIntent.SetThemeId -> {
                Log.d("xzcxzczxczx", "handleIntent: set theme id ${intent.themeId}")
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
                isFirstLaunch(intent.orientation)
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


    private fun isFirstLaunch(orientation: Int) {
        Log.d("fhdgfghfghfg", "landscape: ${orientation == Configuration.ORIENTATION_LANDSCAPE}")
        Log.d("fhdgfghfghfg", "portrait: ${orientation == Configuration.ORIENTATION_PORTRAIT}")
        val isFirstLaunch = deviceManager.getIsFirstLaunch()
        val lastAuthAction = deviceManager.getLastAuthAction()
        _uiState.update {
            it.copy(
                isFirstLaunch = isFirstLaunch
            )
        }
        if (isFirstLaunch) {
            startVideo(orientation)
        } else {
            Log.d("asdsadsadsa", "last: $lastAuthAction")
            when(lastAuthAction) {
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

    private fun startVideo(orientation: Int) = viewModelScope.launch {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            videoPlayerRepository.playVideo(R.raw.intro_landscape)
            player.addListener(
                object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        super.onPlaybackStateChanged(playbackState)
                        if (playbackState == Player.STATE_ENDED) {
                            deviceManager.setIsFirstLaunch(false)
                            _uiAction.tryEmit(AuthUiAction.SetUnspecifiedOrientation)
                            selectUsageAction()
                            _uiState.update {
                                it.copy(
                                    isFirstLaunch = false,
                                )
                            }
                        }
                    }
                }
            )
        } else {
            videoPlayerRepository.playVideo(R.raw.intro)
            player.addListener(
                object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        super.onPlaybackStateChanged(playbackState)
                        if (playbackState == Player.STATE_ENDED) {
                            deviceManager.setIsFirstLaunch(false)
                            _uiAction.tryEmit(AuthUiAction.SetUnspecifiedOrientation)
                            selectUsageAction()
                            _uiState.update {
                                it.copy(
                                    isFirstLaunch = false,
                                )
                            }
                        }
                    }
                }
            )
        }

    }


    private fun checkPinCode() {
        deviceManager.setLastAuthAction(AuthAction.SET_PIN.ordinal)
        val pin = deviceManager.getPinCode()
        if (pin.isNullOrBlank()) {
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

    private fun selectThemeAction() {
        deviceManager.setLastAuthAction(AuthAction.SELECT_THEME.ordinal)
        _uiState.update {
            it.copy(
                currentAuthAction = AuthAction.SELECT_THEME
            )
        }
    }

    private fun selectUsageAction() {
        deviceManager.setLastAuthAction(AuthAction.SELECT_USAGE.ordinal)
        _uiState.update {
            it.copy(
                currentAuthAction = AuthAction.SELECT_USAGE
            )
        }
    }

    private fun validatePinCode(pin: String): Boolean {
        var pinCodeResult = false
        deviceManager.getPinCode()?.let {
            pinCodeResult = pin == it
        }
        return pinCodeResult
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            videoPlayerRepository.releaseVideoPlayer()
        }
    }
}