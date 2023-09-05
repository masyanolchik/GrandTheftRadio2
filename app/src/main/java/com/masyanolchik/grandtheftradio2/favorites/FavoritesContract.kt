package com.masyanolchik.grandtheftradio2.favorites

import com.masyanolchik.grandtheftradio2.domain.Result
import com.masyanolchik.grandtheftradio2.domain.Station
import com.masyanolchik.grandtheftradio2.stationstree.StationsTreeItem
import kotlinx.coroutines.flow.Flow

interface FavoritesContract {
    interface View {
        fun showLoadingProgress()

        fun hideLoadingProgress()

        fun showErrorScreen()

        fun updateList(listItems: List<StationsTreeItem>)
    }

    interface Presenter {
        fun prepareFavoriteStations()

        fun updateStation(station: Station)

        fun setView(view: View)

        fun onDetach()
    }

    interface Model {
        fun getFavoriteItems(): Flow<Result<List<StationsTreeItem>>>

        suspend fun updateStation(station: Station)
    }
}