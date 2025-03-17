package ru.pakarpichev.homeworktool.core.data.manager

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.pakarpichev.homeworktool.core.domain.manager.DeviceManager
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

    override fun setSelectedDisplayMethod(displayMethodId: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_DISPLAY_ID, displayMethodId)
        editor.apply()
    }

    override fun getSelectedDisplayMethod(): String? {
        return sharedPreferences.getString(KEY_DISPLAY_ID, "")
    }

    companion object {
        private const val KEY_PREFERENCES = "prefs"
        private const val KEY_USER_ID = "userId"
        private const val KEY_DISPLAY_ID = "displayId"
    }
}