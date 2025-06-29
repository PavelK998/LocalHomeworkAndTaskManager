package ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers

import io.realm.kotlin.ext.toRealmList
import ru.pkstudio.localhomeworkandtaskmanager.main.data.local.realm.HomeworkObject
import ru.pkstudio.localhomeworkandtaskmanager.main.data.local.realm.StageObject
import ru.pkstudio.localhomeworkandtaskmanager.main.data.local.realm.SubjectObject
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.HomeworkModel
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.StageModel
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.SubjectModel
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList.uiModel.HomeworkUiModel
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.subjectList.uiModel.SubjectUiModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// stages

fun StageModel.toStageObject(): StageObject = StageObject().apply {
    _id = if (this@toStageObject.id.isNotBlank()) org.mongodb.kbson.ObjectId(this@toStageObject.id)
    else org.mongodb.kbson.ObjectId()
    color = this@toStageObject.color
    stageName = this@toStageObject.stageName
    position = this@toStageObject.position
    isFinishStage = this@toStageObject.isFinishStage
}

fun StageObject.toStageModel(): StageModel = StageModel(
    id = _id.toHexString(),
    stageName = stageName,
    color = color,
    position = position,
    isFinishStage = isFinishStage
)

fun List<StageModel>.toListStageObject(): List<StageObject> = this.map {
    it.toStageObject()
}

fun List<StageObject>.toListStageModel(): List<StageModel> = this.map {
    it.toStageModel()
}

// subjects

fun SubjectModel.toSubjectObject(): SubjectObject = SubjectObject().apply {
    _id =
        if (this@toSubjectObject.id.isNotBlank()) org.mongodb.kbson.ObjectId(this@toSubjectObject.id)
        else org.mongodb.kbson.ObjectId()
    subjectName = this@toSubjectObject.subjectName
    comment = this@toSubjectObject.comment
    homeworkList = this@toSubjectObject.homework.toListHomeworkObject().toRealmList()
}

fun SubjectUiModel.toSubjectModel(): SubjectModel = SubjectModel(
    id = id,
    subjectName = subjectName,
    comment = comment
)

fun SubjectModel.toSubjectUiModel(): SubjectUiModel = SubjectUiModel(
    id = id,
    subjectName = subjectName,
    comment = comment,
    isRevealed = false,
    isEditModeEnabled = false
)

fun SubjectObject.toSubjectModel(): SubjectModel = SubjectModel(
    id = _id.toHexString(),
    subjectName = subjectName,
    comment = comment,
    homework = homeworkList.toListHomeworkModel()
)

 //homework

fun HomeworkModel.toHomeworkObject(): HomeworkObject = HomeworkObject().apply {
    val addDateFormatted = try {
        this@toHomeworkObject.addDate?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    } catch (e: Exception) {
        ""
    }

    val startDateFormatted = try {
        this@toHomeworkObject.startDate?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    } catch (e: Exception) {
        ""
    }

    val endDateFormatted = try {
        this@toHomeworkObject.endDate?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    } catch (e: Exception) {
        ""
    }
    _id =
        if (this@toHomeworkObject.id.isNotBlank()) org.mongodb.kbson.ObjectId(this@toHomeworkObject.id)
        else org.mongodb.kbson.ObjectId()
    color = this@toHomeworkObject.color
    importance = this@toHomeworkObject.importance
    addDate = addDateFormatted ?: ""
    name = this@toHomeworkObject.name
    stage = this@toHomeworkObject.stage
    stageId = this@toHomeworkObject.stageId
    description = this@toHomeworkObject.description
    startDate = startDateFormatted
    endDate = endDateFormatted
    isFinished = this@toHomeworkObject.isFinished
    imageNameList = this@toHomeworkObject.imageNameList.toRealmList()

}

fun HomeworkObject.toHomeworkModel(): HomeworkModel {
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
        LocalDateTime.parse(this.endDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    } catch (e: Exception) {
        null
    }
    return HomeworkModel(
        id = _id.toHexString(),
        color = color,
        importance = importance,
        addDate = addDate,
        startDate = startDate,
        endDate = endDate,
        name = name,
        stageId = stageId,
        stage = stage,
        description = description,
        isFinished = isFinished,
        imageNameList = imageNameList.toList()

    )
}

fun HomeworkModel.toHomeworkUiModel(): HomeworkUiModel {
    val addDate = try {
        this.addDate?.format(DateTimeFormatter.ofPattern("dd-MM-yyyy  HH:mm"))
    } catch (e: Exception) {
        ""
    }

    val endDate = try {
        this.endDate?.format(DateTimeFormatter.ofPattern("dd-MM-yyyy  HH:mm"))
    } catch (e: Exception) {
        ""
    }

    val startDate = try {
        this.startDate?.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
    } catch (e: Exception) {
        ""
    }
    return HomeworkUiModel(
        id = id,
        color = color,
        importance = importance,
        addDate = addDate ?: "",
        description = description,
        endDate = endDate ?: "",
        startDate = startDate ?: "",
        imageNameList = imageNameList,
        isCheckBoxVisible = false,
        isChecked = false,
        name = name,
        stageName = stage,
        stageId = stageId,
        isFinished = isFinished
    )
}

fun HomeworkUiModel.toHomeworkModel(): HomeworkModel {
    val dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy  HH:mm")
    val addDate = try {
        LocalDateTime.parse(this.addDate, dateTimeFormatter)
    } catch (e: Exception) {
        null
    }

    val endDate = try {
        LocalDateTime.parse(this.endDate, dateTimeFormatter)
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
        imageNameList = imageNameList,
        name = name,
        stageId = stageId,
        stage = stageName,
        isFinished = isFinished
    )
}

fun List<SubjectObject>.toListSubjectModel(): List<SubjectModel> = this.map {
    it.toSubjectModel()
}

fun List<HomeworkModel>.toListHomeworkObject(): List<HomeworkObject> = this.map {
    it.toHomeworkObject()
}

fun List<HomeworkObject>.toListHomeworkModel(): List<HomeworkModel> = this.map {
    it.toHomeworkModel()
}

fun List<HomeworkModel>.toListHomeworkUiModel(): List<HomeworkUiModel> = this.map {
    it.toHomeworkUiModel()
}

fun List<HomeworkUiModel>.toHomeworkModelList(): List<HomeworkModel> = this.map {
    it.toHomeworkModel()
}




