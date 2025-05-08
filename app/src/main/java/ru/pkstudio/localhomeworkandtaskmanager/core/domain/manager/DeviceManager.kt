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

    fun getIsFirstLaunch(): Boolean

    fun setIsFirstLaunch(isFirstLaunch: Boolean)

    fun getUsage(): Int

    fun setUsage(usageId: Int)

    fun setLastAuthAction(authAction: Int)

    fun getLastAuthAction(): Int

    fun setFilterAddDate(filterAction: Int)

    fun getFilterAddDate(): Int

    fun setFilterImportance(filterAction: Int)

    fun getFilterImportance(): Int


}