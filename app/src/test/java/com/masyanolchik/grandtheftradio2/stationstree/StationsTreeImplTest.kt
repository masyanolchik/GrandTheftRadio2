package com.masyanolchik.grandtheftradio2.stationstree

import androidx.media3.common.MediaMetadata
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.masyanolchik.grandtheftradio2.domain.Game
import com.masyanolchik.grandtheftradio2.domain.Song
import com.masyanolchik.grandtheftradio2.domain.Station
import com.masyanolchik.grandtheftradio2.stationstree.repository.StationsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.robolectric.RuntimeEnvironment
import com.masyanolchik.grandtheftradio2.domain.Result
import com.masyanolchik.grandtheftradio2.stationstree.StationsTreeItem.Companion.ERA_ID
import com.masyanolchik.grandtheftradio2.stationstree.StationsTreeItem.Companion.GAME_PREFIX
import com.masyanolchik.grandtheftradio2.stationstree.StationsTreeItem.Companion.ROOT_ID
import com.masyanolchik.grandtheftradio2.stationstree.StationsTreeItem.Companion.SONG_PREFIX
import com.masyanolchik.grandtheftradio2.stationstree.StationsTreeItem.Companion.STATION_PREFIX
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test
import org.koin.core.context.stopKoin
import org.mockito.Mockito.spy
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.kotlin.any

@RunWith(AndroidJUnit4::class)
class StationsTreeImplTest {
    private val mockedStationsRepository = mock(StationsRepository::class.java)

    @Before
    fun setup() {
        Mockito.`when`(mockedStationsRepository.getAllStations())
            .thenReturn(
                flowOf(
                    Result.Success(
                        listOf(FIRST_STATION, SECOND_STATION, THIRD_STATION)
                    )
                )
            )
    }

    @After
    fun stopApp() {
        stopKoin()
    }

    @Test
    fun testStationTreeImpl_rootItemInitializedCorrectly() = runTest {
        val dispatcher = StandardTestDispatcher()
        val scope = CoroutineScope(dispatcher)
        val stationsTreeImpl =
            StationsTreeImpl(
                mockedStationsRepository,
                scope,
                RuntimeEnvironment.getApplication().applicationContext
        )

        dispatcher.scheduler.advanceUntilIdle()

        val rootItem = stationsTreeImpl.getRoot()
        val rootMediaItem = rootItem?.toMediaItem()
        assertThat(rootItem).isNotNull()
        assertThat(rootMediaItem?.mediaId).isEqualTo(ROOT_ID)
        assertThat(rootMediaItem?.mediaMetadata?.isPlayable).isFalse()
        assertThat(rootMediaItem?.mediaMetadata?.isBrowsable).isTrue()
        assertThat(rootMediaItem?.mediaMetadata?.mediaType)
            .isEqualTo(MediaMetadata.MEDIA_TYPE_FOLDER_MIXED)
    }

    @Test
    fun testStationTreeImpl_rootItemChildrenInitializedCorrectly() = runTest {
        val dispatcher = StandardTestDispatcher()
        val scope = CoroutineScope(dispatcher)
        val stationsTreeImpl =
            StationsTreeImpl(
                mockedStationsRepository,
                scope,
                RuntimeEnvironment.getApplication().applicationContext
            )

        dispatcher.scheduler.advanceUntilIdle()

        assertThat(stationsTreeImpl.getChildren(ROOT_ID).map { it.toMediaItem().mediaId })
            .isEqualTo(listOf(ERA_ID+"2D", ERA_ID+"3D", ERA_ID+"HD"))
        assertThat(
            stationsTreeImpl
                .getChildren(ROOT_ID)
                .map { it.toMediaItem().mediaMetadata.isBrowsable }
                .toSet()
        ).isEqualTo(setOf(true))
        assertThat(
            stationsTreeImpl
                .getChildren(ROOT_ID)
                .map { it.toMediaItem().mediaMetadata.isPlayable }
                .toSet()
        ).isEqualTo(setOf(false))
        assertThat(
            stationsTreeImpl
                .getChildren(ROOT_ID)
                .map { it.toMediaItem().mediaMetadata.mediaType }
                .toSet()
        ).isEqualTo(setOf(MediaMetadata.MEDIA_TYPE_FOLDER_ALBUMS))
        assertThat(stationsTreeImpl.getItem(GAME_PREFIX+ GAME_FIRST.id)).isEqualTo(GAME_FIRST)
        assertThat(stationsTreeImpl.getItem(GAME_PREFIX+ GAME_SECOND.id)).isEqualTo(GAME_SECOND)
        assertThat(stationsTreeImpl.getItem(GAME_PREFIX+ GAME_THIRD.id)).isEqualTo(GAME_THIRD)
    }

