package com.masyanolchik.grandtheftradio2

import android.app.Application
import androidx.room.Room
import com.masyanolchik.grandtheftradio2.assetimport.AssetImportContract
import com.masyanolchik.grandtheftradio2.assetimport.ImportFragment
import com.masyanolchik.grandtheftradio2.assetimport.model.AssetImportModel
import com.masyanolchik.grandtheftradio2.assetimport.presenter.AssetImportPresenter
import com.masyanolchik.grandtheftradio2.db.LocalDatabase
import com.masyanolchik.grandtheftradio2.db.station.StationsDao
import com.masyanolchik.grandtheftradio2.stations.StationContract
import com.masyanolchik.grandtheftradio2.stations.StationsFragment
import com.masyanolchik.grandtheftradio2.stations.model.StationModel
import com.masyanolchik.grandtheftradio2.stations.presenter.StationPresenter
import com.masyanolchik.grandtheftradio2.stationstree.StationsTree
import com.masyanolchik.grandtheftradio2.stationstree.StationsTreeImpl
import com.masyanolchik.grandtheftradio2.stationstree.repository.StationsRepository
import com.masyanolchik.grandtheftradio2.stationstree.repository.StationsRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.binds
import org.koin.dsl.module

class RadioApplication : Application() {

    private val assetImportModule = module {
        single<AssetImportContract.Model> { AssetImportModel(get()) }
        single { _ ->
            AssetImportPresenter(
                get(),
                CoroutineScope(Dispatchers.IO),
                Dispatchers.Main
            )
        } binds arrayOf(AssetImportContract.Presenter::class)
    }

    private val importFragmentScopeModule = module {
        scope<ImportFragment> {
            scoped {
                get<ImportFragment>().requireActivity().activityResultRegistry
            }
        }
    }

    private val stationFragmentScopeModule = module {
        scope<StationsFragment> {
            scoped {
                StationPresenter(
                    StationModel(get()),
                    CoroutineScope(Dispatchers.IO),
                    Dispatchers.Main
                ) as StationContract.Presenter
            }
        }
    }

    private val stationsTreeModule = module {
        single {
            Room.databaseBuilder(get(), LocalDatabase::class.java, LocalDatabase.DB_NAME).build()
        } binds arrayOf(LocalDatabase::class)
        single<StationsDao> {
            val database = get<LocalDatabase>()
            database.stationsDao()
        }
        factory<StationsRepository> { StationsRepositoryImpl(get()) }
        single<StationsTree> { StationsTreeImpl(get(), CoroutineScope(Dispatchers.IO), get()) }
    }

    private val playbackServiceModule = module {
        factory { CoroutineScope(Dispatchers.IO) }
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@RadioApplication)
            modules(
                assetImportModule,
                stationsTreeModule,
                importFragmentScopeModule,
                stationFragmentScopeModule,
                playbackServiceModule
            )
        }
    }
}