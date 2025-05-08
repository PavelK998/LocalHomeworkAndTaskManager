package ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers

import androidx.compose.ui.graphics.Color
import ru.pkstudio.localhomeworkandtaskmanager.main.data.model.HomeworkEntity
import ru.pkstudio.localhomeworkandtaskmanager.main.data.model.StageEntity
import ru.pkstudio.localhomeworkandtaskmanager.main.data.model.SubjectWithHomeworkData
import ru.pkstudio.localhomeworkandtaskmanager.main.data.model.SubjectsEntity
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.HomeworkModel
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.StageModel
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.SubjectModel
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.SubjectWithHomework
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList.uiModel.HomeworkUiModel
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.subjectList.uiModel.SubjectUiModel
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
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.inversePrimaryDarkMediumContrast
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.inversePrimaryLightMediumContrast
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.onDarkCardText
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.onLightCardText
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.primaryContainerDarkMediumContrast
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.primaryContainerLightMediumContrast
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun SubjectsEntity.toSubjectModel(): SubjectModel {
    return SubjectModel(
        id = subjectId ?: 0,
        subjectName = subjectName,
        comment = comment
    )
}

fun SubjectModel.toSubjectEntity(): SubjectsEntity {
    return SubjectsEntity(
        subjectId = id,
        subjectName = subjectName,
        comment = comment
    )
}

fun SubjectModel.toSubjectUiModel(): SubjectUiModel {
    return SubjectUiModel(
        id = id ?: 0,
        subjectName = subjectName,
        comment = comment,
        isRevealed = false,
        isEditModeEnabled = false
    )
}

fun SubjectUiModel.toSubjectModel(): SubjectModel {
    return SubjectModel(
        id = id,
        subjectName = subjectName,
        comment = comment,
    )
}

fun List<SubjectsEntity>.toListSubjectModel(): List<SubjectModel> {
    return map { subject ->
        SubjectModel(
            id = subject.subjectId ?: 0,
            subjectName = subject.subjectName,
            comment = subject.comment
        )
    }
}

