package com.masyanolchik.grandtheftradio2.db.station

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import com.masyanolchik.grandtheftradio2.db.song.LocalSong

@Entity(
    primaryKeys = ["stationId","songId"],
    indices = [Index("stationId"), Index("songId")],
    foreignKeys = [
        ForeignKey(
            parentColumns = ["stationId"],
            childColumns = ["stationId"],
            entity = LocalStation::class,
            onDelete = CASCADE,
            onUpdate = CASCADE,
        ),
        ForeignKey(
            parentColumns = ["songId"],
            childColumns = ["songId"],
            entity = LocalSong::class,
            onDelete = CASCADE,
            onUpdate = CASCADE,
        )
    ]
)
data class StationSongsCrossRef(
    val stationId: Int,
    val songId: Int,
)
