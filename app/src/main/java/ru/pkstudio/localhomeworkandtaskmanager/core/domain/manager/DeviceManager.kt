package ru.pkstudio.localhomeworkandtaskmanager.core.domain.manager

interface DeviceManager {

    suspend fun setSelectedDisplayMethod(displayMethodId: Int)

    suspend fun getSelectedDisplayMethod(): Int

    suspend fun setPinCode(pinCode: String)

    suspend fun getPinCode(): String

    suspend fun setTheme(themeId: Int)

    suspend fun setDynamicColors(isDynamicColor: Boolean)

    suspend fun getDynamicColors(): Boolean

    suspend fun getTheme(): Int

    fun startMicroVibrate()

    suspend fun getIsFirstLaunch(): Boolean

    suspend fun setIsFirstLaunch(isFirstLaunch: Boolean)

    suspend fun getUsage(): Int

    suspend fun setUsage(usageId: Int)

    suspend fun setLastAuthAction(authAction: Int)

    suspend fun getLastAuthAction(): Int

    suspend fun setFilterAddDate(filterAction: Int)

    suspend fun getFilterAddDate(): Int

    suspend fun setFilterImportance(filterAction: Int)

    suspend fun getFilterImportance(): Int

    suspend fun setFilePathUri(path: String)

    suspend fun getFilePathUri(): String?

}