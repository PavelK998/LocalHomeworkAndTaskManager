package ru.pakarpichev.homeworktool.core.domain.util

sealed class ResourceOld<T>(val data:T? = null, val message: String? = null ) {
    class Success<T>(data: T?): ResourceOld<T>(data)
    class Error<T>(message: String?): ResourceOld<T>(data = null, message)
    class IsLoading<T>(val isLoading: Boolean = true): ResourceOld<T>()

}