    @Test
    fun testStationTreeImpl_containsAddedGames() = runTest {
        val dispatcher = StandardTestDispatcher()
        val scope = CoroutineScope(dispatcher)
        val stationsTreeImpl =
            StationsTreeImpl(
                mockedStationsRepository,
                scope,
                RuntimeEnvironment.getApplication().applicationContext
            )

        dispatcher.scheduler.advanceUntilIdle()

        assertThat(stationsTreeImpl.getChildren(ERA_ID + "2D")).contains(GAME_FIRST)
        assertThat(stationsTreeImpl.getChildren(ERA_ID + "3D")).contains(GAME_SECOND)
        assertThat(stationsTreeImpl.getChildren(ERA_ID + "HD")).contains(GAME_THIRD)
        assertThat(stationsTreeImpl.getItem(GAME_PREFIX + GAME_FIRST.id)).isEqualTo(GAME_FIRST)
        assertThat(stationsTreeImpl.getItem(GAME_PREFIX + GAME_SECOND.id)).isEqualTo(GAME_SECOND)
        assertThat(stationsTreeImpl.getItem(GAME_PREFIX + GAME_THIRD.id)).isEqualTo(GAME_THIRD)
    }

    @Test
    fun testStationTreeImpl_containsAddedStations() = runTest {
        val dispatcher = StandardTestDispatcher()
        val scope = CoroutineScope(dispatcher)
        val stationsTreeImpl =
            StationsTreeImpl(
                mockedStationsRepository,
                scope,
                RuntimeEnvironment.getApplication().applicationContext
            )

        dispatcher.scheduler.advanceUntilIdle()

        listOf(FIRST_STATION, SECOND_STATION, THIRD_STATION).forEach {
            assertThat(stationsTreeImpl.getChildren(GAME_PREFIX + it.id)).contains(it)
            assertThat(stationsTreeImpl.getItem(STATION_PREFIX + it.id)).isEqualTo(it)
        }
    }

    @Test
    fun testStationTreeImpl_containsAddedSongs() = runTest {
        val dispatcher = StandardTestDispatcher()
        val scope = CoroutineScope(dispatcher)
        val stationsTreeImpl =
            StationsTreeImpl(
                mockedStationsRepository,
                scope,
                RuntimeEnvironment.getApplication().applicationContext
            )

        dispatcher.scheduler.advanceUntilIdle()

        listOf(FIRST_STATION, SECOND_STATION, THIRD_STATION).forEach { station ->
            val storedSongs = stationsTreeImpl.getChildren(STATION_PREFIX + station.id)
                .map { it as Song }
            val expectedSongs = buildList {
                addAll(FIRST_STATION_SONGS_LIST)
                addAll(SECOND_STATION_SONGS_LIST)
                addAll(THIRD_STATION_SONGS_LIST)
            }
            storedSongs.forEach { song ->
                assertThat(expectedSongs).contains(stationsTreeImpl.getItem(SONG_PREFIX + song.id))
            }
        }
    }

