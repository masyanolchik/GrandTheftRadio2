package com.masyanolchik.grandtheftradio2.stations

import com.masyanolchik.grandtheftradio2.domain.Result
import com.masyanolchik.grandtheftradio2.stationstree.StationsTreeItem
import kotlinx.coroutines.flow.Flow

interface StationContract {
    interface View {
        fun updateList(listItems: List<StationsTreeItem>)
    }

    interface Presenter {
        fun prepareItemsForEra(string: String)
    }

    interface Model {
        fun getItemsForEra(eraName: String): Flow<Result<List<StationsTreeItem>>>
    }
}