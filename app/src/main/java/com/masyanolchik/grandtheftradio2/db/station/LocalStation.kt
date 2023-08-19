package com.masyanolchik.grandtheftradio2.db.station

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey
import com.masyanolchik.grandtheftradio2.db.game.LocalGame
import com.masyanolchik.grandtheftradio2.db.game.toDomain
import com.masyanolchik.grandtheftradio2.db.song.toDomain
import com.masyanolchik.grandtheftradio2.domain.Station

@Entity(
    tableName="stations",
    foreignKeys = [
        ForeignKey(
            parentColumns = ["gameId"],
            childColumns = ["gameId"],
            entity = LocalGame::class,
            onDelete = CASCADE,
            onUpdate = CASCADE,
        ),
    ]
)
data class LocalStation(
    @PrimaryKey val stationId: Int,
    val gameId: Int,
    val name: String,
    val genre: String,
    val picLink: String,
)

fun LocalStationWithAdditionalAttributes.toDomain() =
    Station(
        id = localStation.stationId,
        game = localGame.toDomain(),
        name = localStation.name,
        genre = localStation.genre,
        picLink = localStation.picLink,
        songs = localSongs.map { it.toDomain() }
    )

fun Station.fromDomain() =
    LocalStation(
        stationId = id,
        gameId = game.id,
        name = name,
        genre = genre,
        picLink = picLink
    )