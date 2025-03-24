package ru.pkstudio.localhomeworkandtaskmanager.main.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "homework",
    foreignKeys = [
        ForeignKey(
            entity = SubjectsEntity::class,
            parentColumns = ["subjectId"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        ),
    ],
    indices = [Index("subjectId")]
)
data class HomeworkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val subjectId: Long,
    val addDate: String,
    val name: String,
    val stage: String,
    val stageId: Long,
    val description: String,
    val startDate: String?,
    val endDate: String?,
    val imageUrl: String
)
