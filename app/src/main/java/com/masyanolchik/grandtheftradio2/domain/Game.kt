package com.masyanolchik.grandtheftradio2.domain

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.masyanolchik.grandtheftradio2.stationstree.StationsTreeItem
import com.masyanolchik.grandtheftradio2.stationstree.StationsTreeItem.Companion.GAME_PREFIX

data class Game(
    val id: Int,
    val gameName: String,
    val universe: String,
): StationsTreeItem {
    override fun toMediaItem(): MediaItem {
        val mediaMetadata =
            MediaMetadata.Builder()
                .setTitle(gameName)
                .setIsBrowsable(true)
                .setIsPlayable(false)
                .setMediaType(MediaMetadata.MEDIA_TYPE_ARTIST)
                .build()

        return MediaItem.Builder()
            .setMediaId(GAME_PREFIX+id)
            .setMediaMetadata(mediaMetadata)
            .build()
    }

}
