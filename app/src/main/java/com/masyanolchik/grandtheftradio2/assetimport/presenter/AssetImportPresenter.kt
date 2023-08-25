package com.masyanolchik.grandtheftradio2.assetimport.presenter

import com.masyanolchik.grandtheftradio2.R
import com.masyanolchik.grandtheftradio2.assetimport.AssetImportContract
import com.masyanolchik.grandtheftradio2.domain.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class AssetImportPresenter constructor(
    private val assetImportContractModel: AssetImportContract.Model,
    private val coroutineScope: CoroutineScope,
) : AssetImportContract.Presenter {
    private var assetImportContractView: AssetImportContract.View? = null

    override fun processImportedJsonString(serializedString: String) {
        coroutineScope.launch {
            assetImportContractModel
                .buildMediaTreeFromStationsList(serializedString)
                .flowOn(Dispatchers.Main)
                .collectLatest { result ->
                    when(result) {
                        is Result.Completed -> {
                            assetImportContractView?.showImportResultStatus(
                                assetImportContractView?.getString(
                                    R.string.import_tree_success
                                )?: "")
                        }
                        else -> {
                            if(result is Result.Error) {
                                assetImportContractView?.showImportResultStatus(
                                    assetImportContractView?.getString(
                                        R.string.import_tree_error,
                                        result.throwable.toString()
                                    )?: ""
                                )
                            }
                        }
                    }
                }
        }
    }

    override fun setView(view: AssetImportContract.View) {
        assetImportContractView = view
    }

    override fun onDetach() {
        assetImportContractView = null
        /*assetImportContractView = null
        appContext = null*/
    }
}