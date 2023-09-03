package com.masyanolchik.grandtheftradio2.stations

import com.masyanolchik.grandtheftradio2.domain.Result
import com.masyanolchik.grandtheftradio2.stationstree.StationsTreeItem
import kotlinx.coroutines.flow.Flow

interface StationContract {
    interface View {
        fun showLoadingProgress()

        fun hideLoadingProgress()

        fun showErrorScreen()

        fun updateList(listItems: List<StationsTreeItem>)
    }

    interface Presenter {
        fun prepareItemsForEra(eraName: String)

        fun setView(view: View)

        fun onDetach()
    }

    interface Model {
        fun getItemsForEra(eraName: String): Flow<Result<List<StationsTreeItem>>>
    }
}