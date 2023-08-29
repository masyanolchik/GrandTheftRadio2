package com.masyanolchik.grandtheftradio2.stations.model

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.masyanolchik.grandtheftradio2.domain.Game
import com.masyanolchik.grandtheftradio2.domain.Result
import com.masyanolchik.grandtheftradio2.domain.Song
import com.masyanolchik.grandtheftradio2.domain.Station
import com.masyanolchik.grandtheftradio2.stationstree.StationsTree
import com.masyanolchik.grandtheftradio2.stationstree.StationsTreeItem
import com.masyanolchik.grandtheftradio2.stationstree.StationsTreeItem.Companion.ERA_ID
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.mock


@RunWith(AndroidJUnit4::class)
class StationModelTest {
    private val mockedStationsTree: StationsTree = mock()
    private lateinit var stationModel: StationModel

    @Before
    fun setup() {
        stationModel = StationModel(mockedStationsTree)
    }

    @After
    fun stopDi() {
        stopKoin()
    }

    @Test
    fun testStationModel_getItemsForEra_eraExistsListReturned() = runTest {
        val itemList = listOf(
            GAME_FIRST,
            GAME_THIRD,
            GAME_SECOND,
            FIRST_STATION,
            SECOND_STATION,
            THIRD_STATION
        )
        Mockito.`when`(mockedStationsTree.getChildren(any())).then {
            itemList
        }

        val result = stationModel.getItemsForEra(ERA_ID+ GAME_FIRST.universe).first()

        assertThat(result).isInstanceOf(Result.Success::class.java)
        assertThat((result as Result.Success).data.filterIsInstance<Game>()).isEqualTo(
            listOf(
                GAME_FIRST,
                GAME_SECOND,
                GAME_THIRD
            )
        )
        assertThat(result.data[result.data.indexOf(GAME_FIRST)+1]).isInstanceOf(Station::class.java)
        assertThat(result.data[result.data.indexOf(GAME_SECOND)-1]).isInstanceOf(Station::class.java)
        assertThat(result.data[result.data.indexOf(GAME_SECOND)+1]).isInstanceOf(Station::class.java)
        assertThat(result.data[result.data.indexOf(GAME_THIRD)-1]).isInstanceOf(Station::class.java)
        assertThat(result.data[result.data.indexOf(GAME_THIRD)+1]).isInstanceOf(Station::class.java)
    }

    @Test
    fun testStationModel_getItemsForEra_eraExistsGameAddedNoStations() = runTest {
        val itemList = listOf(
            GAME_FIRST,
            GAME_THIRD,
            GAME_SECOND,
        )
        Mockito.`when`(mockedStationsTree.getChildren(any())).then {
            itemList
        }

        val result = stationModel.getItemsForEra(GAME_FIRST.universe).first()

        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).throwable.message).isEqualTo(
            "Error! Station tree doesn't have children for the ${ERA_ID+GAME_FIRST.universe}"
        )
    }

    @Test
    fun testStationModel_getItemsForEra_eraNotExists() = runTest {
        Mockito.`when`(mockedStationsTree.getChildren(any())).thenReturn(emptyList())

        val result = stationModel.getItemsForEra(GAME_FIRST.universe).first()

        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).throwable.message).isEqualTo(
            "Error! Station tree doesn't have children for the ${ERA_ID+GAME_FIRST.universe}"
        )
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
            universe = "2D"
        )
        private val GAME_THIRD = Game(
            id = 2,
            gameName = "GameName3",
            universe = "2D"
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