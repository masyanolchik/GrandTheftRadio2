package com.masyanolchik.grandtheftradio2.db.station

import com.google.common.truth.Truth.assertThat
import com.masyanolchik.grandtheftradio2.db.game.LocalGame
import com.masyanolchik.grandtheftradio2.db.game.toDomain
import com.masyanolchik.grandtheftradio2.db.song.toDomain
import com.masyanolchik.grandtheftradio2.domain.Game
import com.masyanolchik.grandtheftradio2.domain.Station
import org.junit.Test

class LocalStationExtensionsTest {
    @Test
    fun convertStationToLocalStation() {
        val station = Station(
            id = 0,
            game = Game(
                id = 0,
                gameName = "GameName",
                universe = "2D"
            ),
            name = "station name",
            genre = "NA",
            picLink = "",
            songs = emptyList()
        )

        val localStation = station.fromDomain()

        assertThat(localStation.stationId).isEqualTo(station.id)
        assertThat(localStation.gameId).isEqualTo(station.game.id)
        assertThat(localStation.name).isEqualTo(station.name)
        assertThat(localStation.genre).isEqualTo(station.genre)
        assertThat(localStation.picLink).isEqualTo(station.picLink)
    }

    @Test
    fun convertLocalStationToStation() {
        val localStationWithAdditionalAttributes =
            LocalStationWithAdditionalAttributes(
                localStation = LocalStation(
                    stationId = 0,
                    gameId = 0,
                    name = "station name",
                    genre = "NA",
                    picLink = ""
                ),
                localGame = LocalGame(
                    gameId = 0,
                    gameName = "GameName",
                    universe = "2D"
                ),
                localSongs = emptyList()
            )

        val station = localStationWithAdditionalAttributes.toDomain()

        assertThat(localStationWithAdditionalAttributes.localStation.stationId)
            .isEqualTo(station.id)
        assertThat(localStationWithAdditionalAttributes.localStation.gameId)
            .isEqualTo(station.game.id)
        assertThat(localStationWithAdditionalAttributes.localGame.toDomain())
            .isEqualTo(station.game)
        assertThat(localStationWithAdditionalAttributes.localStation.name)
            .isEqualTo(station.name)
        assertThat(localStationWithAdditionalAttributes.localStation.genre)
            .isEqualTo(station.genre)
        assertThat(localStationWithAdditionalAttributes.localStation.picLink)
            .isEqualTo(station.picLink)
        assertThat(localStationWithAdditionalAttributes.localSongs.map { it.toDomain() })
            .isEqualTo(station.songs)
    }
}