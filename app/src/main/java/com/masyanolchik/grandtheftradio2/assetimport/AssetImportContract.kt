package com.masyanolchik.grandtheftradio2.assetimport

import com.masyanolchik.grandtheftradio2.domain.Result
import kotlinx.coroutines.flow.Flow

interface AssetImportContract {
    interface View {
        fun getString(resId: Int): String

        fun getString(resId: Int, vararg formatArgs: Any): String

        fun showImportResultStatus(status: String)
    }

    interface Presenter {
        fun processImportedJsonString(serializedString: String)

        fun setView(view: AssetImportContract.View)

        fun onDetach()
    }

    interface Model {
        suspend fun buildMediaTreeFromStationsList(serializedString: String): Flow<Result<Nothing>>
    }
}