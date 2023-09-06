package com.masyanolchik.grandtheftradio2.domain

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.github.kotvertolet.youtubejextractor.YoutubeJExtractor
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

    companion object {
        fun getSongCorrectLink(song: Song): String {
            return if (song.link.contains("http")) {
                song.link
            } else {
                val youtubeJExtractor = YoutubeJExtractor()
                val videoData = youtubeJExtractor.extract(song.link)
                videoData.streamingData?.muxedStreams?.first()?.url ?: ""
            }
        }
    }
}
