package ru.pkstudio.localhomeworkandtaskmanager.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.pkstudio.localhomeworkandtaskmanager.R
import ru.pkstudio.localhomeworkandtaskmanager.core.domain.manager.DeviceManager
import ru.pkstudio.localhomeworkandtaskmanager.core.domain.manager.ResourceManager
import ru.pkstudio.localhomeworkandtaskmanager.core.navigation.Destination
import ru.pkstudio.localhomeworkandtaskmanager.core.navigation.Navigator
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val deviceManager: DeviceManager,
    private val resourceManager: ResourceManager,
    private val navigator: Navigator
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    private var firstEnteredPin = ""

    init {
        checkPinCode()
    }

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

    private fun checkPinCode() {
        val pin = deviceManager.getPinCode()
        Log.d("xzczxczxc", "checkPinCode: $pin")
        if (pin.isNullOrBlank()) {
            _uiState.update {
                it.copy(
                    isCreatePin = true,
                    titleText = resourceManager.getString(R.string.create_password)
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    isCreatePin = false,
                    titleText = resourceManager.getString(R.string.enter_password)
                )
            }
        }
    }

    private fun setPinCode(pin: String) {
        deviceManager.setPinCode(pin)
    }

    private fun validatePinCode(pin: String): Boolean {
        var pinCodeResult = false
        deviceManager.getPinCode()?.let {
            pinCodeResult = pin == it
        }
        return pinCodeResult
    }
}