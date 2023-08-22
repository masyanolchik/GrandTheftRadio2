package com.masyanolchik.grandtheftradio2.stationstree.repository

import android.database.sqlite.SQLiteConstraintException
import com.google.common.truth.Truth.assertThat
import com.masyanolchik.grandtheftradio2.db.game.LocalGame
import com.masyanolchik.grandtheftradio2.db.game.fromDomain
import com.masyanolchik.grandtheftradio2.db.song.LocalSong
import com.masyanolchik.grandtheftradio2.db.song.LocalSongPrevNext
import com.masyanolchik.grandtheftradio2.db.song.LocalSongWithAdditionalAttributes
import com.masyanolchik.grandtheftradio2.db.song.fromDomain
import com.masyanolchik.grandtheftradio2.db.song.fromPrevNextDomain
import com.masyanolchik.grandtheftradio2.db.station.LocalStation
import com.masyanolchik.grandtheftradio2.db.station.LocalStationWithAdditionalAttributes
import com.masyanolchik.grandtheftradio2.db.station.StationSongsCrossRef
import com.masyanolchik.grandtheftradio2.db.station.StationsDao
import com.masyanolchik.grandtheftradio2.db.station.fromDomain
import com.masyanolchik.grandtheftradio2.domain.Game
import com.masyanolchik.grandtheftradio2.domain.Song
import com.masyanolchik.grandtheftradio2.domain.Station
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import com.masyanolchik.grandtheftradio2.domain.Result
import org.junit.After
import org.koin.core.context.stopKoin
import org.mockito.Mockito.`when`

@RunWith(MockitoJUnitRunner::class)
class StationsRepositoryImplTest {

    private var mockedStationsDao: StationsDao = mock(StationsDao::class.java)
    private var stationsRepositoryImpl = StationsRepositoryImpl(mockedStationsDao)

    @After
    fun stopApp() {
        stopKoin()
    }

    @Test
    fun testStationsRepositoryImpl_saveStations_stationsAreAddedWithComplete() = runTest{
        val stationList = listOf(FIRST_STATION, SECOND_STATION)
        val expectedStationSongCrossRefList =
            stationList.map {
                val stationId = it.id
                buildList {
                    it.songs.forEach { song ->
                        add(StationSongsCrossRef(
                            stationId,
                            song.id
                        ))
                    }
                }
            }.flatten()
        val localStationsList = mutableListOf<LocalStation>()
        val localGamesList = mutableListOf<LocalGame>()
        val localSongsList = mutableListOf<LocalSong>()
        val localSongPrevNextList = mutableListOf<LocalSongPrevNext>()
        val localLocalStationLocalSongCrossRefList = mutableListOf<StationSongsCrossRef>()

        setupMocks(
            localStationsList,
            localGamesList,
            localSongsList,
            localSongPrevNextList,
            localLocalStationLocalSongCrossRefList
        )
        val resultFlow = stationsRepositoryImpl.saveStations(stationList)

        assertThat(stationList.map { it.id })
            .isEqualTo(localStationsList.map { it.stationId })
        assertThat(localLocalStationLocalSongCrossRefList)
            .isEqualTo(expectedStationSongCrossRefList)
        assertThat(localGamesList)
            .isEqualTo(
                listOf(
                    GAME_FIRST.fromDomain(),
                    GAME_SECOND.fromDomain()
                )
            )
        assertThat(localSongsList).isEqualTo(
            listOf(
                FIRST_STATION_SONGS_LIST,
                SECOND_STATION_SONGS_LIST
            ).flatten().map {it.fromDomain() }
        )
        assertThat(localSongPrevNextList).isEqualTo(
            listOf(
                FIRST_STATION_SONGS_LIST,
                SECOND_STATION_SONGS_LIST
            ).flatten().map { it.fromPrevNextDomain() }
        )
        assertThat(resultFlow.first()).isInstanceOf(Result.Completed::class.java)
    }

    @Test
    fun testStationsRepositoryImpl_saveStations_couldFailOnAddGame() = runTest {
        val sqLiteConstraintException =
            SQLiteConstraintException("FOREIGN KEY constraint failed (code 787)")
        Mockito.doThrow(sqLiteConstraintException).`when`(mockedStationsDao).addGame(any())

        val resultFlow = stationsRepositoryImpl.saveStations(listOf(FIRST_STATION, SECOND_STATION))

        assertThat(resultFlow.first()).isInstanceOf(Result.Error::class.java)
        assertThat(((resultFlow.first()) as Result.Error).throwable).isEqualTo(sqLiteConstraintException)
    }