    @Test
    fun testStationTreeImpl_reinitializationWorksCorrectly() = runTest {
        val dispatcher = StandardTestDispatcher()
        val scope = CoroutineScope(dispatcher)
        val stationsTreeImpl = spy(
            StationsTreeImpl(
                mockedStationsRepository,
                scope,
                RuntimeEnvironment.getApplication().applicationContext
            )
        )
        stationsTreeImpl.reinitialize(listOf(FIRST_STATION, SECOND_STATION, THIRD_STATION))
        dispatcher.scheduler.advanceUntilIdle()

        verify(mockedStationsRepository, times(1)).nukeDatabase()
        verify(mockedStationsRepository, times(1)).saveStations(any())
    }

    companion object {
        private val GAME_FIRST = Game(
            id = 0,
            gameName = "GameName1",
            universe = "2D"
        )
        private val GAME_SECOND = Game(
            id = 1,
            gameName = "GameName2",
            universe = "3D"
        )
        private val GAME_THIRD = Game(
            id = 2,
            gameName = "GameName3",
            universe = "HD"
        )
        private val FIRST_STATION_SONGS_LIST = listOf(
            Song(
                id = 0,
                prevSongId = 2,
                nextSongId = 1,
                artist = "art1",
                title = "title1",
                msOffset = 0L,
                link = "link",
                radioName = "station name1",
                picLink = "picLink",
                msTotalLength = 300L
            ),
            Song(
                id = 1,
                prevSongId = 0,
                nextSongId = 2,
                artist = "art1",
                title = "title2",
                msOffset = 50L,
                link = "link",
                radioName = "station name1",
                picLink = "picLink",
                msTotalLength = 300L
            ),
            Song(
                id = 2,
                prevSongId = 1,
                nextSongId = 0,
                artist = "art2",
                title = "title3",
                msOffset = 100L,
                link = "link",
                radioName = "station name1",
                picLink = "picLink",
                msTotalLength = 300L
            )
        )
        private val SECOND_STATION_SONGS_LIST = listOf(
            Song(
                id = 3,
                prevSongId = 5,
                nextSongId = 4,
                artist = "art3",
                title = "title4",
                msOffset = 0L,
                link = "link",
                radioName = "station name1",
                picLink = "picLink",
                msTotalLength = 100L
            ),
            Song(
                id = 4,
                prevSongId = 3,
                nextSongId = 5,
                artist = "art3",
                title = "title5",
                msOffset = 20L,
                link = "link",
                radioName = "station name1",
                picLink = "picLink",
                msTotalLength = 100L
            ),
            Song(
                id = 5,
                prevSongId = 4,
                nextSongId = 3,
                artist = "art5",
                title = "title9",
                msOffset = 40L,
                link = "link",
                radioName = "station name1",
                picLink = "picLink",
                msTotalLength = 100L
            )
        )
        private val THIRD_STATION_SONGS_LIST = listOf(
            Song(
                id = 6,
                prevSongId = 8,
                nextSongId = 7,
                artist = "art4",
                title = "title5",
                msOffset = 0L,
                link = "link",
                radioName = "station name3",
                picLink = "picLink",
                msTotalLength = 100L
            ),
            Song(
                id = 7,
                prevSongId = 6,
                nextSongId = 8,
                artist = "art4",
                title = "title8",
                msOffset = 40L,
                link = "link",
                radioName = "station name3",
                picLink = "picLink",
                msTotalLength = 100L
            ),
            Song(
                id = 8,
                prevSongId = 7,
                nextSongId = 6,
                artist = "art4",
                title = "title10",
                msOffset = 80L,
                link = "link",
                radioName = "station name3",
                picLink = "picLink",
                msTotalLength = 100L
            )
        )
        private val FIRST_STATION = Station(
            id = 0,
            game = GAME_FIRST,
            name = "station name1",
            genre = "NA",
            picLink = "",
            songs = FIRST_STATION_SONGS_LIST
        )
        private val SECOND_STATION = Station(
            id = 1,
            game = GAME_SECOND,
            name = "station name2",
            genre = "NA",
            picLink = "",
            songs = SECOND_STATION_SONGS_LIST
        )
        private val THIRD_STATION = Station(
            id = 2,
            game = GAME_THIRD,
            name = "station name3",
            genre = "NA",
            picLink = "",
            songs = THIRD_STATION_SONGS_LIST
        )
    }
}