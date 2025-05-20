package ru.pkstudio.localhomeworkandtaskmanager.core.di

import android.content.Context
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.pkstudio.localhomeworkandtaskmanager.core.data.manager.DeviceManagerImpl
import ru.pkstudio.localhomeworkandtaskmanager.core.data.manager.VideoPlayerRepositoryImpl
import ru.pkstudio.localhomeworkandtaskmanager.core.domain.manager.DeviceManager
import ru.pkstudio.localhomeworkandtaskmanager.core.domain.manager.VideoPlayerRepository
import ru.pkstudio.localhomeworkandtaskmanager.core.navigation.DefaultNavigator
import ru.pkstudio.localhomeworkandtaskmanager.core.navigation.Destination
import ru.pkstudio.localhomeworkandtaskmanager.core.navigation.Navigator
import ru.pkstudio.localhomeworkandtaskmanager.main.data.local.AppDb
import ru.pkstudio.localhomeworkandtaskmanager.main.data.local.HomeworkDao
import ru.pkstudio.localhomeworkandtaskmanager.main.data.local.StageDao
import ru.pkstudio.localhomeworkandtaskmanager.main.data.local.SubjectsDao
import ru.pkstudio.localhomeworkandtaskmanager.main.data.local.UtilsDao
import ru.pkstudio.localhomeworkandtaskmanager.main.data.repository.FilesHandleRepositoryImpl
import ru.pkstudio.localhomeworkandtaskmanager.main.data.repository.ImportExportDbRepositoryImpl
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository.FilesHandleRepository
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository.ImportExportDbRepository
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Provides
    @Singleton
    fun provideNavigation(): Navigator = DefaultNavigator(startDestination = Destination.AuthGraph)

    @Provides
    @Singleton
    fun provideAppDb(@ApplicationContext context: Context): AppDb {
        return Room.databaseBuilder(
            context = context,
            AppDb::class.java,
            name = "homework.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideSubjectsDao(database: AppDb): SubjectsDao {
        return database.subjectsDao
    }

    @Provides
    @Singleton
    fun provideHomeworkDao(database: AppDb): HomeworkDao {
        return database.homeworkDao
    }

    @Provides
    @Singleton
    fun provideStageDao(database: AppDb): StageDao {
        return database.stageDao
    }

    @Provides
    @Singleton
    fun provideUtilsDao(database: AppDb): UtilsDao {
        return database.utilsDao
    }

    @Provides
    @Singleton
    fun provideImportExportDbRepository(
        @ApplicationContext context: Context,
        appDb: AppDb
    ): ImportExportDbRepository {
        return ImportExportDbRepositoryImpl(
            context = context,
            appDb = appDb,
        )
    }

    @Provides
    @Singleton
    fun provideVideoPlayer(
        @ApplicationContext context: Context,
    ): Player {
        return ExoPlayer.Builder(context)
            .build()
    }

    @Provides
    @Singleton
    fun provideVideoRepository(
        @ApplicationContext context: Context,
        player: Player
    ): VideoPlayerRepository {
        return VideoPlayerRepositoryImpl(
            context = context,
            player = player,
        )
    }

    @Provides
    @Singleton
    fun provideFilesRepository(
        @ApplicationContext context: Context
    ): FilesHandleRepository {
        return FilesHandleRepositoryImpl(
            context = context
        )
    }

    @Provides
    @Singleton
     fun provideDeviceManager(
        @ApplicationContext context: Context,
    ): DeviceManager {
         return DeviceManagerImpl(
             context = context
         )
     }

}