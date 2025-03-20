package ru.pkstudio.localhomeworkandtaskmanager.core.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.pkstudio.localhomeworkandtaskmanager.core.navigation.DefaultNavigator
import ru.pkstudio.localhomeworkandtaskmanager.core.navigation.Destination
import ru.pkstudio.localhomeworkandtaskmanager.core.navigation.Navigator
import ru.pkstudio.localhomeworkandtaskmanager.main.data.local.AppDb
import ru.pkstudio.localhomeworkandtaskmanager.main.data.local.SubjectsDao
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

}