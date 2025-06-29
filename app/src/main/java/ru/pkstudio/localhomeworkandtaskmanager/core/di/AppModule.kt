package ru.pkstudio.localhomeworkandtaskmanager.core.di

import android.content.Context
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import ru.pkstudio.localhomeworkandtaskmanager.core.data.encrypt.Crypto
import ru.pkstudio.localhomeworkandtaskmanager.core.data.manager.DeviceManagerImpl
import ru.pkstudio.localhomeworkandtaskmanager.core.data.manager.VideoPlayerRepositoryImpl
import ru.pkstudio.localhomeworkandtaskmanager.core.domain.manager.DeviceManager
import ru.pkstudio.localhomeworkandtaskmanager.core.domain.manager.VideoPlayerRepository
import ru.pkstudio.localhomeworkandtaskmanager.core.navigation.DefaultNavigator
import ru.pkstudio.localhomeworkandtaskmanager.core.navigation.Destination
import ru.pkstudio.localhomeworkandtaskmanager.core.navigation.Navigator
import ru.pkstudio.localhomeworkandtaskmanager.main.data.local.realm.HomeworkObject
import ru.pkstudio.localhomeworkandtaskmanager.main.data.local.realm.StageObject
import ru.pkstudio.localhomeworkandtaskmanager.main.data.local.realm.SubjectObject
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
    fun provideRealmDb(@ApplicationContext context: Context): Realm {
        val encryptionKey = Crypto.getKeyForRealm(context)
        return Realm.open(
            configuration = RealmConfiguration.Builder(
                schema = setOf(
                    StageObject::class,
                    SubjectObject::class,
                    HomeworkObject::class
                )
            )
                .name("homework_database.realm")
                .encryptionKey(encryptionKey)
                .schemaVersion(1)
                .build(),
        )
    }


    @Provides
    @Singleton
    fun provideNavigation(): Navigator = DefaultNavigator(startDestination = Destination.AuthGraph)

    @Provides
    @Singleton
    fun provideImportExportDbRepository(
        @ApplicationContext context: Context,
    ): ImportExportDbRepository {
        return ImportExportDbRepositoryImpl(
            context = context,
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