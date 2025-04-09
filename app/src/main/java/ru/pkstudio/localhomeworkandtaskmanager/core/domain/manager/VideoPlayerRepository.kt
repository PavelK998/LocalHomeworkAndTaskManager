package ru.pkstudio.localhomeworkandtaskmanager.core.domain.manager

import androidx.annotation.RawRes
import androidx.media3.common.Player

interface VideoPlayerRepository {

    val currentPlayer: Player

    val checkIsPlaying: Boolean

    suspend fun playVideo(@RawRes mediaRawResourceId: Int)

    fun releaseVideoPlayer()
}