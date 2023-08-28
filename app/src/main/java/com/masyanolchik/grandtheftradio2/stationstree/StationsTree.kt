package com.masyanolchik.grandtheftradio2.stationstree

import android.media.browse.MediaBrowser.MediaItem
import com.masyanolchik.grandtheftradio2.domain.Station

interface StationsTree {
    suspend fun getItem(id: String): StationsTreeItem?

    suspend fun getRoot(): StationsTreeItem?

    suspend fun getChildren(id: String): List<StationsTreeItem>

    fun reinitialize(stations: List<Station>)
}