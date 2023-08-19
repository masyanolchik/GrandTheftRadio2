package com.masyanolchik.grandtheftradio2.domain

import androidx.media3.common.MediaMetadata
import com.google.common.truth.Truth.assertThat
import com.masyanolchik.grandtheftradio2.stationstree.StationsTreeItem
import org.junit.Test

class GameTest {
    @Test
    fun testGame_implementsStationsTreeItem() {
        val game = Game(
            id = 0,
            gameName = "GameName",
            universe = "2D"
        )

        assertThat(game).isInstanceOf(StationsTreeItem::class.java)
    }

    @Test
    fun testGame_mediaItemConversionCorrect() {
        val game = Game(
            id = 0,
            gameName = "GameName",
            universe = "2D"
        )

        val mediaItem = game.toMediaItem()

        assertThat(mediaItem.mediaId).isEqualTo(StationsTreeItem.GAME_PREFIX +game.id)
        assertThat(mediaItem.mediaMetadata.title).isEqualTo(game.gameName)
        assertThat(mediaItem.mediaMetadata.isBrowsable).isEqualTo(true)
        assertThat(mediaItem.mediaMetadata.isPlayable).isEqualTo(false)
        assertThat(mediaItem.mediaMetadata.mediaType).isEqualTo(MediaMetadata.MEDIA_TYPE_ARTIST)
    }
}