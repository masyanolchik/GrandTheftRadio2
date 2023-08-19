package com.masyanolchik.grandtheftradio2.stationstree

import androidx.media3.common.MediaItem

interface StationsTreeItem {
    fun toMediaItem(): MediaItem

    companion object {
        const val ROOT_ID = "[rootID]"
        const val ERA_ID = "[eraId]"
        const val SONG_PREFIX = "[song]"
        const val GAME_PREFIX = "[game]"
        const val STATION_PREFIX = "[station]"
    }
}