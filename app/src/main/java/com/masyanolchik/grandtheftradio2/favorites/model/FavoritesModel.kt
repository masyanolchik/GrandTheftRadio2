package com.masyanolchik.grandtheftradio2.favorites.model

import com.masyanolchik.grandtheftradio2.domain.Game
import com.masyanolchik.grandtheftradio2.domain.Result
import com.masyanolchik.grandtheftradio2.domain.Station
import com.masyanolchik.grandtheftradio2.favorites.FavoritesContract
import com.masyanolchik.grandtheftradio2.stationstree.StationsTree
import com.masyanolchik.grandtheftradio2.stationstree.StationsTreeImpl
import com.masyanolchik.grandtheftradio2.stationstree.StationsTreeItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception

class FavoritesModel(private val stationsTree: StationsTree): FavoritesContract.Model {
    override fun getFavoriteItems(): Flow<Result<List<StationsTreeItem>>> {
        return flow {
            val favoriteItems = stationsTree.getChildren(
                StationsTreeItem.ERA_ID + StationsTreeImpl.FAVORITE_TAB_TITLE
            )
            if(favoriteItems.isNotEmpty()) {
                emit(Result.Success(favoriteItems))
            } else {
                emit(
                    Result.Error(
                        Exception("Error! Station tree doesn't have children for the ${StationsTreeItem.ERA_ID +StationsTreeImpl.FAVORITE_TAB_TITLE}")
                    )
                )
            }
        }
    }

    override suspend fun updateStation(station: Station) {
        stationsTree.updateStation(station)
    }
}