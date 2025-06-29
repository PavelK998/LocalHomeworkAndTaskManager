package ru.pkstudio.localhomeworkandtaskmanager.core.data.manager

import android.content.Context
import android.net.Uri
import androidx.annotation.RawRes
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import ru.pkstudio.localhomeworkandtaskmanager.core.domain.manager.VideoPlayerRepository
import javax.inject.Inject

class VideoPlayerRepositoryImpl @Inject constructor(
    private val player: Player,
    private val context: Context
) : VideoPlayerRepository {
    override val currentPlayer: Player
        get() = player

    override val checkIsPlaying: Boolean
        get() = player.isPlaying

    override suspend fun playVideo(@RawRes mediaRawResourceId: Int) {
        player.prepare()
        val videoFileUri = getRawFileUri(context = context, rawResourceId = mediaRawResourceId)
        val mediaItem = MediaItem.fromUri(videoFileUri)
        player.setMediaItem(mediaItem)
        player.playWhenReady = true
    }

    override fun releaseVideoPlayer() {
        player.release()
    }

    private fun getRawFileUri(context: Context, rawResourceId: Int): Uri {
        return "android.resource://${context.packageName}/raw/${
            context.resources.getResourceEntryName(
                rawResourceId
            )
        }".toUri()
    }
}