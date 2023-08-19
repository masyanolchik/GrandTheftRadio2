package com.masyanolchik.grandtheftradio2.domain

import android.net.Uri
import android.os.SystemClock
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.masyanolchik.grandtheftradio2.stationstree.StationsTreeItem
import com.masyanolchik.grandtheftradio2.stationstree.StationsTreeItem.Companion.SONG_PREFIX
import com.masyanolchik.grandtheftradio2.stationstree.StationsTreeItem.Companion.STATION_PREFIX
import java.util.Calendar
import kotlin.math.ceil

data class Station (
    val id: Int,
    val game: Game,
    val name: String,
    val genre: String,
    val picLink: String,
    val songs: List<Song>,
): StationsTreeItem {
    override fun toMediaItem(): MediaItem {
        val albumFolderIdInTree = STATION_PREFIX + id
        val mediaMetadata =
            MediaMetadata.Builder()
                .setMediaType(MediaMetadata.MEDIA_TYPE_ALBUM)
                .setTitle(name)
                .setArtworkUri(Uri.parse(picLink))
                .setIsPlayable(true)
                .setIsBrowsable(true)
                .build()

        return MediaItem.Builder()
            .setMediaId(albumFolderIdInTree)
            .setMediaMetadata(mediaMetadata)
            .build()
    }

    fun getCurrentSongWithSeekPosition(): Pair<Song, Long> {
        val fullDurationMs = songs.first().msTotalLength
        val currentTime = SystemClock.currentThreadTimeMillis()
        val timePassed = currentTime - START_TIME
        val repeatCount = (timePassed.toDouble() / fullDurationMs)
        val currentOffset = ceil(repeatCount % 1 * fullDurationMs).toLong()
        val songsCopy = songs.toMutableList()
        val currentSong = songsCopy.sortedBy { it.msOffset }.last { it.msOffset <= currentOffset }
        return Pair(currentSong, currentOffset - currentSong.msOffset)
    }

    companion object {
        const val START_TIME = 1610539200000L
    }
}