    @Test
    fun testStationsRepositoryImpl_saveStations_couldFailOnAddSongs() = runTest {
        val sqLiteConstraintException =
            SQLiteConstraintException("FOREIGN KEY constraint failed (code 787)")
        Mockito.doThrow(sqLiteConstraintException).`when`(mockedStationsDao).addSongs(any())

        val resultFlow = stationsRepositoryImpl.saveStations(listOf(FIRST_STATION, SECOND_STATION))

        assertThat(resultFlow.first()).isInstanceOf(Result.Error::class.java)
        assertThat(((resultFlow.first()) as Result.Error).throwable).isEqualTo(sqLiteConstraintException)
    }

    @Test
    fun testStationsRepositoryImpl_saveStations_couldFailOnAddLocalPrevSong() = runTest {
        val sqLiteConstraintException =
            SQLiteConstraintException("FOREIGN KEY constraint failed (code 787)")
        Mockito.doThrow(sqLiteConstraintException).`when`(mockedStationsDao).addLocalSongPrevNext(any())

        val resultFlow = stationsRepositoryImpl.saveStations(listOf(FIRST_STATION, SECOND_STATION))

        assertThat(resultFlow.first()).isInstanceOf(Result.Error::class.java)
        assertThat(((resultFlow.first()) as Result.Error).throwable).isEqualTo(sqLiteConstraintException)
    }

    @Test
    fun testStationsRepositoryImpl_saveStations_couldFailOnAddStation() = runTest {
        val sqLiteConstraintException =
            SQLiteConstraintException("FOREIGN KEY constraint failed (code 787)")
        Mockito.doThrow(sqLiteConstraintException).`when`(mockedStationsDao).addLocalSongPrevNext(any())

        val resultFlow = stationsRepositoryImpl.saveStations(listOf(FIRST_STATION, SECOND_STATION))

        assertThat(resultFlow.first()).isInstanceOf(Result.Error::class.java)
        assertThat(((resultFlow.first()) as Result.Error).throwable).isEqualTo(sqLiteConstraintException)
    }

    @Test
    fun testStationsRepositoryImpl_saveStations_couldFailOnAddStationSongsCrossRef() = runTest {
        val sqLiteConstraintException =
            SQLiteConstraintException("FOREIGN KEY constraint failed (code 787)")
        Mockito.doThrow(sqLiteConstraintException).`when`(mockedStationsDao).addLocalSongPrevNext(any())

        val resultFlow = stationsRepositoryImpl.saveStations(listOf(FIRST_STATION, SECOND_STATION))

        assertThat(resultFlow.first()).isInstanceOf(Result.Error::class.java)
        assertThat(((resultFlow.first()) as Result.Error).throwable).isEqualTo(sqLiteConstraintException)
    }

    @Test
    fun testStationRepositoryImpl_nukeDatabase_deletesEverything() = runTest {
        val resultFlow = stationsRepositoryImpl.nukeDatabase()

        verify(mockedStationsDao, times(1)).deleteAllGames()
        verify(mockedStationsDao, times(1)).deleteAllSongs()
        verify(mockedStationsDao, times(1)).deleteAllStations()
        assertThat(resultFlow.first()).isInstanceOf(Result.Completed::class.java)
    }

    @Test
    fun testStationRepositoryImpl_nukeDatabase_couldFailOnDeleteAllGames() = runTest {
        val sqLiteConstraintException =
            SQLiteConstraintException("FOREIGN KEY constraint failed (code 787)")
        Mockito.doThrow(sqLiteConstraintException).`when`(mockedStationsDao).deleteAllGames()

        val resultFlow = stationsRepositoryImpl.nukeDatabase()

        verify(mockedStationsDao, times(1)).deleteAllGames()
        verify(mockedStationsDao, times(0)).deleteAllSongs()
        verify(mockedStationsDao, times(0)).deleteAllStations()
        assertThat(resultFlow.first()).isInstanceOf(Result.Error::class.java)
        assertThat((resultFlow.first() as Result.Error).throwable).isEqualTo(sqLiteConstraintException)
    }

