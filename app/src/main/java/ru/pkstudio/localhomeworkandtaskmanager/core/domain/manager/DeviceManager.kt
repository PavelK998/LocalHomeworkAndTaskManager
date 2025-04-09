package ru.pkstudio.localhomeworkandtaskmanager.core.domain.manager

interface DeviceManager {
    fun setUserId(userId: String)

    fun getUserId(): String?

    fun setSelectedDisplayMethod(displayMethodId: Int)

    fun getSelectedDisplayMethod(): Int

    fun setPinCode(pinCode: String)

    fun getPinCode(): String?

    fun setTheme(themeId: Int)

    fun setDynamicColors(isDynamicColor: Boolean)

    fun getDynamicColors(): Boolean

    fun getTheme(): Int

    fun startMicroVibrate()

    fun setFilePathUri(path: String)

    fun getFilePathUri(): String?
}