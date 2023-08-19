package com.masyanolchik.grandtheftradio2.stationstree.repository

import com.masyanolchik.grandtheftradio2.db.game.fromDomain
import com.masyanolchik.grandtheftradio2.db.song.fromDomain
import com.masyanolchik.grandtheftradio2.db.song.fromPrevNextDomain
import com.masyanolchik.grandtheftradio2.db.station.StationSongsCrossRef
import com.masyanolchik.grandtheftradio2.db.station.StationsDao
import com.masyanolchik.grandtheftradio2.db.station.fromDomain
import com.masyanolchik.grandtheftradio2.db.station.toDomain
import com.masyanolchik.grandtheftradio2.domain.Station
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception
import com.masyanolchik.grandtheftradio2.domain.Result
import kotlinx.coroutines.flow.flowOf

class StationsRepositoryImpl(private val stationsDao: StationsDao): StationsRepository {
    override fun saveStations(stations: List<Station>): Flow<Result<Nothing>> {
        return try {
            val games = stations.map { it.game }.distinct()
            val songs = stations.map { it.songs }.flatten()
            games.forEach { stationsDao.addGame(it.fromDomain()) }
            stationsDao.addSongs(songs.map { it.fromDomain() })
            stationsDao.addLocalSongPrevNext(songs.map { it.fromPrevNextDomain() })
            stations.forEach {station ->
                val localStation = station.fromDomain()
                stationsDao.addStation(localStation)
                station.songs.forEach {
                    stationsDao.addStationSongsCrossRef(
                        StationSongsCrossRef(
                            stationId = station.id,
                            songId = it.id
                        )
                    )
                }

            }
            flowOf(Result.Completed())
        } catch (ex: Exception) {
            flowOf(Result.Error(ex))
        }
    }

    override fun nukeDatabase(): Flow<Result<Nothing>> {
        return try {
            stationsDao.deleteAllGames()
            stationsDao.deleteAllSongs()
            stationsDao.deleteAllStations()
            flowOf(Result.Completed())
        } catch (ex: Exception) {
            flowOf(Result.Error(ex))
        }
    }

    override fun getAllStations(): Flow<Result<List<Station>>> {
        return try {
            flowOf(Result.Success(stationsDao.getAllStations().map { it.toDomain() }))
        } catch (ex: Exception) {
            flowOf(Result.Error(ex))
        }
    }
}