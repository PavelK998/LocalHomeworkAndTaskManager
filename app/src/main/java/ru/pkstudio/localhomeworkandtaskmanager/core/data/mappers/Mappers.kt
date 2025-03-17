package ru.pakarpichev.homeworktool.core.data.mappers

import ru.pakarpichev.homeworktool.main.data.dto.HomeworkDto
import ru.pakarpichev.homeworktool.main.data.dto.SubjectDto
import ru.pakarpichev.homeworktool.auth.data.dto.UserDto
import ru.pakarpichev.homeworktool.main.domain.model.HomeworkRemoteModel
import ru.pakarpichev.homeworktool.main.domain.model.SubjectRemoteModel
import ru.pakarpichev.homeworktool.auth.domain.model.User
import ru.pakarpichev.homeworktool.main.domain.model.NewHomework
import ru.pakarpichev.homeworktool.main.presentation.uiModels.HomeworkUiModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun HomeworkDto.toHomeworkRemoteModel(): HomeworkRemoteModel {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    val addDate = LocalDate.parse(this.addDate, formatter)
    val startDate = if (!this.startDate.isNullOrBlank()) LocalDate.parse(this.startDate, formatter) else null
    val endDate =  if (!this.endDate.isNullOrBlank()) LocalDate.parse(this.endDate, formatter) else null

    return HomeworkRemoteModel(
        id = id,
        addDate = addDate,
        name = name,
        description = description,
        stage = stage,
        startDate = startDate,
        endDate = endDate,
        imageUrl = imageUrl
    )
}

fun HomeworkRemoteModel.toHomeworkDto(): HomeworkDto {
    return HomeworkDto(
        addDate = addDate.toString(),
        name = name,
        description = description,
        startDate = startDate?.toString() ?: "",
        endDate = endDate?.toString() ?: ""
    )
}

fun HomeworkRemoteModel.toHomeworkUiModel(): HomeworkUiModel {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    return HomeworkUiModel(
        id = id,
        addDate = formatter.format(addDate),
        name = name,
        description = description,
        stage = stage,
        startDate = if (startDate != null) formatter.format(startDate) else "",
        endDate = if (endDate != null) formatter.format(endDate) else "",
        imageUrl = imageUrl
    )
}

fun SubjectDto.toSubjectRemoteModel(): SubjectRemoteModel {
    return SubjectRemoteModel(
        id = id,
        name = name,
        comment = comment
    )
}

fun UserDto.toUser(): User {
    return User(
        name = name,
        lastName = lastName,
        educationalInstitution = educationalInstitution,
        year = year,
        imageUrl = imageUrl
    )
}