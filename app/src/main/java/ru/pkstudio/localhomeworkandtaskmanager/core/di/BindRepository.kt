package ru.pkstudio.localhomeworkandtaskmanager.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.pkstudio.localhomeworkandtaskmanager.core.data.manager.ResourceManagerImpl
import ru.pkstudio.localhomeworkandtaskmanager.core.domain.manager.ResourceManager
import ru.pkstudio.localhomeworkandtaskmanager.main.data.repository.HomeworkRepositoryImpl
import ru.pkstudio.localhomeworkandtaskmanager.main.data.repository.StageRepositoryImpl
import ru.pkstudio.localhomeworkandtaskmanager.main.data.repository.SubjectsRepositoryImpl
import ru.pkstudio.localhomeworkandtaskmanager.main.data.repository.UtilsRepositoryImpl
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository.HomeworkRepository
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository.StageRepository
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository.SubjectsRepository
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository.UtilsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BindRepository {

    @Binds
    @Singleton
    abstract fun bindResourceManager(
        resourceManagerImpl: ResourceManagerImpl
    ): ResourceManager

    @Binds
    @Singleton
    abstract fun bindSubjectsRepository(
        subjectsRepositoryImpl: SubjectsRepositoryImpl
    ): SubjectsRepository

    @Binds
    @Singleton
    abstract fun bindHomeworkRepository(
        homeworkRepositoryImpl: HomeworkRepositoryImpl
    ): HomeworkRepository

    @Binds
    @Singleton
    abstract fun bindStageRepository(
        stageRepositoryImpl: StageRepositoryImpl
    ): StageRepository

    @Binds
    @Singleton
    abstract fun bindUtilsRepository(
        utilsRepositoryImpl: UtilsRepositoryImpl
    ): UtilsRepository
}