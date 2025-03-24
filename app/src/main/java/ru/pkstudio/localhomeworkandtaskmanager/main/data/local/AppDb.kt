package ru.pkstudio.localhomeworkandtaskmanager.main.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.pkstudio.localhomeworkandtaskmanager.main.data.model.HomeworkEntity
import ru.pkstudio.localhomeworkandtaskmanager.main.data.model.StageEntity
import ru.pkstudio.localhomeworkandtaskmanager.main.data.model.SubjectsEntity

@Database(
    entities = [SubjectsEntity::class, HomeworkEntity::class, StageEntity::class],
    version = 1
)
abstract class AppDb: RoomDatabase() {
    abstract val subjectsDao: SubjectsDao
    abstract val homeworkDao: HomeworkDao
    abstract val stageDao: StageDao
}