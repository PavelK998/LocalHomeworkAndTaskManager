package ru.pakarpichev.homeworktool.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.pakarpichev.homeworktool.core.data.manager.DeviceManagerImpl
import ru.pakarpichev.homeworktool.main.data.repository.HomeworkRepositoryImpl
import ru.pakarpichev.homeworktool.main.data.repository.SubjectsRepositoryImpl
import ru.pakarpichev.homeworktool.core.domain.manager.DeviceManager
import ru.pakarpichev.homeworktool.main.domain.repository.HomeworkRepository
import ru.pakarpichev.homeworktool.main.domain.repository.SubjectsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BindRepository {

    @Binds
    @Singleton
    abstract fun bindSubjectRepository(
        subjectsRepositoryImpl: SubjectsRepositoryImpl
    ): SubjectsRepository

    @Binds
    @Singleton
    abstract fun bindHomeworkRepository(
        homeworkRepositoryImpl: HomeworkRepositoryImpl
    ): HomeworkRepository

    @Binds
    @Singleton
    abstract fun bindDeviceManager(
        deviceManagerImpl: DeviceManagerImpl
    ): DeviceManager
}