package com.masyanolchik.grandtheftradio2.stationstree.repository

import com.masyanolchik.grandtheftradio2.domain.Station
import kotlinx.coroutines.flow.Flow
import com.masyanolchik.grandtheftradio2.domain.Result

interface StationsRepository {
    fun saveStations(stations: List<Station>): Flow<Result<Nothing>>
    fun nukeDatabase(): Flow<Result<Nothing>>
    fun getAllStations(): Flow<Result<List<Station>>>
}