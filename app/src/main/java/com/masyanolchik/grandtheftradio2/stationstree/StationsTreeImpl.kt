package com.masyanolchik.grandtheftradio2.stationstree

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.core.graphics.drawable.toBitmap
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MediaMetadata.PICTURE_TYPE_FILE_ICON
import com.google.common.collect.ImmutableList
import com.masyanolchik.grandtheftradio2.R
import com.masyanolchik.grandtheftradio2.domain.Station
import com.masyanolchik.grandtheftradio2.stationstree.StationsTreeItem.Companion.ERA_ID
import com.masyanolchik.grandtheftradio2.stationstree.StationsTreeItem.Companion.ROOT_ID
import com.masyanolchik.grandtheftradio2.stationstree.repository.StationsRepository
import kotlinx.coroutines.flow.collectLatest
import com.masyanolchik.grandtheftradio2.domain.Result
import com.masyanolchik.grandtheftradio2.stationstree.StationsTreeItem.Companion.GAME_PREFIX
import com.masyanolchik.grandtheftradio2.stationstree.StationsTreeItem.Companion.SONG_PREFIX
import com.masyanolchik.grandtheftradio2.stationstree.StationsTreeItem.Companion.STATION_PREFIX
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class StationsTreeImpl(
    private val repository: StationsRepository,
    private val coroutineScope: CoroutineScope,
    private val appContext: Context
): StationsTree {
    private var initializationJob: Job
    init {
        initializationJob = coroutineScope.launch {
            repository.getAllStations()
                .collectLatest{
                    if(it is Result.Success) {
                        initialize(it.data)
                    }
                }
        }

    }

    private var treeNodes: MutableMap<String, StationsTreeNode> = mutableMapOf()

    private inner class StationsTreeNode(val item: StationsTreeItem) {
        private val children: MutableList<StationsTreeItem> = ArrayList()

        fun addChild(childID: String) {
            treeNodes[childID]?.item?.let { this.children.add(it) }
        }

        fun getChildren(): List<StationsTreeItem> {
            return ImmutableList.copyOf(children)
        }

    }

    override suspend fun getItem(id: String): StationsTreeItem? {
        initializationJob.join()
        return treeNodes[id]?.item
    }

    override suspend fun getRoot(): StationsTreeItem? {
        initializationJob.join()
        return treeNodes[ROOT_ID]?.item
    }

    override suspend fun getChildren(id: String): List<StationsTreeItem> {
        initializationJob.join()
        return treeNodes[id]?.getChildren()?: emptyList()
    }

    override fun reinitialize(stations: List<Station>) {
        initializationJob.cancel()
        repository.nukeDatabase()
        repository.saveStations(stations)
        initializationJob = coroutineScope.launch {
            repository.getAllStations()
                .collectLatest{
                    if(it is Result.Success) {
                        initialize(it.data)
                    }
                }
        }

    }

   private fun initialize(stations: List<Station>) {
        treeNodes[ROOT_ID] =
            StationsTreeNode(object: StationsTreeItem{
                override fun toMediaItem(): MediaItem {
                    return MediaItem.Builder()
                        .setMediaMetadata(
                            MediaMetadata.Builder()
                                .setTitle(ROOT_TITLE)
                                .setIsPlayable(false)
                                .setIsBrowsable(true)
                                .setMediaType(MediaMetadata.MEDIA_TYPE_FOLDER_MIXED)
                                .build()
                        )
                        .setMediaId(ROOT_ID)
                        .build()
                }
            })

        val stationsByUniverse = stations.groupBy { it.game.universe }
        val universes = stationsByUniverse.keys.sorted()
        universes.forEach {
            val mediaId = ERA_ID + it
            val artworkResource = when(it) {
                "2D" -> R.drawable.twod
                "3D" -> R.drawable.threed
                "HD" -> R.drawable.hd
                else -> R.drawable.favorite
            }
            val bitmap = appContext.resources.getDrawable(artworkResource).toBitmap()
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val bitmapData = stream.toByteArray()
            stream.close()
            treeNodes[mediaId] =
                StationsTreeNode(
                    object: StationsTreeItem {
                        override fun toMediaItem(): MediaItem {
                            val mediaMetadata = MediaMetadata.Builder()
                                .setTitle(it)
                                .setIsPlayable(false)
                                .setIsBrowsable(true)
                                .setArtworkData(bitmapData,PICTURE_TYPE_FILE_ICON)
                                .setMediaType(MediaMetadata.MEDIA_TYPE_FOLDER_ALBUMS)

                            return MediaItem.Builder()
                                .setMediaMetadata(mediaMetadata.build())
                                .setMediaId(mediaId)
                                .build()
                        }
                    }
                )
            treeNodes[ROOT_ID]?.addChild(mediaId)
        }

       stationsByUniverse.keys.forEach {
           val nullableStationList = stationsByUniverse[it]
           nullableStationList?.let {stations ->
               stations.map { station -> station.game }.toSet().forEach { game ->
                   val parentId = ERA_ID + game.universe
                   val gameId = GAME_PREFIX + game.id
                   treeNodes[gameId] = StationsTreeNode(game)
                   treeNodes[parentId]?.addChild(gameId)
               }
           }
       }

       stations.groupBy { it.game }.forEach { gameStationsPair ->
           gameStationsPair.value.forEach {
               val parentId = GAME_PREFIX+it.game.id
               val stationId = STATION_PREFIX + it.id
               treeNodes[stationId] = StationsTreeNode(it)
               it.songs.forEach { song ->
                   val songId = SONG_PREFIX + song.id
                   treeNodes[songId] = StationsTreeNode(song)
                   treeNodes[stationId]?.addChild(songId)
               }
               treeNodes[parentId]?.addChild(stationId)
           }
       }
    }

    companion object {
        private const val ROOT_TITLE = "Root folder"
    }
}