fun HomeworkEntity.toHomeworkModel(): HomeworkModel {
    val addDate = try {
        LocalDateTime.parse(this.addDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    } catch (e: Exception) {
        null
    }

    val startDate = try {
        LocalDate.parse(this.startDate, DateTimeFormatter.ISO_LOCAL_DATE)
    } catch (e: Exception) {
        null
    }

    val endDate = try {
        LocalDate.parse(this.endDate, DateTimeFormatter.ISO_LOCAL_DATE)
    } catch (e: Exception) {
        null
    }
    return HomeworkModel(
        id = id,
        color = color,
        importance = importance,
        subjectId = subjectId,
        addDate = addDate,
        name = name,
        stage = stage,
        description = description,
        startDate = startDate,
        endDate = endDate,
        imageUrl = imageUrl,
        stageId = stageId
    )
}

fun HomeworkModel.toHomeworkEntity(): HomeworkEntity {

    val addDate = try {
        this.addDate?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    } catch (e: Exception) {
        ""
    }

    val startDate = try {
        this.startDate?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    } catch (e: Exception) {
        ""
    }

    val endDate = try {
        this.endDate?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    } catch (e: Exception) {
        ""
    }
    return HomeworkEntity(
        id = id,
        color = color,
        importance = importance,
        subjectId = subjectId,
        addDate = addDate ?: "",
        name = name,
        stage = stage,
        description = description,
        startDate = startDate,
        endDate = endDate,
        imageUrl = imageUrl,
        stageId = stageId
    )
}

fun List<HomeworkModel>.toListHomeworkEntity(): List<HomeworkEntity> {
    return this.map {
        it.toHomeworkEntity()
    }
}

fun List<HomeworkUiModel>.toListHomeworkModels(): List<HomeworkModel> {
    return this.map {
        it.toHomeworkModel()
    }
}

fun SubjectWithHomeworkData.toSubjectWithHomework(): SubjectWithHomework {
    return SubjectWithHomework(
        subject = subject.toSubjectModel(),
        homework = homework.toListHomeworkModel()
    )
}

fun List<HomeworkEntity>.toListHomeworkModel(): List<HomeworkModel> {
    return map {
        it.toHomeworkModel()
    }
}

fun List<SubjectWithHomeworkData>.toListSubjectWithHomework(): List<SubjectWithHomework> {
    return map {
        SubjectWithHomework(
            subject = it.subject.toSubjectModel(),
            homework = it.homework.toListHomeworkModel()
        )
    }
}

fun HomeworkModel.toHomeworkUiModel(): HomeworkUiModel {
    val addDate = try {
        this.addDate?.format(DateTimeFormatter.ofPattern("dd-MM-yyyy   HH:mm"))
    } catch (e: Exception) {
        ""
    }

    val endDate = try {
        this.endDate?.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
    } catch (e: Exception) {
        ""
    }

    val startDate = try {
        this.startDate?.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
    } catch (e: Exception) {
        ""
    }
    return HomeworkUiModel(
        id = id ?: 0L,
        color = color,
        importance = importance,
        addDate = addDate ?: "",
        description = description,
        endDate = endDate ?: "",
        startDate = startDate ?: "",
        imageUrl = imageUrl,
        isCheckBoxVisible = false,
        isChecked = false,
        name = name,
        stageName = stage,
        stageId = stageId,
        subjectId = subjectId
    )
}

fun HomeworkUiModel.toHomeworkModel(): HomeworkModel {
    val dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy   HH:mm")
    val addDate = try {
        LocalDateTime.parse(this.addDate, dateTimeFormatter)
    } catch (e: Exception) {
        null
    }

    val endDate = try {
        LocalDate.parse(this.endDate, dateTimeFormatter)
    } catch (e: Exception) {
        null
    }

    val startDate = try {
        LocalDate.parse(this.startDate, dateTimeFormatter)
    } catch (e: Exception) {
        null
    }
    return HomeworkModel(
        id = id,
        color = color,
        importance = importance,
        addDate = addDate,
        description = description,
        endDate = endDate,
        startDate = startDate,
        imageUrl = imageUrl,
        name = name,
        stageId = stageId,
        stage = stageName,
        subjectId = subjectId
    )
}


fun List<HomeworkModel>.toHomeworkUiModelList(): List<HomeworkUiModel> {
    return map {
        it.toHomeworkUiModel()
    }
}

fun StageEntity.toStageModel(): StageModel {
    return StageModel(
        id = id,
        color = color,
        stageName = stageName,
        position = position
    )
}

fun StageModel.toStageEntity(): StageEntity {
    return StageEntity(
        id = id,
        color = color,
        stageName = stageName,
        position = position
    )
}

fun List<StageEntity>.toStageModelList(): List<StageModel> {
    return map {
        StageModel(
            id = it.id,
            color = it.color,
            stageName = it.stageName,
            position = it.position
        )
    }
}

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
        importance10, importance9, importance8, importance7 -> onDarkCardText
        else -> onLightCardText
    }
}

fun Color.toStageNameInCardColor(isSystemInDarkMode: Boolean): Color {
    return if (isSystemInDarkMode) {
        when (this) {
            importance10, importance9, importance8, importance7 -> inversePrimaryDarkMediumContrast
            else -> primaryContainerLightMediumContrast
        }
    } else {
        when (this) {
            importance10, importance9, importance8, importance7 -> inversePrimaryLightMediumContrast
            else -> primaryContainerDarkMediumContrast
        }
    }

}

fun Color.toStageNameTextColor(): Color {
    return when (this) {
        importance10, importance9, importance8, importance7 -> onDarkCardText
        else -> onLightCardText
    }
}

fun Color.toStageBackground(): Color {
    return when (this) {
        importance10, importance9, importance8, importance7 -> onDarkCardText
        else -> onLightCardText
    }
}


