package ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers

import ru.pkstudio.localhomeworkandtaskmanager.main.data.local.SubjectsEntity
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.SubjectModel
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.subjectList.uiModel.SubjectUiModel

fun SubjectsEntity.toSubjectModel(): SubjectModel {
    return SubjectModel(
        id = id?: 0,
        subjectName = subjectName,
        comment = comment
    )
}

fun SubjectModel.toSubjectEntity(): SubjectsEntity {
    return SubjectsEntity(
        id = id,
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
            id = subject.id ?: 0,
            subjectName = subject.subjectName,
            comment = subject.comment
        )
    }
}


