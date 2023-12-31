package com.masyanolchik.grandtheftradio2.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.masyanolchik.grandtheftradio2.db.game.LocalGame
import com.masyanolchik.grandtheftradio2.db.song.LocalSong
import com.masyanolchik.grandtheftradio2.db.station.LocalStation
import com.masyanolchik.grandtheftradio2.db.station.StationSongsCrossRef
import com.masyanolchik.grandtheftradio2.db.station.StationsDao

@Database(
    entities = [
        LocalGame::class,
        LocalSong::class,
        LocalStation::class,
        StationSongsCrossRef::class,
    ],
    version = 1,
    exportSchema = true
)
abstract class LocalDatabase: RoomDatabase() {

    abstract fun stationsDao(): StationsDao
    companion object {
        const val DB_NAME="localDataSource"
    }
}