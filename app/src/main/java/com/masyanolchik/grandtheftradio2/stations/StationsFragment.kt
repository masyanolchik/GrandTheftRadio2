package com.masyanolchik.grandtheftradio2.stations

import android.content.ComponentName
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.masyanolchik.grandtheftradio2.PlaybackService
import com.masyanolchik.grandtheftradio2.R
import com.masyanolchik.grandtheftradio2.domain.Station
import com.masyanolchik.grandtheftradio2.stationstree.StationsTreeItem
import org.koin.android.scope.createScope
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.inject
import org.koin.core.scope.Scope
import java.lang.RuntimeException

/**
 * A [Fragment] that displays stations for the given era.
 * Use the [StationsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class StationsFragment : Fragment(), StationContract.View, KoinScopeComponent {
    override val scope: Scope by lazy { createScope(this) }

    private val stationPresenter: StationContract.Presenter by inject()

    private lateinit var errorTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var stationAdapter: StationAdapter

    private lateinit var controllerFuture: ListenableFuture<MediaController>
    private val controller: MediaController?
        get() = if (controllerFuture.isDone) controllerFuture.get() else null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stations, container, false)
    }

    private fun getSpanSizeLookup(adapter: StationAdapter, position: Int): Int {
        return when(adapter.getItemViewType(position)) {
            StationAdapter.GAME_VIEW_TYPE -> 2
            else -> 1
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeController()
        val eraName = arguments?.getString("eraName") ?: ""
        progressBar = view.findViewById(R.id.progress_bar)
        errorTextView = view.findViewById(R.id.error_text)
        recyclerView = view.findViewById(R.id.stations_list)

        stationAdapter = StationAdapter(this::onStationTileClick,this::onTrailingTileIconClick)
        recyclerView.apply {
            recyclerView.layoutManager =
                GridLayoutManager(
                    context,
                    2,
                    GridLayoutManager.VERTICAL,
                    false
                )
                    .apply {
                        spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                            override fun getSpanSize(position: Int) =
                                getSpanSizeLookup(stationAdapter, position)
                        }
                    }
            adapter = stationAdapter
        }
        stationPresenter.setView(this)
        stationPresenter.prepareItemsForEra(eraName)
    }

    private fun onStationTileClick(station: Station) {
        controller?.clearMediaItems()
        controller?.addMediaItems(station.songs.map { it.toMediaItem() })
        controller?.play()
    }

    private fun onTrailingTileIconClick(station: Station) {
        stationPresenter.updateStation(station.apply {
            favorite = !favorite
        })
    }

    private fun initializeController() {
        val context = requireContext()
        controllerFuture =
            MediaController.Builder(
                context,
                SessionToken(context, ComponentName(context, PlaybackService::class.java))
            ).buildAsync()
    }



    override fun showLoadingProgress() {
        requireActivity().runOnUiThread {
            errorTextView.isVisible = false
            recyclerView.isVisible = false
            progressBar.isVisible = true
        }

    }

    override fun hideLoadingProgress() {
        requireActivity().runOnUiThread {
            errorTextView.isVisible = false
            recyclerView.isVisible = true
            progressBar.isVisible = false
        }

    }

    override fun showErrorScreen() {
        requireActivity().runOnUiThread {
            errorTextView.isVisible = true
            recyclerView.isVisible = false
            progressBar.isVisible = false
        }

    }

    override fun updateList(listItems: List<StationsTreeItem>) {
        requireActivity().runOnUiThread {
            stationAdapter.submitList(listItems)
            stationAdapter.notifyDataSetChanged()
        }
    }

    override fun onDetach() {
        stationPresenter.onDetach()
        super.onDetach()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment.
         *
         * @return A new instance of fragment StationsFragment.
         */
        @JvmStatic
        fun newInstance() = StationsFragment()
    }
}