package ru.pkstudio.localhomeworkandtaskmanager.main.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stage")
data class StageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val stageName: String,
    val position: Int,
)
