package com.masyanolchik.grandtheftradio2.db

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.masyanolchik.grandtheftradio2.db.game.LocalGame
import com.masyanolchik.grandtheftradio2.db.song.LocalSong
import com.masyanolchik.grandtheftradio2.db.song.LocalSongPrevNext
import com.masyanolchik.grandtheftradio2.db.station.LocalStation
import com.masyanolchik.grandtheftradio2.db.station.StationSongsCrossRef
import com.masyanolchik.grandtheftradio2.db.station.StationsDao
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
class LocalDatabaseTest {

    private lateinit var localDatabase: LocalDatabase
    private lateinit var stationsDao: StationsDao

    @Before
    fun setUp() {
        localDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            LocalDatabase::class.java
        ).allowMainThreadQueries().build()

        stationsDao = localDatabase.stationsDao()
    }

    @After
    fun closeDatabase() {
        stopKoin()
        localDatabase.close()
    }

    @Test
    fun addSingleGame_gameIsAdded() {
        val localGame = FIRST_GAME

        stationsDao.addGame(localGame)
        val gameList = stationsDao.getAllGames()

        assertThat(localGame).isEqualTo(gameList.first())
    }

    @Test
    fun addSingleGame_onConflictDoesReplace() {
        val localGame = FIRST_GAME

        val localGameWhichReplaces = CONFLICTED_FIRST_GAME

        stationsDao.addGame(localGame)
        stationsDao.addGame(localGameWhichReplaces)
        val gameList = stationsDao.getAllGames()

        assertThat(localGameWhichReplaces).isEqualTo(gameList.first())
    }

    @Test
    fun addListOfGames_gamesAreAdded() {
        val listOfGames = GAME_LIST

        stationsDao.addGames(listOfGames)
        val addedGames = stationsDao.getAllGames()

        assertThat(addedGames).isEqualTo(listOfGames)
    }

    @Test
    fun addConflictedListOfGames_conflictItemsReplaced() {
        val listOfGames = GAME_LIST
        val conflictedListOfGames = CONFLICTED_GAME_LIST

        stationsDao.addGames(listOfGames)
        stationsDao.addGames(conflictedListOfGames)

        val addedGames = stationsDao.getAllGames()

        assertThat(addedGames).contains(CONFLICTED_FIRST_GAME)
        assertThat(addedGames).contains(SECOND_GAME)
        assertThat(addedGames).contains(THIRD_GAME)
    }

    @Test
    fun addGames_clearTable_tableCleared() {
        val listOfGames = GAME_LIST

        stationsDao.addGames(listOfGames)

        val addedGamesBeforeClearing = stationsDao.getAllGames()

        stationsDao.deleteAllGames()

        val addedGamesAfterClearing = stationsDao.getAllGames()

        assertThat(addedGamesBeforeClearing).isNotEmpty()
        assertThat(addedGamesAfterClearing).isEmpty()
    }

    @Test
    fun addSongs_songsAreAdded() {
        val listOfSongs = SONG_LIST

        stationsDao.addSongs(listOfSongs)
        val addedSongs = stationsDao.getAllSongs()

        assertThat(listOfSongs).isEqualTo(addedSongs)
    }

    @Test
    fun addConflictedListOfSongs_conflictItemsReplaced() {
        val listOfSongs = SONG_LIST
        val conflictedListOfSongs = CONFLICTED_SONG_LIST

        stationsDao.addSongs(listOfSongs)
        stationsDao.addSongs(conflictedListOfSongs)
        val addedSongs = stationsDao.getAllSongs()

        assertThat(addedSongs).contains(CONFLICTED_FIRST_SONG)
        assertThat(addedSongs).contains(SECOND_SONG)
        assertThat(addedSongs).contains(THIRD_SONG)
    }

    @Test
    fun addSongs_clearTable_tableCleared() {
        val listOfSongs = SONG_LIST

        stationsDao.addSongs(listOfSongs)

        val addedSongsBeforeClearing = stationsDao.getAllSongs()

        stationsDao.deleteAllSongs()

        val addedSongsAfterClearing = stationsDao.getAllSongs()

        assertThat(addedSongsBeforeClearing).isNotEmpty()
        assertThat(addedSongsAfterClearing).isEmpty()
    }

    @Test
    fun addStation_stationIsAdded() {
        val station = FIRST_STATION
        val songList = SONG_LIST
        val firstPrevNext = LocalSongPrevNext(
            songId = FIRST_SONG.songId,
            prevSongId = SECOND_SONG.songId,
            nextSongId = SECOND_SONG.songId,
        )
        val secondPrevNext = LocalSongPrevNext(
            songId = SECOND_SONG.songId,
            prevSongId = FIRST_SONG.songId,
            nextSongId = FIRST_SONG.songId,
        )

        stationsDao.addSongs(songList)
        stationsDao.addLocalSongPrevNext(
            listOf(firstPrevNext, secondPrevNext)
        )

        stationsDao.addGame(FIRST_GAME)
        stationsDao.addStation(station)
        songList.forEach {
            stationsDao.addStationSongsCrossRef(
                StationSongsCrossRef(
                    stationId = station.stationId,
                    songId = it.songId
                )
            )
        }

        val stationFromDb = stationsDao.getAllStations()

        assertThat(stationFromDb.first().localStation).isEqualTo(station)
        assertThat(stationFromDb.first().localGame).isEqualTo(FIRST_GAME)
        assertThat(stationFromDb.first().localSongs.map { it.localSong }).isEqualTo(songList)
        assertThat(stationFromDb.first().localSongs.map { it.prevNextSongs })
            .isEqualTo(listOf(firstPrevNext, secondPrevNext))
    }

    @Test
    fun addStation_onConflictDoesReplace() {
        val station = FIRST_STATION
        val songList = SONG_LIST
        val firstPrevNext = LocalSongPrevNext(
            songId = FIRST_SONG.songId,
            prevSongId = SECOND_SONG.songId,
            nextSongId = SECOND_SONG.songId,
        )
        val secondPrevNext = LocalSongPrevNext(
            songId = SECOND_SONG.songId,
            prevSongId = FIRST_SONG.songId,
            nextSongId = FIRST_SONG.songId,
        )

        stationsDao.addSongs(songList)
        stationsDao.addLocalSongPrevNext(
            listOf(firstPrevNext, secondPrevNext)
        )

        stationsDao.addGame(FIRST_GAME)
        stationsDao.addStation(station)
        songList.forEach {
            stationsDao.addStationSongsCrossRef(
                StationSongsCrossRef(
                    stationId = station.stationId,
                    songId = it.songId
                )
            )
        }

        val stationFromDb = stationsDao.getAllStations().first()

        stationsDao.addStation(CONFLICTED_FIRST_STATION)
        val stationFromDbAfterReplacing = stationsDao.getAllStations().first()

        assertThat(stationFromDb.localStation).isEqualTo(station)
        assertThat(stationFromDb.localGame).isEqualTo(FIRST_GAME)
        assertThat(stationFromDb.localSongs.map { it.localSong }).isEqualTo(songList)
        assertThat(stationFromDb.localSongs.map { it.prevNextSongs })
            .isEqualTo(listOf(firstPrevNext, secondPrevNext))
        assertThat(stationFromDbAfterReplacing.localStation).isEqualTo(CONFLICTED_FIRST_STATION)
    }

    @Test
    fun addListOfStations_stationsAreAdded() {
        val stations = listOf(FIRST_STATION, SECOND_STATION)
        val songList = SONG_LIST
        val firstPrevNext = LocalSongPrevNext(
            songId = FIRST_SONG.songId,
            prevSongId = SECOND_SONG.songId,
            nextSongId = SECOND_SONG.songId,
        )
        val secondPrevNext = LocalSongPrevNext(
            songId = SECOND_SONG.songId,
            prevSongId = FIRST_SONG.songId,
            nextSongId = FIRST_SONG.songId,
        )

        stationsDao.addSongs(songList)
        stationsDao.addLocalSongPrevNext(
            listOf(firstPrevNext, secondPrevNext)
        )

        stationsDao.addGame(FIRST_GAME)
        stationsDao.addStations(stations)
        songList.forEach {
            stationsDao.addStationSongsCrossRef(
                StationSongsCrossRef(
                    stationId = stations.first().stationId,
                    songId = it.songId
                )
            )
        }

        val stationsFromDb = stationsDao.getAllStations()

        assertThat(stationsFromDb.map { it.localStation }).isEqualTo(stations)
    }

    @Test
    fun addConflictedListOfStations_conflictItemsReplaced() {
        val stations = listOf(FIRST_STATION, SECOND_STATION)
        val stationsConflicted = listOf(CONFLICTED_FIRST_STATION, THIRD_STATION)
        val songList = SONG_LIST
        val firstPrevNext = LocalSongPrevNext(
            songId = FIRST_SONG.songId,
            prevSongId = SECOND_SONG.songId,
            nextSongId = SECOND_SONG.songId,
        )
        val secondPrevNext = LocalSongPrevNext(
            songId = SECOND_SONG.songId,
            prevSongId = FIRST_SONG.songId,
            nextSongId = FIRST_SONG.songId,
        )

        stationsDao.addSongs(songList)
        stationsDao.addLocalSongPrevNext(
            listOf(firstPrevNext, secondPrevNext)
        )

        stationsDao.addGame(FIRST_GAME)
        stationsDao.addGame(SECOND_GAME)
        stationsDao.addStations(stations)
        songList.forEach {
            stationsDao.addStationSongsCrossRef(
                StationSongsCrossRef(
                    stationId = stations.first().stationId,
                    songId = it.songId
                )
            )
        }

        val stationsFromDbBeforeReplacing = stationsDao.getAllStations()
        stationsDao.addStations(stationsConflicted)
        val stationsFromDbAfterReplacing = stationsDao.getAllStations()

        assertThat(stationsFromDbBeforeReplacing.map { it.localStation }).isEqualTo(stations)
        assertThat(stationsFromDbAfterReplacing.map { it.localStation })
            .contains(CONFLICTED_FIRST_STATION)
        assertThat(stationsFromDbAfterReplacing.map { it.localStation }).contains(THIRD_STATION)
    }

    @Test
    fun addStations_clearTable_tableCleared() {
        val stations = listOf(FIRST_STATION, SECOND_STATION)
        val songList = SONG_LIST
        val firstPrevNext = LocalSongPrevNext(
            songId = FIRST_SONG.songId,
            prevSongId = SECOND_SONG.songId,
            nextSongId = SECOND_SONG.songId,
        )
        val secondPrevNext = LocalSongPrevNext(
            songId = SECOND_SONG.songId,
            prevSongId = FIRST_SONG.songId,
            nextSongId = FIRST_SONG.songId,
        )

        stationsDao.addSongs(songList)
        stationsDao.addLocalSongPrevNext(
            listOf(firstPrevNext, secondPrevNext)
        )

        stationsDao.addGame(FIRST_GAME)
        stationsDao.addStations(stations)
        songList.forEach {
            stationsDao.addStationSongsCrossRef(
                StationSongsCrossRef(
                    stationId = stations.first().stationId,
                    songId = it.songId
                )
            )
        }
        stationsDao.deleteAllStations()

        val stationsFromDb = stationsDao.getAllStations()

        assertThat(stationsFromDb).isEmpty()
    }
    companion object {
        private val FIRST_GAME = LocalGame(
            gameId = 0,
            gameName = "GameName",
            universe = "2D"
        )
        private val CONFLICTED_FIRST_GAME = LocalGame(
            gameId = 0,
            gameName = "GameName",
            universe = "3D"
        )
        private val SECOND_GAME = LocalGame(
            gameId = 1,
            gameName = "GameName1",
            universe = "3D"
        )
        private val THIRD_GAME = LocalGame(
            gameId = 2,
            gameName = "GameName2",
            universe = "3D"
        )
        private val GAME_LIST = listOf(FIRST_GAME, SECOND_GAME)
        private val CONFLICTED_GAME_LIST = listOf(CONFLICTED_FIRST_GAME, THIRD_GAME)
        private val FIRST_SONG = LocalSong(
            songId = 0,
            artist = "John Doe",
            title = "No name",
            msOffset = 0L,
            link = "link",
            radioName = "station no name",
            picLink = "link",
            msTotalLength = 30L
        )
        private val CONFLICTED_FIRST_SONG = LocalSong(
            songId = 0,
            artist = "Bob Doe",
            title = "No name",
            msOffset = 0L,
            link = "link",
            radioName = "station no name",
            picLink = "link",
            msTotalLength = 30L
        )
        private val SECOND_SONG = LocalSong(
            songId = 1,
            artist = "Bob Doe",
            title = "No name",
            msOffset = 10L,
            link = "link",
            radioName = "station no name",
            picLink = "link",
            msTotalLength = 30L
        )
        private val THIRD_SONG = LocalSong(
            songId = 2,
            artist = "John March",
            title = "No name",
            msOffset = 20L,
            link = "link",
            radioName = "station no name",
            picLink = "link",
            msTotalLength = 30L
        )
        private val SONG_LIST = listOf(FIRST_SONG, SECOND_SONG)
        private val CONFLICTED_SONG_LIST = listOf(CONFLICTED_FIRST_SONG, THIRD_SONG)
        private val FIRST_STATION = LocalStation(
            stationId = 0,
            gameId = 0,
            name = "Station name",
            genre = "Doesnt matter",
            picLink = "link"
        )
        private val CONFLICTED_FIRST_STATION = LocalStation(
            stationId = 0,
            gameId = 0,
            name = "Station NAME",
            genre = "Reallly matters",
            picLink = "link"
        )
        private val SECOND_STATION = LocalStation(
            stationId = 1,
            gameId = 0,
            name = "Station name 2",
            genre = "Doesnt matter",
            picLink = "link"
        )
        private val THIRD_STATION = LocalStation(
            stationId = 2,
            gameId = 1,
            name = "Station name 3",
            genre = "who cares",
            picLink = "link"
        )
    }
}