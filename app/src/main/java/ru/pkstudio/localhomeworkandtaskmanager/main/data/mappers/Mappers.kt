package ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers

import androidx.compose.ui.graphics.Color
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.forImportance1_5
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.forImportance6_10
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.importance1
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.importance10
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.importance2
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.importance3
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.importance4
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.importance5
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.importance6
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.importance7
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.importance8
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.importance9
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.onDarkCardText
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.onLightCardText

//utils

fun Color.toImportance(): Int {
    return when (this) {
        importance1 -> 1
        importance2 -> 2
        importance3 -> 3
        importance4 -> 4
        importance5 -> 5
        importance6 -> 6
        importance7 -> 7
        importance8 -> 8
        importance9 -> 9
        importance10 -> 10
        else -> {
            0
        }
    }
}

fun Color.toTextColor(): Color {
    return when (this) {
        importance1, importance2, importance3, importance4, importance5 -> onLightCardText
        else -> onDarkCardText
    }
}

fun Color.toStageNameInCardColor(isSystemInDarkMode: Boolean): Color {
    return if (isSystemInDarkMode) {
        when (this) {
            importance1, importance2, importance3, importance4, importance5 -> forImportance1_5
            else -> forImportance6_10
        }
    } else {
        when (this) {
            importance1, importance2, importance3, importance4, importance5 -> forImportance1_5
            else -> forImportance6_10
        }
    }
}

