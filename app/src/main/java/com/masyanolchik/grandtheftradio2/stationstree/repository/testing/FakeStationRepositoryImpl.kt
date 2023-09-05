package com.masyanolchik.grandtheftradio2.stationstree.repository.testing

import com.masyanolchik.grandtheftradio2.domain.Game
import com.masyanolchik.grandtheftradio2.domain.Result
import com.masyanolchik.grandtheftradio2.domain.Song
import com.masyanolchik.grandtheftradio2.domain.Station
import com.masyanolchik.grandtheftradio2.stationstree.repository.StationsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeStationRepositoryImpl: StationsRepository {
    private val stationsList = mutableListOf<Station>()

    var isThrowingErrors = false
    override fun saveStations(stations: List<Station>): Flow<Result<Nothing>> {
        return if(isThrowingErrors) {
            flowOf(Result.Error(Exception()))
        } else {
            stationsList.addAll(stations)
            flowOf(Result.Completed())
        }

    }

    override fun updateStation(station: Station) {
        with(stationsList) {
            val prevPos = indexOf(first{ station.id == it.id })
            removeAt(prevPos)
            add(prevPos, station)
        }
    }
    override fun nukeDatabase(): Flow<Result<Nothing>> {
        return if(isThrowingErrors) {
            flowOf(Result.Error(Exception()))
        } else {
            stationsList.clear()
            flowOf(Result.Completed())
        }
    }

    override fun getAllStations(): Flow<Result<List<Station>>> {
        return if(isThrowingErrors) {
            flowOf(Result.Error(Exception()))
        } else {
            flowOf(Result.Success(stationsList))
        }
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
                artist = "art5",
                title = "title9",
                msOffset = 0L,
                link = "link",
                radioName = "station name3",
                picLink = "picLink",
                msTotalLength = 100L
            ),
            Song(
                id =7,
                artist = "art3",
                title = "title5",
                msOffset = 20L,
                link = "link",
                radioName = "station name3",
                picLink = "picLink",
                msTotalLength = 100L
            ),
            Song(
                id = 8,
                artist = "art5",
                title = "title9",
                msOffset = 40L,
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

        val FAKE_STATIONS = listOf(
            FIRST_STATION,
            SECOND_STATION,
            THIRD_STATION
        )
    }
}