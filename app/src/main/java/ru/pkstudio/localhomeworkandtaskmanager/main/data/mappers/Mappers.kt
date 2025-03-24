package ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers

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
        id = id?: 0L,
        addDate = addDate?: "",
        description = description,
        endDate = endDate?: "",
        startDate = startDate?: "",
        imageUrl = imageUrl,
        isCheckBoxVisible = false,
        isChecked = false,
        name = name,
        stageName = stage,
        stageId = stageId
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
        stageName = stageName
    )
}

fun StageModel.toStageEntity(): StageEntity {
    return StageEntity(
        id = id,
        stageName = stageName
    )
}

fun List<StageEntity>.toStageModelList(): List<StageModel> {
    return map {
        StageModel(
            id = it.id,
            stageName = it.stageName
        )
    }
}


