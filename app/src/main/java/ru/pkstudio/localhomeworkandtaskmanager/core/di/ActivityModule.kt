package ru.pkstudio.localhomeworkandtaskmanager.core.di

import android.app.Activity
import androidx.activity.ComponentActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {

    @Provides
    @Singleton
    fun provideComponentActivity(activity: Activity): ComponentActivity {
        return activity as ComponentActivity
    }
}