package com.masyanolchik.grandtheftradio2.favorites.presenter

import com.masyanolchik.grandtheftradio2.domain.Station
import com.masyanolchik.grandtheftradio2.favorites.FavoritesContract
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import com.masyanolchik.grandtheftradio2.domain.Result
import com.masyanolchik.grandtheftradio2.domain.Song
import kotlinx.coroutines.Job

class FavoritesPresenter(
    private var favoritesContractModel: FavoritesContract.Model,
    private val coroutineScope: CoroutineScope,
    private val uiDispatcher: CoroutineDispatcher
): FavoritesContract.Presenter {
    private var favoritesContractView: FavoritesContract.View? = null
    private var songsForStationJob: Job? = null

    override fun prepareFavoriteStations() {
        favoritesContractView?.showLoadingProgress()
        coroutineScope.launch {
            favoritesContractModel
                .getFavoriteItems()
                .flowOn(uiDispatcher)
                .collectLatest {
                    favoritesContractView?.hideLoadingProgress()
                    when(it) {
                        is Result.Success -> favoritesContractView?.updateList(it.data)
                        else -> favoritesContractView?.showErrorScreen()
                    }
                }
        }
    }

    override fun updateStation(station: Station) {
        coroutineScope.launch {
            favoritesContractModel.updateStation(station)
            favoritesContractModel.getFavoriteItems()
                .flowOn(uiDispatcher)
                .collectLatest {
                    favoritesContractView?.hideLoadingProgress()
                    when(it) {
                        is Result.Success -> favoritesContractView?.updateList(it.data)
                        else -> favoritesContractView?.showErrorScreen()
                    }
                }
        }
    }

    override fun setView(view: FavoritesContract.View) {
        favoritesContractView = view
    }

    override fun prepareStationSongs(station: Station) {
        songsForStationJob?.cancel()
        songsForStationJob = coroutineScope.launch {
            val (songs, offset) = station.getSongsListWithCurrentAtTheTopAndSeekPosition(System.currentTimeMillis())
            favoritesContractView?.playMediaItems(
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
        favoritesContractView = null
    }
}