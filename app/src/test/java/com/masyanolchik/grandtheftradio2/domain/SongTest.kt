package com.masyanolchik.grandtheftradio2.domain

import android.net.Uri
import androidx.media3.common.MediaMetadata
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.masyanolchik.grandtheftradio2.stationstree.StationsTreeItem
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
class SongTest {
    @After
    fun stopApp() {
        stopKoin()
    }

    @Test
    fun testSong_implementsStationsTreeItem() {
        val song = Song(
            id = 0,
            artist = "Bob Doe",
            prevSongId = 1,
            nextSongId = 1,
            title = "No name",
            msOffset = 0L,
            link = "link",
            radioName = "station no name",
            picLink = "link",
            msTotalLength = 30L
        )

        assertThat(song).isInstanceOf(StationsTreeItem::class.java)
    }

    @Test
    fun testSong_mediaItemConversionCorrect() {
        val song = Song(
            id = 0,
            artist = "Bob Doe",
            prevSongId = 1,
            nextSongId = 1,
            title = "No name",
            msOffset = 0L,
            link = "link",
            radioName = "station no name",
            picLink = "https://www.link.com",
            msTotalLength = 30L
        )

        val mediaItem = song.toMediaItem()
        val mediaItemMetadata = mediaItem.mediaMetadata

        assertThat(mediaItem.mediaId)
            .isEqualTo(StationsTreeItem.SONG_PREFIX + song.id)
        assertThat(mediaItem.localConfiguration?.uri).isEqualTo(Uri.parse(song.link))
        assertThat(mediaItemMetadata.mediaType).isEqualTo(MediaMetadata.MEDIA_TYPE_MUSIC)
        assertThat(mediaItemMetadata.isBrowsable).isEqualTo(false)
        assertThat(mediaItemMetadata.isPlayable).isEqualTo(true)
        assertThat(mediaItemMetadata.title).isEqualTo(song.title)
        assertThat(mediaItemMetadata.artist).isEqualTo(song.artist)
        assertThat(mediaItemMetadata.subtitle).isEqualTo(song.artist)
        assertThat(mediaItemMetadata.station).isEqualTo(song.radioName)
        assertThat(mediaItemMetadata.artworkUri).isEqualTo(Uri.parse(song.picLink))
    }
}