    @Test
    fun testStationRepositoryImpl_nukeDatabase_couldFailOnDeleteAllSongs() = runTest {
        val sqLiteConstraintException =
            SQLiteConstraintException("FOREIGN KEY constraint failed (code 787)")
        Mockito.doThrow(sqLiteConstraintException).`when`(mockedStationsDao).deleteAllSongs()

        val resultFlow = stationsRepositoryImpl.nukeDatabase()

        verify(mockedStationsDao, times(1)).deleteAllGames()
        verify(mockedStationsDao, times(1)).deleteAllSongs()
        verify(mockedStationsDao, times(0)).deleteAllStations()
        assertThat(resultFlow.first()).isInstanceOf(Result.Error::class.java)
        assertThat((resultFlow.first() as Result.Error).throwable).isEqualTo(sqLiteConstraintException)
    }

    @Test
    fun testStationRepositoryImpl_nukeDatabase_couldFailOnDeleteAllStations() = runTest {
        val sqLiteConstraintException =
            SQLiteConstraintException("FOREIGN KEY constraint failed (code 787)")
        Mockito.doThrow(sqLiteConstraintException).`when`(mockedStationsDao).deleteAllStations()

        val resultFlow = stationsRepositoryImpl.nukeDatabase()

        verify(mockedStationsDao, times(1)).deleteAllGames()
        verify(mockedStationsDao, times(1)).deleteAllSongs()
        verify(mockedStationsDao, times(1)).deleteAllStations()
        assertThat(resultFlow.first()).isInstanceOf(Result.Error::class.java)
        assertThat((resultFlow.first() as Result.Error).throwable).isEqualTo(sqLiteConstraintException)
    }

    @Test
    fun testStationRepositoryImpl_getAllStations_returnsFlowWithStations() = runTest {
        `when`(mockedStationsDao.getAllStations()).thenReturn(
            listOf(
                LocalStationWithAdditionalAttributes(
                    localStation = FIRST_STATION.fromDomain(),
                    localGame = GAME_FIRST.fromDomain(),
                    localSongs = FIRST_STATION_SONGS_LIST.map {
                        LocalSongWithAdditionalAttributes(
                            localSong = it.fromDomain(),
                            prevNextSongs = it.fromPrevNextDomain()
                        )
                    }
                )
            )
        )

        val resultFlow = stationsRepositoryImpl.getAllStations()

        assertThat(resultFlow.first()).isInstanceOf(Result.Success::class.java)
        assertThat((resultFlow.first() as Result.Success).data.first()).isEqualTo(FIRST_STATION)
    }

    @Test
    fun testStationRepositoryImpl_getAllStations_couldFailWithNpe() = runTest {
        `when`(mockedStationsDao.getAllStations()).thenThrow(NullPointerException())

        val resultFlow = stationsRepositoryImpl.getAllStations()

        assertThat(resultFlow.first()).isInstanceOf(Result.Error::class.java)
        assertThat((resultFlow.first() as Result.Error).throwable)
            .isInstanceOf(NullPointerException::class.java)
    }

    private fun setupMocks(
        localStationsList: MutableList<LocalStation> = mutableListOf(),
        localGamesList: MutableList<LocalGame> = mutableListOf(),
        localSongsList: MutableList<LocalSong> = mutableListOf(),
        localSongPrevNextList: MutableList<LocalSongPrevNext> = mutableListOf(),
        localLocalStationLocalSongCrossRefList: MutableList<StationSongsCrossRef> = mutableListOf(),
    ) {
        Mockito.doAnswer {
            val localGame = it.arguments[0] as LocalGame
            localGamesList.add(localGame)
            null
        }.`when`(mockedStationsDao).addGame(any())
        Mockito.doAnswer {
            val localSongs = it.arguments[0] as List<LocalSong>
            localSongsList.addAll(localSongs)
            null
        }.`when`(mockedStationsDao).addSongs(any())
        Mockito.doAnswer {
            val localSongPrevNext = it.arguments[0] as List<LocalSongPrevNext>
            localSongPrevNextList.addAll(localSongPrevNext)
            null
        }.`when`(mockedStationsDao).addLocalSongPrevNext(any())
        Mockito.doAnswer {
            val station = it.arguments[0] as LocalStation
            localStationsList.add(station)
            null
        }.`when`(mockedStationsDao).addStation(any())
        Mockito.doAnswer {
            val stationSongCrossRef = it.arguments[0] as StationSongsCrossRef
            localLocalStationLocalSongCrossRefList.add(stationSongCrossRef)
            null
        }.`when`(mockedStationsDao).addStationSongsCrossRef(any())

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
    }

}