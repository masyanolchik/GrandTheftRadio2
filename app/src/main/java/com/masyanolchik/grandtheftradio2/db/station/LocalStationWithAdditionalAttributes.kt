package com.masyanolchik.grandtheftradio2.db.station

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.masyanolchik.grandtheftradio2.db.game.LocalGame
import com.masyanolchik.grandtheftradio2.db.song.LocalSong
import com.masyanolchik.grandtheftradio2.db.song.LocalSongWithAdditionalAttributes

data class LocalStationWithAdditionalAttributes(
    @Embedded var localStation: LocalStation,
    @Relation(
        parentColumn = "gameId",
        entityColumn = "gameId"
    )
    var localGame: LocalGame,
    @Relation(
        entity = LocalSong::class,
        parentColumn = "stationId",
        entityColumn = "songId",
        associateBy = Junction(StationSongsCrossRef::class)
    )
    var localSongs: List<LocalSongWithAdditionalAttributes>,
)
