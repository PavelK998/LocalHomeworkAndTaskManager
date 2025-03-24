package ru.pkstudio.localhomeworkandtaskmanager.main.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class SubjectWithHomeworkData(
    @Embedded
    val subject: SubjectsEntity,
    @Relation(
        parentColumn = "subjectId",
        entityColumn = "subjectId"
    )
    val homework: List<HomeworkEntity>
)
