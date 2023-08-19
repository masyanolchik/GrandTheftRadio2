package com.masyanolchik.grandtheftradio2.db.game

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.masyanolchik.grandtheftradio2.domain.Game

@Entity(tableName="games")
data class LocalGame(
    @PrimaryKey val gameId: Int,
    val gameName: String,
    val universe: String,
)

fun LocalGame.toDomain() =
    Game(
        id = gameId,
        gameName = gameName,
        universe = universe,
    )

fun Game.fromDomain() =
    LocalGame(
        gameId = id,
        gameName = gameName,
        universe = universe,
    )