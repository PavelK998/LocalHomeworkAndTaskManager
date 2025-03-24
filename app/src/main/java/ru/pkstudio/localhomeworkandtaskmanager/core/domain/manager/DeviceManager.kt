package ru.pkstudio.localhomeworkandtaskmanager.core.domain.manager

interface DeviceManager {
    fun setUserId(userId: String)

    fun getUserId(): String?

    fun setSelectedDisplayMethod(displayMethodId: Int)

    fun getSelectedDisplayMethod(): Int

    fun setPinCode(pinCode: String)

    fun getPinCode(): String?

    fun startMicroVibrate()
}