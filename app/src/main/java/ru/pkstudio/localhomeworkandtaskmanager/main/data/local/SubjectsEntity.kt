package ru.pkstudio.localhomeworkandtaskmanager.main.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class SubjectsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val subjectName: String,
    val comment: String
)
