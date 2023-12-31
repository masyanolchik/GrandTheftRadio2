package com.masyanolchik.grandtheftradio2.stations.model

import com.masyanolchik.grandtheftradio2.domain.Game
import com.masyanolchik.grandtheftradio2.domain.Result
import com.masyanolchik.grandtheftradio2.domain.Station
import com.masyanolchik.grandtheftradio2.stations.StationContract
import com.masyanolchik.grandtheftradio2.stationstree.StationsTree
import com.masyanolchik.grandtheftradio2.stationstree.StationsTreeItem
import com.masyanolchik.grandtheftradio2.stationstree.StationsTreeItem.Companion.ERA_ID
import com.masyanolchik.grandtheftradio2.stationstree.StationsTreeItem.Companion.GAME_PREFIX
import com.masyanolchik.grandtheftradio2.stationstree.StationsTreeItem.Companion.STATION_PREFIX
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.Exception

class StationModel(private val stationsTree: StationsTree): StationContract.Model {
    override fun getItemsForEra(eraName: String): Flow<Result<List<StationsTreeItem>>> {
        return flow {
            val gamesForGivenEra = stationsTree.getChildren(ERA_ID+eraName).filterIsInstance<Game>()
            val finalItems = buildList {
                gamesForGivenEra.sortedBy { it.id }.forEach {game ->
                    val stationsForGame = stationsTree.getChildren(GAME_PREFIX+game.id).filter {
                        it.toMediaItem().mediaId.contains(STATION_PREFIX)
                    }
                    if(stationsForGame.isNotEmpty()) {
                        add(game)
                        addAll(stationsForGame)
                    }
                }
            }
            if(finalItems.isNotEmpty()) {
                emit(Result.Success(finalItems))
            } else {
                emit(
                    Result.Error(
                        Exception("Error! Station tree doesn't have children for the ${ERA_ID+eraName}")
                    )
                )
            }
        }
    }

    override suspend fun updateStation(station: Station) {
       stationsTree.updateStation(station)
    }
}