package ru.pkstudio.localhomeworkandtaskmanager.core.di

import ru.pkstudio.localhomeworkandtaskmanager.core.navigation.DefaultNavigator
import ru.pkstudio.localhomeworkandtaskmanager.core.navigation.Navigator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.pkstudio.localhomeworkandtaskmanager.core.navigation.Destination
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Provides
    @Singleton
    fun provideNavigation(): Navigator = DefaultNavigator(startDestination = Destination.AuthGraph)

}