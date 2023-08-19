package com.masyanolchik.grandtheftradio2.assetimport.presenter

import com.masyanolchik.grandtheftradio2.R
import com.masyanolchik.grandtheftradio2.assetimport.AssetImportContract
import com.masyanolchik.grandtheftradio2.domain.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AssetImportPresenter constructor(
    private var assetImportContractView: AssetImportContract.View?,
    private val assetImportContractModel: AssetImportContract.Model,
    private val coroutineScope: CoroutineScope
) : AssetImportContract.Presenter {

    override fun processImportedJsonString(serializedString: String) {
        assetImportContractView?.showImportProgress()
        coroutineScope.launch {
            assetImportContractModel
                .buildMediaTreeFromStationsList(serializedString)
                .collectLatest { result ->
                    when(result) {
                        is Result.Completed -> {
                            assetImportContractView?.hideImportProgress(
                                assetImportContractView?.getString(
                                    R.string.import_tree_success,
                                )?: "")
                        }
                        is Result.Error -> {
                            assetImportContractView?.hideImportProgress(
                                assetImportContractView?.getString(R.string.import_tree_error, result.throwable.toString())?: ""
                            )
                        }
                        else -> throw IllegalStateException("This shouldn't have happened")
                    }
                }
        }
    }

    override fun onDestroy() {
        assetImportContractView = null
    }
}