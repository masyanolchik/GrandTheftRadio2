package com.masyanolchik.grandtheftradio2.domain

import android.net.Uri
import android.os.SystemClock
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.masyanolchik.grandtheftradio2.domain.Station.Companion.START_TIME
import com.masyanolchik.grandtheftradio2.stationstree.StationsTreeItem
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
class StationTest {

    @After
    fun stopApp() {
        stopKoin()
    }

    @Before
    fun setup() {
        SystemClock.setCurrentTimeMillis(Station.START_TIME)
    }
    @Test
    fun testStation_implementsStationTreeItem() {
        assertThat(STATION_SINGLE_SONG).isInstanceOf(StationsTreeItem::class.java)
    }

    @Test
    fun testStation_mediaItemConversionIsCorrect() {
        val mediaItem = STATION_SINGLE_SONG.toMediaItem()
        val metadata = mediaItem.mediaMetadata

        assertThat(mediaItem.mediaId)
            .isEqualTo(StationsTreeItem.STATION_PREFIX + STATION_SINGLE_SONG.id)
        assertThat(metadata.title)
            .isEqualTo(STATION_SINGLE_SONG.name)
        assertThat(metadata.artworkUri)
            .isEqualTo(Uri.parse(STATION_SINGLE_SONG.picLink))
        assertThat(metadata.isPlayable).isTrue()
        assertThat(metadata.isBrowsable).isTrue()
    }

    @Test
    fun testStation_getCurrentSongWithSeekPosition_returnsCorrectForSingleSongAtStartTime() {
        val(song, seekMillis) = STATION_SINGLE_SONG.getCurrentSongWithSeekPosition(START_TIME)

        assertThat(song).isEqualTo(SINGLE_SONG_LIST.first())
        assertThat(seekMillis).isEqualTo(0L)
    }

    @Test
    fun testStation_getCurrentSongWithSeekPosition_returnsCorrectForSingleSongAfterTotalLengthPassed() {
        val(song, seekMillis) = STATION_SINGLE_SONG.getCurrentSongWithSeekPosition(START_TIME + 300L)

        assertThat(song).isEqualTo(SINGLE_SONG_LIST.first())
        assertThat(seekMillis).isEqualTo(0L)
    }

    @Test
    fun testStation_getCurrentSongWithSeekPosition_returnsCorrectForSingleSongAfterTotalLengthExceeded() {
        val(song, seekMillis) = STATION_SINGLE_SONG.getCurrentSongWithSeekPosition(START_TIME+340L)

        assertThat(song).isEqualTo(SINGLE_SONG_LIST.first())
        assertThat(seekMillis).isEqualTo(40L)
    }

    @Test
    fun testStation_getCurrentSongWithSeekPosition_returnsCorrectForMultipleSongsAtStartTime() {
        val(song, seekMillis) = STATION_MULTIPLE_SONGS.getCurrentSongWithSeekPosition(START_TIME)

        assertThat(song).isEqualTo(STATION_MULTIPLE_SONGS.songs.first())
        assertThat(seekMillis).isEqualTo(0L)
    }

    @Test
    fun testStation_getCurrentSongWithSeekPosition_returnsCorrectForMultipleSongsAfterTotalLengthPassed() {
        val(song, seekMillis) = STATION_MULTIPLE_SONGS.getCurrentSongWithSeekPosition(START_TIME+300L)

        assertThat(song).isEqualTo(STATION_MULTIPLE_SONGS.songs.first())
        assertThat(seekMillis).isEqualTo(0L)
    }

    @Test
    fun testStation_getCurrentSongWithSeekPosition_returnsCorrectForMultipleSongsAfterTotalLengthExceeded() {
        val(song, seekMillis) = STATION_MULTIPLE_SONGS.getCurrentSongWithSeekPosition(START_TIME+310L)

        assertThat(song).isEqualTo(STATION_MULTIPLE_SONGS.songs.first { it.id == 1 })
        assertThat(seekMillis).isEqualTo(1L)
    }

    companion object {
        private val GAME = Game(
            id = 0,
            gameName = "GameName",
            universe = "2D"
        )
        private val SINGLE_SONG_LIST =
            listOf(
                Song(
                    id = 0,
                    prevSongId = 0,
                    nextSongId = 0,
                    artist = "Artist",
                    title = "Title",
                    msOffset = 0L,
                    link = "Link",
                    radioName = "Radio name",
                    picLink = "PicLink",
                    msTotalLength = 300L
                )
            )
        private val MULTIPLE_SONG_LIST =
            listOf(
                Song(
                    id = 0,
                    prevSongId = 2,
                    nextSongId = 1,
                    artist = "Artist",
                    title = "Title",
                    msOffset = 0L,
                    link = "Link",
                    radioName = "Radio name",
                    picLink = "PicLink",
                    msTotalLength = 300L
                ),
                Song(
                    id = 1,
                    prevSongId = 0,
                    nextSongId = 2,
                    artist = "Artist",
                    title = "Title",
                    msOffset = 10L,
                    link = "Link",
                    radioName = "Radio name",
                    picLink = "PicLink",
                    msTotalLength = 300L
                ),
                Song(
                    id = 2,
                    prevSongId = 1,
                    nextSongId = 0,
                    artist = "Artist",
                    title = "Title",
                    msOffset = 144L,
                    link = "Link",
                    radioName = "Radio name",
                    picLink = "PicLink",
                    msTotalLength = 300L
                )
            )
        private val STATION_SINGLE_SONG =
            Station(
                id = 0,
                game = GAME,
                name = "Station name",
                genre = "genre",
                picLink = "picLink",
                songs = SINGLE_SONG_LIST
            )
        private val STATION_MULTIPLE_SONGS =
            Station(
                id = 0,
                game = GAME,
                name = "Station name",
                genre = "genre",
                picLink = "picLink",
                songs = MULTIPLE_SONG_LIST
            )
    }
}