package com.masyanolchik.grandtheftradio2.assetimport.model

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.masyanolchik.grandtheftradio2.assetimport.AssetImportContract
import com.masyanolchik.grandtheftradio2.domain.Result
import com.masyanolchik.grandtheftradio2.domain.Station
import com.masyanolchik.grandtheftradio2.stationstree.StationsTree
import com.masyanolchik.grandtheftradio2.stationstree.repository.StationsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.lang.Exception

class AssetImportModel(private val stationsTree: StationsTree): AssetImportContract.Model {
    override suspend fun buildMediaTreeFromStationsList(serializedString: String): Flow<Result<Nothing>> {
        val listStationType = object : TypeToken<List<Station>>() {}.type
        val stationsList = Gson().fromJson<List<Station>>(serializedString, listStationType)
        return try {
            stationsTree.reinitialize(stationsList)
            flowOf(Result.Completed())
        } catch (ex: Exception) {
            flowOf(Result.Error(ex))
        }
    }
}