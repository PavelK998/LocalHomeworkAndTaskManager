package ru.pkstudio.localhomeworkandtaskmanager.auth

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ru.pkstudio.localhomeworkandtaskmanager.R
import ru.pkstudio.localhomeworkandtaskmanager.core.domain.manager.DeviceManager
import ru.pkstudio.localhomeworkandtaskmanager.core.domain.manager.ResourceManager
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val deviceManager: DeviceManager,
    private val resourceManager: ResourceManager,
): ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    private var firstEnteredPin = ""

    init {
        checkPinCode()
    }

    fun handleIntent(intent: AuthIntent) {
        when(intent) {
            is AuthIntent.OnKeyboardClicked -> {
                when(intent.text) {
                    "-" -> {
                        if (_uiState.value.text.isNotBlank()){
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
                                    if (firstEnteredPin.isBlank()){
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
                                            _uiState.update {
                                                it.copy(
                                                    isSuccess = true
                                                )
                                            }
                                            setPinCode(newString)
                                        } else {
                                            _uiState.update {
                                                it.copy(
                                                    isError = true
                                                )
                                            }
                                            deviceManager.startMicroVibrate()
                                        }
                                    }
                                } else {
                                    if (validatePinCode(newString)) {
                                        _uiState.update {
                                            it.copy(
                                                isSuccess = true
                                            )
                                        }
                                    } else {
                                        _uiState.update {
                                            it.copy(
                                                isError = true
                                            )
                                        }
                                        deviceManager.startMicroVibrate()
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
    }
    private fun checkPinCode() {
        val pin = deviceManager.getPinCode()
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