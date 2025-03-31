package ru.pkstudio.localhomeworkandtaskmanager.main.data.repository

import ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository.Repository
import javax.inject.Inject

class RepositoryImpl @Inject constructor(

): Repository {
    override var qwe: Boolean = false

    override fun toggleDarkTheme() {
        qwe = true
    }

    override fun toggleLightTheme() {
       qwe = false
    }
}