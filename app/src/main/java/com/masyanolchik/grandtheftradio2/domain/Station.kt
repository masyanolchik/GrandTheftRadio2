package com.masyanolchik.grandtheftradio2.domain

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.masyanolchik.grandtheftradio2.stationstree.StationsTreeItem
import com.masyanolchik.grandtheftradio2.stationstree.StationsTreeItem.Companion.STATION_PREFIX
import kotlin.math.ceil

data class Station (
    val id: Int,
    val game: Game,
    var favorite: Boolean = false,
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

    fun getCurrentSongWithSeekPosition(currentTimeMillis: Long): Pair<Song, Long> {
        val fullDurationMs = songs.first().msTotalLength
        val timePassed = currentTimeMillis - START_TIME
        val repeatCount = (timePassed.toDouble() / fullDurationMs)
        val currentOffset = ceil(repeatCount % 1 * fullDurationMs).toLong()
        val songsCopy = songs.toMutableList()
        val currentSong = songsCopy.sortedBy { it.msOffset }.last { it.msOffset <= currentOffset }
        return Pair(currentSong, currentOffset - currentSong.msOffset)
    }

    fun getSongsListWithCurrentAtTheTopAndSeekPosition(currentTimeMillis: Long): Pair<List<Song>,Long> {
        val (song, offset) = getCurrentSongWithSeekPosition(currentTimeMillis)
        val rearrangedList = buildList {
            add(song)
            val position = songs.indexOf(song)
            addAll(songs.takeLast(songs.size - 1 - position))
            addAll(songs.take(position))
        }
        return Pair(rearrangedList, offset)
    }

    companion object {
        const val START_TIME = 1610539200000L
    }
}