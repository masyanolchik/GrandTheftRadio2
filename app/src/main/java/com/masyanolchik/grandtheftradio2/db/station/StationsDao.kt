package com.masyanolchik.grandtheftradio2.db.station

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.masyanolchik.grandtheftradio2.db.game.LocalGame
import com.masyanolchik.grandtheftradio2.db.song.LocalSong

@Dao
interface StationsDao {
    @Insert(onConflict = REPLACE)
    fun addGame(localGame: LocalGame)

    @Insert(onConflict = REPLACE)
    fun addGames(localGames: List<LocalGame>)

    @Insert(onConflict = REPLACE)
    fun addSongs(localSongs: List<LocalSong>)

    @Insert(onConflict = REPLACE)
    fun addStation(localStation: LocalStation)

    @Update
    fun updateStation(localStation: LocalStation)

    @Insert(onConflict = REPLACE)
    fun addStationSongsCrossRef(stationSongsCrossRef: StationSongsCrossRef)

    @Insert(onConflict = REPLACE)
    fun addStations(localStations: List<LocalStation>)

    @Query("SELECT * from games")
    fun getAllGames(): List<LocalGame>

    @Query("SELECT * from songs")
    fun getAllSongs(): List<LocalSong>

    @Transaction
    @Query("SELECT * from stations")
    fun getAllStations(): List<LocalStationWithAdditionalAttributes>

    @Query("DELETE FROM stations")
    fun deleteAllStations()

    @Query("DELETE FROM songs")
    fun deleteAllSongs()

    @Query("DELETE FROM games")
    fun deleteAllGames()
}