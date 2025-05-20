package ru.pkstudio.localhomeworkandtaskmanager.main.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "utils")
data class UtilsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val finalStageId: Long,
    val pathUri: String
)
