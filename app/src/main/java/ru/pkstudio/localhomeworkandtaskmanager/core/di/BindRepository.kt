package ru.pakarpichev.homeworktool.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.pkstudio.localhomeworkandtaskmanager.core.data.manager.DeviceManagerImpl
import ru.pkstudio.localhomeworkandtaskmanager.core.data.manager.ResourceManagerImpl

import ru.pkstudio.localhomeworkandtaskmanager.core.domain.manager.DeviceManager
import ru.pkstudio.localhomeworkandtaskmanager.core.domain.manager.ResourceManager

import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BindRepository {

//    @Binds
//    @Singleton
//    abstract fun bindSubjectRepository(
//        subjectsRepositoryImpl: SubjectsRepositoryImpl
//    ): SubjectsRepository
//
//    @Binds
//    @Singleton
//    abstract fun bindHomeworkRepository(
//        homeworkRepositoryImpl: HomeworkRepositoryImpl
//    ): HomeworkRepository

    @Binds
    @Singleton
    abstract fun bindDeviceManager(
        deviceManagerImpl: DeviceManagerImpl
    ): DeviceManager

    @Binds
    @Singleton
    abstract fun bindResourceManager(
        resourceManagerImpl: ResourceManagerImpl
    ): ResourceManager
}