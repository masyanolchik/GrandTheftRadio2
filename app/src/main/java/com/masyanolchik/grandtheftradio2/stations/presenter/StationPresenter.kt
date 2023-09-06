package com.masyanolchik.grandtheftradio2.stations.presenter

import com.masyanolchik.grandtheftradio2.domain.Result
import com.masyanolchik.grandtheftradio2.domain.Song
import com.masyanolchik.grandtheftradio2.domain.Station
import com.masyanolchik.grandtheftradio2.stations.StationContract
import com.masyanolchik.grandtheftradio2.stationstree.StationsTreeItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class StationPresenter(
    private var stationContractModel: StationContract.Model,
    private val coroutineScope: CoroutineScope,
    private val uiDispatcher: CoroutineDispatcher
): StationContract.Presenter {
    private var stationContractView: StationContract.View? = null
    private var songsForStationJob: Job? = null

    override fun prepareItemsForEra(eraName: String) {
        stationContractView?.showLoadingProgress()
        coroutineScope.launch {
            stationContractModel
                .getItemsForEra(eraName)
                .flowOn(uiDispatcher)
                .collectLatest {
                    stationContractView?.hideLoadingProgress()
                    when(it) {
                        is Result.Success -> stationContractView?.updateList(it.data)
                        else -> stationContractView?.showErrorScreen()
                    }
                }
        }
    }

    override fun updateStation(station: Station) {
        coroutineScope.launch {
            stationContractModel.updateStation(station)
            stationContractModel.getItemsForEra(station.game.universe)
                .flowOn(uiDispatcher)
                .collectLatest {
                    stationContractView?.hideLoadingProgress()
                    when(it) {
                        is Result.Success -> stationContractView?.updateList(it.data)
                        else -> stationContractView?.showErrorScreen()
                    }
                }
        }
    }

    override fun setView(view: StationContract.View) {
        stationContractView = view
    }

    override fun prepareStationSongs(station: Station) {
        songsForStationJob?.cancel()
        songsForStationJob = coroutineScope.launch {
            val (songs, offset) = station.getSongsListWithCurrentAtTheTopAndSeekPosition(System.currentTimeMillis())
            stationContractView?.playMediaItems(
                songs.map {
                   it.toMediaItem()
                       .buildUpon()
                       .setUri(Song.getSongCorrectLink(it))
                       .build()
                },
                offset
            )
        }
    }

    override fun onDetach() {
        stationContractView = null
    }
}