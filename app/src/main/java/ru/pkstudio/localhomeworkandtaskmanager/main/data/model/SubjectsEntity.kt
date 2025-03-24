package ru.pkstudio.localhomeworkandtaskmanager.main.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class SubjectsEntity(
    @PrimaryKey(autoGenerate = true)
    val subjectId: Long? = null,
    val subjectName: String,
    val comment: String
)
