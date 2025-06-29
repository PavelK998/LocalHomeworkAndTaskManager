package ru.pkstudio.localhomeworkandtaskmanager.core.navigation

import kotlinx.serialization.Serializable

sealed interface Destination {
    @Serializable
    data object MainGraph: Destination

    @Serializable
    data object AuthGraph: Destination

    @Serializable
    data object AuthScreen: Destination

    @Serializable
    data object MainScreen: Destination

    @Serializable
    data class DetailsHomeworkScreen(
        val homeworkId: String,
        val subjectId: String,
    ): Destination

    @Serializable
    data class HomeworkListScreen(
        val subjectId: String,
    ): Destination

    @Serializable
    data class HomeworkAddScreen(
        val subjectId: String
    ): Destination

    @Serializable
    data object StageEditScreen: Destination

    @Serializable
    data object SettingsScreen: Destination
}