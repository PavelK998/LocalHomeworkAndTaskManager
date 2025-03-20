package ru.pkstudio.localhomeworkandtaskmanager.main.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SubjectsEntity::class], version = 1)
abstract class AppDb: RoomDatabase() {
    abstract val subjectsDao: SubjectsDao
}