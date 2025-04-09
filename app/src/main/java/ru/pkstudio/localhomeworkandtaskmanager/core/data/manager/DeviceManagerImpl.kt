package ru.pkstudio.localhomeworkandtaskmanager.core.data.manager

import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.pkstudio.localhomeworkandtaskmanager.core.domain.manager.DeviceManager
import javax.inject.Inject

class DeviceManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
): DeviceManager {

    private val sharedPreferences =
        context.getSharedPreferences(KEY_PREFERENCES, Context.MODE_PRIVATE)
    override fun setUserId(userId: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_USER_ID, userId)
        editor.apply()
    }

    override fun getUserId(): String? {
        return sharedPreferences.getString(KEY_USER_ID, "")
    }

    override fun setPinCode(pinCode: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_PIN_CODE, pinCode)
        editor.apply()
    }

    override fun getPinCode(): String? {
        return sharedPreferences.getString(KEY_PIN_CODE, "")
    }

    override fun setTheme(themeId: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(KEY_THEME, themeId)
        editor.apply()
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

    override fun setFilePathUri(path: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_FILE_PATH, path)
        editor.apply()
    }

    override fun getFilePathUri(): String? {
        return sharedPreferences.getString(KEY_FILE_PATH, "")
    }

    override fun setSelectedDisplayMethod(displayMethodId: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(KEY_DISPLAY_ID, displayMethodId)
        editor.apply()
    }

    override fun setDynamicColors(isDynamicColor: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY_DYNAMIC_COLOR, isDynamicColor)
        editor.apply()
    }

    override fun getDynamicColors(): Boolean {
        return sharedPreferences.getBoolean(KEY_DYNAMIC_COLOR, false)
    }

    override fun getSelectedDisplayMethod(): Int {
        return sharedPreferences.getInt(KEY_DISPLAY_ID, -1)
    }

    companion object {
        private const val KEY_PREFERENCES = "prefs"
        private const val KEY_USER_ID = "userId"
        private const val KEY_DISPLAY_ID = "displayId"
        private const val KEY_PIN_CODE = "pinCode"
        private const val KEY_THEME = "theme"
        private const val KEY_DYNAMIC_COLOR = "dynamic_color"
        private const val KEY_FILE_PATH = "file_path"
    }
}