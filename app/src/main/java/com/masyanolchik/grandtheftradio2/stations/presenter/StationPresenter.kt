package com.masyanolchik.grandtheftradio2.stations.presenter

import com.masyanolchik.grandtheftradio2.domain.Result
import com.masyanolchik.grandtheftradio2.stations.StationContract
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StationPresenter(
    private var stationContractModel: StationContract.Model,
    private val coroutineScope: CoroutineScope,
    private val uiDispatcher: CoroutineDispatcher
): StationContract.Presenter {
    private var stationContractView: StationContract.View? = null
    override fun prepareItemsForEra(eraName: String) {
        coroutineScope.launch {
            withContext(uiDispatcher) {
                stationContractView?.showLoadingProgress()
            }
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

    override fun setView(view: StationContract.View) {
        stationContractView = view
    }

    override fun onDetach() {
        stationContractView = null
    }
}