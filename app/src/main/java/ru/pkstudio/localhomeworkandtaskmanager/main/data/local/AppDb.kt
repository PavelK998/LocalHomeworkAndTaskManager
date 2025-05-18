package ru.pkstudio.localhomeworkandtaskmanager.main.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.pkstudio.localhomeworkandtaskmanager.main.data.model.HomeworkEntity
import ru.pkstudio.localhomeworkandtaskmanager.main.data.model.StageEntity
import ru.pkstudio.localhomeworkandtaskmanager.main.data.model.SubjectsEntity
import ru.pkstudio.localhomeworkandtaskmanager.main.data.model.UtilsEntity

@Database(
    entities = [SubjectsEntity::class, HomeworkEntity::class, StageEntity::class, UtilsEntity::class],
    version = 1
)
@TypeConverters(ru.pkstudio.localhomeworkandtaskmanager.main.data.converters.TypeConverters::class)
abstract class AppDb: RoomDatabase() {
    abstract val subjectsDao: SubjectsDao
    abstract val homeworkDao: HomeworkDao
    abstract val stageDao: StageDao
    abstract val utilsDao: UtilsDao
}