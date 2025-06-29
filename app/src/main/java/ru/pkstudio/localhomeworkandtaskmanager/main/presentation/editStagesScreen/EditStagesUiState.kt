package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.editStagesScreen

import androidx.compose.ui.graphics.Color
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.StageModel
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.stageVariant1
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.stageVariant10
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.stageVariant11
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.stageVariant12
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.stageVariant13
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.stageVariant14
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.stageVariant15
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.stageVariant16
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.stageVariant17
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.stageVariant18
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.stageVariant19
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.stageVariant2
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.stageVariant20
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.stageVariant3
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.stageVariant4
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.stageVariant5
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.stageVariant6
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.stageVariant7
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.stageVariant8
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.stageVariant9

data class EditStagesState(
    val paramOne: String = "default",
    val stagesList: List<StageModel> = emptyList(),
    val isDeleteAlertDialogOpened: Boolean = false,
    val isColorAlertDialogOpened: Boolean = false,
    val titleDeleteAlertDialog: String = "",
    val commentDeleteAlertDialog: String = "",
    val colorList: List<Color> = listOf(
        stageVariant1,
        stageVariant2,
        stageVariant3,
        stageVariant4,
        stageVariant5,
        stageVariant6,
        stageVariant7,
        stageVariant8,
        stageVariant9,
        stageVariant10,
        stageVariant11,
        stageVariant12,
        stageVariant13,
        stageVariant14,
        stageVariant15,
        stageVariant16,
        stageVariant17,
        stageVariant18,
        stageVariant19,
        stageVariant20
    ),


)