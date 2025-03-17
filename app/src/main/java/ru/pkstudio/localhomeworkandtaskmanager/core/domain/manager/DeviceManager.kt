package ru.pakarpichev.homeworktool.core.domain.manager

interface DeviceManager {
    fun setUserId(userId: String)

    fun getUserId(): String?

    fun setSelectedDisplayMethod(displayMethodId: String)

    fun getSelectedDisplayMethod(): String?
}