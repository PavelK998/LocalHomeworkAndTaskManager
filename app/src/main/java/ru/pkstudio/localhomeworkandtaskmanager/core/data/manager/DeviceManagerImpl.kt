package ru.pkstudio.localhomeworkandtaskmanager.core.data.manager

import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.content.edit
import ru.pkstudio.localhomeworkandtaskmanager.core.domain.manager.DeviceManager
import javax.inject.Inject

class DeviceManagerImpl @Inject constructor(
    private val context: Context,
): DeviceManager {

    private val sharedPreferences =
        context.getSharedPreferences(KEY_PREFERENCES, Context.MODE_PRIVATE)
    override fun setUserId(userId: String) {
        sharedPreferences.edit() {
            putString(KEY_USER_ID, userId)
        }
    }

    override fun getUserId(): String? {
        return sharedPreferences.getString(KEY_USER_ID, "")
    }

    override fun setPinCode(pinCode: String) {
        sharedPreferences.edit() {
            putString(KEY_PIN_CODE, pinCode)
        }
    }

    override fun getPinCode(): String? {
        return sharedPreferences.getString(KEY_PIN_CODE, "")
    }

    override fun setTheme(themeId: Int) {
        sharedPreferences.edit() {
            putInt(KEY_THEME, themeId)
        }
    }

    override fun getTheme(): Int {
        return sharedPreferences.getInt(KEY_THEME, -1)
    }

    override fun startMicroVibrate() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(VIBRATOR_SERVICE) as Vibrator
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(500)
        }
    }

    override suspend fun setFilePathUri(path: String) {
        sharedPreferences.edit {
            putString(KEY_FILE_PATH, path)
        }
    }

    override suspend fun getFilePathUri(): String? {
        return sharedPreferences.getString(KEY_FILE_PATH, "")
    }

    override fun setSelectedDisplayMethod(displayMethodId: Int) {
        sharedPreferences.edit() {
            putInt(KEY_DISPLAY_ID, displayMethodId)
        }
    }

    override fun setDynamicColors(isDynamicColor: Boolean) {
        sharedPreferences.edit() {
            putBoolean(KEY_DYNAMIC_COLOR, isDynamicColor)
        }
    }

    override fun getDynamicColors(): Boolean {
        return sharedPreferences.getBoolean(KEY_DYNAMIC_COLOR, false)
    }

    override fun getSelectedDisplayMethod(): Int {
        return sharedPreferences.getInt(KEY_DISPLAY_ID, -1)
    }

    override fun getIsFirstLaunch(): Boolean {
        return sharedPreferences.getBoolean(KEY_FIRST_LAUNCH, true)
    }

    override fun setIsFirstLaunch(isFirstLaunch: Boolean) {
        sharedPreferences.edit() {
            putBoolean(KEY_FIRST_LAUNCH, isFirstLaunch)
        }
    }

    override fun getUsage(): Int {
        return sharedPreferences.getInt(KEY_USAGE, -1)
    }

    override fun setUsage(usageId: Int) {
        sharedPreferences.edit() {
            putInt(KEY_USAGE, usageId)
        }
    }

    override fun setLastAuthAction(authAction: Int) {
        sharedPreferences.edit() {
            putInt(KEY_LAST_AUTH_ACTION, authAction)
        }
    }

    override fun getLastAuthAction(): Int {
        return sharedPreferences.getInt(KEY_LAST_AUTH_ACTION, -1)
    }

    override fun setFilterAddDate(filterAction: Int) {
        sharedPreferences.edit() {
            putInt(KEY_FILTER_ADD_DATE, filterAction)
        }
    }

    override fun getFilterAddDate(): Int {
        return sharedPreferences.getInt(KEY_FILTER_ADD_DATE, -1)
    }

    override fun setFilterImportance(filterAction: Int) {
        sharedPreferences.edit() {
            putInt(KEY_FILTER_IMPORTANCE, filterAction)
        }
    }

    override fun getFilterImportance(): Int {
        return sharedPreferences.getInt(KEY_FILTER_IMPORTANCE, -1)
    }

    companion object {
        private const val KEY_PREFERENCES = "prefs"
        private const val KEY_USER_ID = "userId"
        private const val KEY_DISPLAY_ID = "displayId"
        private const val KEY_PIN_CODE = "pinCode"
        private const val KEY_THEME = "theme"
        private const val KEY_DYNAMIC_COLOR = "dynamic_color"
        private const val KEY_FILE_PATH = "file_path"
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_USAGE = "usage"
        private const val KEY_LAST_AUTH_ACTION = "last_auth_action"
        private const val KEY_FILTER_ADD_DATE = "filter_action_add_date"
        private const val KEY_FILTER_IMPORTANCE = "filter_action_importance"
    }
}