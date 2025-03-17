package ru.pkstudio.localhomeworkandtaskmanager.core.di

import DefaultNavigator
import Navigator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.pakarpichev.homeworktool.core.navigation.Destination
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Provides
    @Singleton
    fun provideNavigation(): Navigator = DefaultNavigator(startDestination = Destination.AuthGraph)

}