package com.masyanolchik.grandtheftradio2.domain

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.masyanolchik.grandtheftradio2.stationstree.StationsTreeItem
import com.masyanolchik.grandtheftradio2.stationstree.StationsTreeItem.Companion.SONG_PREFIX

data class Song(
    val id: Int,
    val artist: String,
    val title: String,
    val msOffset: Long,
    val link: String,
    val radioName: String,
    val picLink: String,
    val msTotalLength: Long,
) : StationsTreeItem {
    override fun toMediaItem(): MediaItem {
        val mediaMetadata =
            MediaMetadata.Builder()
                .setIsBrowsable(false)
                .setIsPlayable(true)
                .setMediaType(MediaMetadata.MEDIA_TYPE_MUSIC)
                .setTitle(title)
                .setArtist(artist)
                .setSubtitle(artist)
                .setStation(radioName)
                .setArtworkUri(Uri.parse(picLink))
                .build()

        return MediaItem.Builder()
            .setMediaId(SONG_PREFIX + id)
            .setMediaMetadata(mediaMetadata)
            .setUri(link)
            .build()
    }

}
