package com.masyanolchik.grandtheftradio2.db.game

import com.google.common.truth.Truth.assertThat
import com.masyanolchik.grandtheftradio2.domain.Game
import org.junit.Test

class LocalGameExtensionsTest {
    @Test
    fun convertLocalGameToGame() {
        val localGame = LocalGame(
            gameId = 0,
            gameName = "GameName",
            universe = "2D"
        )

        val game = localGame.toDomain()

        assertThat(game.id).isEqualTo(localGame.gameId)
        assertThat(game.gameName).isEqualTo(localGame.gameName)
        assertThat(game.universe).isEqualTo(localGame.universe)
    }

    @Test
    fun convertGameToLocalGame() {
        val game = Game(
            id = 0,
            gameName = "GameName",
            universe = "2D"
        )

        val localGame = game.fromDomain()

        assertThat(game.id).isEqualTo(localGame.gameId)
        assertThat(game.gameName).isEqualTo(localGame.gameName)
        assertThat(game.universe).isEqualTo(localGame.universe)
    }
}