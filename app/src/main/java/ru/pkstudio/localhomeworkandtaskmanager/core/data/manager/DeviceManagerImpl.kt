package ru.pkstudio.localhomeworkandtaskmanager.core.data.manager

import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.datastore.dataStore
import kotlinx.coroutines.flow.first
import ru.pkstudio.localhomeworkandtaskmanager.core.data.encrypt.UserSettingsSerializer
import ru.pkstudio.localhomeworkandtaskmanager.core.domain.manager.DeviceManager
import javax.inject.Inject

class DeviceManagerImpl @Inject constructor(
    private val context: Context,
) : DeviceManager {
    private val Context.dataStore by dataStore(
        fileName = "user-settings",
        serializer = UserSettingsSerializer
    )

    override suspend fun setSelectedDisplayMethod(displayMethodId: Int) {
        context.dataStore.updateData {
            it.copy(
                displayId = displayMethodId
            )
        }
    }

    override suspend fun getSelectedDisplayMethod(): Int {
        return context.dataStore.data.first().displayId
    }

    override suspend fun setPinCode(pinCode: String) {
        context.dataStore.updateData {
            it.copy(
                pinCode = pinCode
            )
        }
    }

    override suspend fun getPinCode(): String {
        return context.dataStore.data.first().pinCode
    }

    override suspend fun setTheme(themeId: Int) {
        context.dataStore.updateData {
            it.copy(
                themeId = themeId
            )
        }
    }

    override suspend fun setDynamicColors(isDynamicColor: Boolean) {
        context.dataStore.updateData {
            it.copy(
                dynamicColor = isDynamicColor
            )
        }
    }

    override suspend fun getDynamicColors(): Boolean {
        return context.dataStore.data.first().dynamicColor
    }

    override suspend fun getTheme(): Int {
        return context.dataStore.data.first().themeId
    }

    override suspend fun getIsFirstLaunch(): Boolean {
        return context.dataStore.data.first().isFirstLaunch
    }

    override suspend fun setIsFirstLaunch(isFirstLaunch: Boolean) {
        context.dataStore.updateData {
            it.copy(
                isFirstLaunch = isFirstLaunch
            )
        }
    }

    override suspend fun getUsage(): Int {
        return context.dataStore.data.first().usageId
    }

    override suspend fun setUsage(usageId: Int) {
        context.dataStore.updateData {
            it.copy(
                usageId = usageId
            )
        }
    }

    override suspend fun setLastAuthAction(authAction: Int) {
        context.dataStore.updateData {
            it.copy(
                lastAuthAction = authAction
            )
        }
    }

    override suspend fun getLastAuthAction(): Int {
        return context.dataStore.data.first().lastAuthAction
    }

    override suspend fun setFilterAddDate(filterAction: Int) {
        context.dataStore.updateData {
            it.copy(
                filterAddDate = filterAction
            )
        }
    }

    override suspend fun getFilterAddDate(): Int {
        return context.dataStore.data.first().filterAddDate
    }

    override suspend fun setFilterImportance(filterAction: Int) {
        context.dataStore.updateData {
            it.copy(
                filterImportance = filterAction
            )
        }
    }

    override suspend fun getFilterImportance(): Int {
        return context.dataStore.data.first().filterImportance
    }

    override fun startMicroVibrate() {
        val vibratorDuration = 580L
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(VIBRATOR_SERVICE) as Vibrator
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect =
                VibrationEffect.createOneShot(vibratorDuration, VibrationEffect.DEFAULT_AMPLITUDE)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(vibratorDuration)
        }
    }

    override suspend fun setFilePathUri(path: String) {
        context.dataStore.updateData {
            it.copy(
                filePath = path
            )
        }
    }

    override suspend fun getFilePathUri(): String {
        return context.dataStore.data.first().filePath
    }
}